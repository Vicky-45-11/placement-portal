package com.placement.web;

import com.placement.dao.*;
import com.placement.model.Job;
import com.placement.model.Student;
import com.placement.util.Config;
import com.placement.util.Web;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/student/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class StudentServlet extends BaseServlet {
    private final StudentDao students = new StudentDao();
    private final JobDao jobs = new JobDao();
    private final ApplicationDao applications = new ApplicationDao();
    private final InterviewDao interviews = new InterviewDao();

    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Student me = students.findById((Integer) req.getSession().getAttribute("studentId"));
        req.setAttribute("me", me);
        switch (action) {
            case "" -> dashboard(me, req, res);
            case "profile" -> { if (post) saveProfile(me, req, res); else render(req, res, "student/profile"); }
            case "resume" -> { if (post) uploadResume(me, req, res); else redirect(req, res, "/student/profile"); }
            case "jobs" -> listJobs(me, req, res);
            case "job" -> showJob(me, req, res);
            case "apply" -> { if (post) apply(me, req, res); else redirect(req, res, "/student/jobs"); }
            case "applications" -> {
                req.setAttribute("applications", applications.listByStudent(me.getId()));
                render(req, res, "student/applications");
            }
            case "withdraw" -> {
                if (post) {
                    applications.withdraw(Web.intParam(req, "id", 0), me.getId());
                    Web.flash(req, "Application withdrawn.");
                }
                redirect(req, res, "/student/applications");
            }
            default -> notFound(res);
        }
    }

    private void dashboard(Student me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setAttribute("counts", applications.countByStatusForStudent(me.getId()));
        req.setAttribute("upcoming", interviews.listUpcomingForStudent(me.getId()));
        req.setAttribute("announcements", new AnnouncementDao().latest(5));
        List<Job> open = annotate(me, jobs.listOpen());
        req.setAttribute("eligibleCount", open.stream().filter(j -> j.getIneligibleReason() == null && j.getApplicationStatus() == null).count());
        render(req, res, "student/dashboard");
    }

    /** Marks each job with this student's application status and eligibility. */
    private List<Job> annotate(Student me, List<Job> list) throws Exception {
        Map<Integer, String> applied = applications.statusByJobForStudent(me.getId());
        for (Job j : list) {
            j.setApplicationStatus(applied.get(j.getId()));
            j.setIneligibleReason(me.isPlaced() ? "You are already placed" : j.checkEligibility(me));
        }
        return list;
    }

    private void listJobs(Student me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        List<Job> list = annotate(me, jobs.listOpen());
        if ("eligible".equals(req.getParameter("filter"))) {
            list.removeIf(j -> j.getIneligibleReason() != null);
        }
        req.setAttribute("jobs", list);
        render(req, res, "student/jobs");
    }

    private void showJob(Student me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Job j = jobs.findById(Web.intParam(req, "id", 0));
        if (j == null) { notFound(res); return; }
        annotate(me, List.of(j));
        req.setAttribute("job", j);
        render(req, res, "student/job");
    }

    private void apply(Student me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        int jobId = Web.intParam(req, "jobId", 0);
        Job j = jobs.findById(jobId);
        if (j == null) { notFound(res); return; }
        String reason = me.isPlaced() ? "You are already placed" : j.checkEligibility(me);
        if (reason != null) {
            Web.flashError(req, "You cannot apply: " + reason + ".");
        } else if (!me.isHasResume()) {
            Web.flashError(req, "Upload your resume in your profile before applying.");
        } else {
            try {
                applications.apply(jobId, me.getId());
                Web.flash(req, "Applied to " + j.getTitle() + " at " + j.getCompanyName() + ".");
            } catch (SQLIntegrityConstraintViolationException e) {
                Web.flashError(req, "You have already applied to this job.");
            }
        }
        redirect(req, res, "/student/job?id=" + jobId);
    }

    private void saveProfile(Student me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        String name = Web.str(req, "name");
        String dept = Web.str(req, "department");
        BigDecimal cgpa = Web.decimal(req, "cgpa");
        String error = null;
        if (name.isEmpty()) error = "Name is required.";
        else if (!DEPARTMENTS.contains(dept)) error = "Choose your department.";
        else if (cgpa == null || cgpa.signum() < 0 || cgpa.compareTo(BigDecimal.TEN) > 0) error = "CGPA must be between 0 and 10.";
        if (error != null) {
            Web.flashError(req, error);
            redirect(req, res, "/student/profile");
            return;
        }
        me.setDepartment(dept);
        me.setBatchYear(Web.intParam(req, "batchYear", me.getBatchYear()));
        me.setCgpa(cgpa);
        me.setBacklogs(Math.max(0, Web.intParam(req, "backlogs", 0)));
        me.setPhone(Web.str(req, "phone"));
        me.setSkills(Web.str(req, "skills"));
        students.updateProfile(me);
        new UserDao().updateName(me.getUserId(), name);
        req.getSession().setAttribute("userName", name);
        Web.flash(req, "Profile saved.");
        redirect(req, res, "/student/profile");
    }

    private void uploadResume(Student me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Part part;
        try {
            part = req.getPart("resume");
        } catch (IllegalStateException tooBig) {
            Web.flashError(req, "Resume must be 2 MB or smaller.");
            redirect(req, res, "/student/profile");
            return;
        }
        if (part == null || part.getSize() == 0) {
            Web.flashError(req, "Choose a PDF file to upload.");
            redirect(req, res, "/student/profile");
            return;
        }
        byte[] head = new byte[5];
        try (InputStream in = part.getInputStream()) {
            int n = in.readNBytes(head, 0, 5);
            if (n < 5 || !new String(head).equals("%PDF-")) {
                Web.flashError(req, "Only PDF resumes are accepted.");
                redirect(req, res, "/student/profile");
                return;
            }
        }
        Path dir = Path.of(Config.uploadDir()).toAbsolutePath();
        Files.createDirectories(dir);
        String fileName = "resume-" + me.getId() + "-" + UUID.randomUUID() + ".pdf";
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        }
        if (me.isHasResume()) Files.deleteIfExists(dir.resolve(me.getResumeFile()));
        students.updateResume(me.getId(), fileName);
        Web.flash(req, "Resume uploaded.");
        redirect(req, res, "/student/profile");
    }
}
