package com.placement.web;

import com.placement.model.Job;
import com.placement.util.Web;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Shared plumbing: routes /area/action to handle(action, ...), renders JSPs from
 * /WEB-INF/views and turns database errors into a friendly error page.
 */
public abstract class BaseServlet extends HttpServlet {
    public static final List<String> DEPARTMENTS = List.of("CSE", "IT", "ECE", "EEE", "MECH", "CIVIL");

    protected abstract void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res)
            throws Exception;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        dispatch(false, req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        dispatch(true, req, res);
    }

    private void dispatch(boolean post, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getPathInfo();
        String action = (path == null || path.equals("/")) ? "" : path.substring(1);
        req.setAttribute("departments", DEPARTMENTS);
        try {
            handle(action, post, req, res);
        } catch (SQLException e) {
            log("Database error on " + req.getRequestURI(), e);
            req.setAttribute("errorMessage", "Database error: " + e.getMessage()
                    + ". Check that MySQL is running and db.properties is correct.");
            render(req, res, "common/error");
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void render(HttpServletRequest req, HttpServletResponse res, String view) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, res);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
        res.sendRedirect(req.getContextPath() + path);
    }

    protected void notFound(HttpServletResponse res) throws IOException {
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /** Reads the job form shared by admins and companies. Returns an error message or null. */
    protected String readJobForm(HttpServletRequest req, Job j) {
        j.setTitle(Web.str(req, "title"));
        j.setDescription(Web.str(req, "description"));
        j.setJobType("INTERNSHIP".equals(Web.str(req, "jobType")) ? "INTERNSHIP" : "FULL_TIME");
        j.setCtcLpa(Web.decimal(req, "ctcLpa"));
        j.setLocation(Web.str(req, "location"));
        BigDecimal minCgpa = Web.decimal(req, "minCgpa");
        j.setMinCgpa(minCgpa == null ? BigDecimal.ZERO : minCgpa);
        j.setMaxBacklogs(Math.max(0, Web.intParam(req, "maxBacklogs", 0)));
        String[] depts = req.getParameterValues("departments");
        j.setEligibleDepartments(depts == null || depts.length == 0 || depts.length == DEPARTMENTS.size()
                ? "ALL" : String.join(",", depts));
        j.setDeadline(Web.date(req, "deadline"));
        j.setRequiredSkills(Web.strOrNull(req, "requiredSkills"));
        j.setStatus("CLOSED".equals(Web.str(req, "status")) ? "CLOSED" : "OPEN");

        if (j.getTitle().isEmpty()) return "Job title is required.";
        if (j.getCtcLpa() == null || j.getCtcLpa().signum() < 0) return "Enter a valid package (LPA).";
        if (j.getMinCgpa().compareTo(BigDecimal.TEN) > 0) return "Minimum CGPA must be between 0 and 10.";
        if (j.getDeadline() == null) return "Choose a deadline.";
        if (j.getId() == 0 && j.getDeadline().isBefore(LocalDate.now())) return "Deadline cannot be in the past.";
        return null;
    }

    /** True for status updates sent by the drag-and-drop board (fetch) instead of a normal form post. */
    protected static boolean isAjax(HttpServletRequest req) {
        return "fetch".equals(req.getHeader("X-Requested-With"));
    }

    /** Scores every applicant of a drive against its required skills. */
    protected static void scoreApplicants(Job job, java.util.List<com.placement.model.Application> apps) {
        for (com.placement.model.Application a : apps) {
            boolean eligible = a.getCgpa() != null && a.getCgpa().compareTo(job.getMinCgpa()) >= 0
                    && a.getBacklogs() <= job.getMaxBacklogs() && job.isDepartmentEligible(a.getDepartment());
            a.setMatch(com.placement.model.SkillMatch.of(job.getRequiredSkills(), a.getSkills(), a.getCgpa(), job.getMinCgpa(), eligible));
        }
    }

    protected static boolean isValidStatus(String s) {
        return com.placement.model.Application.STATUSES.contains(s);
    }

    protected void scheduleInterview(com.placement.dao.InterviewDao interviews, com.placement.model.Application a, HttpServletRequest req) throws Exception {
        com.placement.model.Interview i = new com.placement.model.Interview();
        i.setApplicationId(a.getId());
        i.setRoundName(Web.str(req, "roundName"));
        i.setScheduledAt(Web.dateTime(req, "scheduledAt"));
        i.setMode("ONLINE".equals(Web.str(req, "mode")) ? "ONLINE" : "OFFLINE");
        i.setVenue(Web.str(req, "venue"));
        i.setNotes(Web.strOrNull(req, "notes"));
        if (i.getRoundName().isEmpty() || i.getScheduledAt() == null) {
            Web.flashError(req, "Round name and date/time are required.");
            return;
        }
        if (i.getScheduledAt().isBefore(java.time.LocalDateTime.now())) {
            Web.flashError(req, "Interview time must be in the future.");
            return;
        }
        interviews.schedule(i);
        com.placement.util.Notify.student(a.getId(), i.getRoundName() + " round for " + a.getJobTitle() + " at " + a.getCompanyName()
                + " on " + i.getScheduledAtText() + " (" + i.getMode().toLowerCase() + ").", "/student/");
        Web.flash(req, i.getRoundName() + " round scheduled for " + a.getStudentName() + ".");
    }

}
