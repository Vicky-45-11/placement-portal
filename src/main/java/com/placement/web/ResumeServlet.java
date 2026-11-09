package com.placement.web;

import com.placement.dao.ApplicationDao;
import com.placement.dao.StudentDao;
import com.placement.model.Application;
import com.placement.model.Student;
import com.placement.util.Config;
import com.placement.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Streams a student's resume PDF. Allowed for the student, any admin, and companies
 * the student has applied to.
 */
@WebServlet("/resume")
public class ResumeServlet extends BaseServlet {
    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Student s = new StudentDao().findById(Web.intParam(req, "student", 0));
        if (s == null || !s.isHasResume()) { notFound(res); return; }

        HttpSession session = req.getSession();
        String role = (String) session.getAttribute("role");
        boolean allowed = switch (role) {
            case "ADMIN" -> true;
            case "STUDENT" -> s.getId() == (Integer) session.getAttribute("studentId");
            case "COMPANY" -> {
                int companyId = (Integer) session.getAttribute("companyId");
                boolean applied = false;
                for (Application a : new ApplicationDao().listByStudent(s.getId())) {
                    if (a.getCompanyId() == companyId) { applied = true; break; }
                }
                yield applied;
            }
            default -> false;
        };
        if (!allowed) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Path file = Path.of(Config.uploadDir()).toAbsolutePath().resolve(s.getResumeFile()).normalize();
        if (!Files.exists(file)) { notFound(res); return; }
        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "inline; filename=\"" + s.getRollNo() + "-resume.pdf\"");
        res.setContentLengthLong(Files.size(file));
        Files.copy(file, res.getOutputStream());
    }
}
