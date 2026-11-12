package com.placement.web;

import com.placement.dao.CompanyDao;
import com.placement.dao.StudentDao;
import com.placement.dao.UserDao;
import com.placement.model.Company;
import com.placement.model.Student;
import com.placement.model.User;
import com.placement.util.PasswordUtil;
import com.placement.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.Year;
import java.util.regex.Pattern;

/** Login, logout, registration and password change. */
@WebServlet(urlPatterns = {"/login", "/logout", "/register", "/account"})
public class AuthServlet extends BaseServlet {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UserDao users = new UserDao();

    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        switch (req.getServletPath()) {
            case "/login" -> { if (post) login(req, res); else render(req, res, "common/login"); }
            case "/logout" -> logout(req, res);
            case "/register" -> { if (post) register(req, res); else render(req, res, "common/register"); }
            case "/account" -> { if (post) changePassword(req, res); else render(req, res, "common/account"); }
            default -> notFound(res);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String email = Web.str(req, "email").toLowerCase();
        String password = req.getParameter("password");
        User u = users.findByEmail(email);
        if (u == null || !PasswordUtil.verify(password, u.getPasswordHash())) {
            req.setAttribute("error", "Wrong email or password.");
            req.setAttribute("email", email);
            render(req, res, "common/login");
            return;
        }
        if (!u.isActive()) {
            req.setAttribute("error", "Your account is waiting for approval from the placement cell.");
            req.setAttribute("email", email);
            render(req, res, "common/login");
            return;
        }
        // new session id on login prevents session fixation
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        HttpSession s = req.getSession(true);
        s.setAttribute("userId", u.getId());
        s.setAttribute("userName", u.getName());
        s.setAttribute("role", u.getRole());
        s.setMaxInactiveInterval(60 * 60);
        if ("STUDENT".equals(u.getRole())) {
            s.setAttribute("studentId", new StudentDao().findByUserId(u.getId()).getId());
        } else if ("COMPANY".equals(u.getRole())) {
            Company c = new CompanyDao().findByUserId(u.getId());
            s.setAttribute("companyId", c.getId());
            s.setAttribute("companyName", c.getName());
        }
        redirect(req, res, "/" + u.getRole().toLowerCase() + "/");
    }

    private void logout(HttpServletRequest req, HttpServletResponse res) throws Exception {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        req.getSession(true).setAttribute("flash", "You have been logged out.");
        redirect(req, res, "/login");
    }

    private void register(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String type = Web.str(req, "type");
        String name = Web.str(req, "name");
        String email = Web.str(req, "email").toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        req.setAttribute("type", type);

        String error = null;
        if (name.isEmpty()) error = "Name is required.";
        else if (!EMAIL.matcher(email).matches()) error = "Enter a valid email address.";
        else if (password.length() < 6) error = "Password must be at least 6 characters.";
        else if (!password.equals(req.getParameter("confirm"))) error = "Passwords do not match.";
        else if (users.emailExists(email)) error = "An account with this email already exists.";

        if (error == null && "company".equals(type)) {
            Company c = new Company();
            c.setName(Web.str(req, "companyName"));
            c.setIndustry(Web.str(req, "industry"));
            c.setWebsite(Web.str(req, "website"));
            c.setLocation(Web.str(req, "location"));
            c.setDescription(Web.str(req, "description"));
            if (c.getName().isEmpty()) error = "Company name is required.";
            if (error == null) {
                users.registerCompany(name, email, password, c);
                com.placement.util.Notify.admins(c.getName() + " registered as a recruiter and is waiting for your approval.", "/admin/companies");
                Web.flash(req, "Company registered. You can log in once the placement cell approves your account.");
                redirect(req, res, "/login");
                return;
            }
        } else if (error == null) {
            Student s = new Student();
            s.setRollNo(Web.str(req, "rollNo").toUpperCase());
            s.setDepartment(Web.str(req, "department"));
            s.setBatchYear(Web.intParam(req, "batchYear", Year.now().getValue()));
            BigDecimal cgpa = Web.decimal(req, "cgpa");
            s.setCgpa(cgpa == null ? BigDecimal.ZERO : cgpa);
            s.setBacklogs(Math.max(0, Web.intParam(req, "backlogs", 0)));
            s.setPhone(Web.str(req, "phone"));
            s.setSkills(Web.str(req, "skills"));
            if (s.getRollNo().isEmpty()) error = "Roll number is required.";
            else if (!DEPARTMENTS.contains(s.getDepartment())) error = "Choose your department.";
            else if (s.getCgpa().compareTo(BigDecimal.ZERO) < 0 || s.getCgpa().compareTo(BigDecimal.TEN) > 0) error = "CGPA must be between 0 and 10.";
            else if (new StudentDao().rollNoExists(s.getRollNo())) error = "This roll number is already registered.";
            if (error == null) {
                users.registerStudent(name, email, password, s);
                Web.flash(req, "Registration successful. Please log in.");
                redirect(req, res, "/login");
                return;
            }
        }
        req.setAttribute("error", error);
        render(req, res, "common/register");
    }

    private void changePassword(HttpServletRequest req, HttpServletResponse res) throws Exception {
        User u = users.findById(Web.currentUserId(req));
        String current = req.getParameter("current");
        String next = req.getParameter("password");
        String error = null;
        if (!PasswordUtil.verify(current, u.getPasswordHash())) error = "Current password is wrong.";
        else if (next == null || next.length() < 6) error = "New password must be at least 6 characters.";
        else if (!next.equals(req.getParameter("confirm"))) error = "Passwords do not match.";
        if (error != null) {
            req.setAttribute("error", error);
            render(req, res, "common/account");
            return;
        }
        users.updatePassword(u.getId(), next);
        Web.flash(req, "Password changed.");
        redirect(req, res, "/account");
    }
}
