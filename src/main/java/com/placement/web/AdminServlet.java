package com.placement.web;

import com.placement.dao.*;
import com.placement.model.Application;
import com.placement.model.Company;
import com.placement.model.Job;
import com.placement.model.Student;
import com.placement.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.time.LocalDate;

@WebServlet("/admin/*")
public class AdminServlet extends BaseServlet {
    private final StudentDao students = new StudentDao();
    private final CompanyDao companies = new CompanyDao();
    private final JobDao jobs = new JobDao();
    private final ApplicationDao applications = new ApplicationDao();
    private final InterviewDao interviews = new InterviewDao();
    private final AnnouncementDao announcements = new AnnouncementDao();
    private final ReportDao reports = new ReportDao();

    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        switch (action) {
            case "" -> {
                req.setAttribute("stats", reports.summary());
                req.setAttribute("byDept", reports.byDepartment());
                req.setAttribute("funnel", reports.statusCounts());
                req.setAttribute("recent", applications.listAll(null).stream().limit(8).toList());
                render(req, res, "admin/dashboard");
            }
            // ---------- students ----------
            case "students" -> {
                req.setAttribute("students", students.list(Web.str(req, "dept"), Web.str(req, "placed"), Web.str(req, "q")));
                render(req, res, "admin/students");
            }
            case "student" -> {
                Student s = students.findById(Web.intParam(req, "id", 0));
                if (s == null) { notFound(res); return; }
                req.setAttribute("student", s);
                req.setAttribute("applications", applications.listByStudent(s.getId()));
                render(req, res, "admin/student");
            }
            case "student/delete" -> {
                if (post) {
                    students.delete(Web.intParam(req, "id", 0));
                    Web.flash(req, "Student removed.");
                }
                redirect(req, res, "/admin/students");
            }
            // ---------- companies ----------
            case "companies" -> {
                req.setAttribute("companies", companies.listAll());
                render(req, res, "admin/companies");
            }
            case "company/toggle" -> {
                Company c = companies.findById(Web.intParam(req, "id", 0));
                if (post && c != null) {
                    new UserDao().setActive(c.getUserId(), !c.isActive());
                    Web.flash(req, c.getName() + (c.isActive() ? " disabled." : " approved."));
                }
                redirect(req, res, "/admin/companies");
            }
            // ---------- jobs ----------
            case "jobs" -> {
                req.setAttribute("jobs", jobs.listAll());
                render(req, res, "admin/jobs");
            }
            case "job/new" -> {
                Job j = new Job();
                j.setDeadline(LocalDate.now().plusDays(14));
                req.setAttribute("job", j);
                req.setAttribute("companies", companies.listAll());
                render(req, res, "common/job-form");
            }
            case "job/edit" -> {
                Job j = jobs.findById(Web.intParam(req, "id", 0));
                if (j == null) { notFound(res); return; }
                req.setAttribute("job", j);
                req.setAttribute("companies", companies.listAll());
                render(req, res, "common/job-form");
            }
            case "job/save" -> { if (post) saveJob(req, res); else redirect(req, res, "/admin/jobs"); }
            case "job/status" -> {
                Job j = jobs.findById(Web.intParam(req, "id", 0));
                if (post && j != null) {
                    jobs.setStatus(j.getId(), "OPEN".equals(j.getStatus()) ? "CLOSED" : "OPEN");
                    Web.flash(req, "Drive status updated.");
                }
                redirect(req, res, "/admin/jobs");
            }
            case "job/delete" -> {
                if (post) {
                    jobs.delete(Web.intParam(req, "id", 0));
                    Web.flash(req, "Drive deleted.");
                }
                redirect(req, res, "/admin/jobs");
            }
            case "applicants" -> {
                Job j = jobs.findById(Web.intParam(req, "job", 0));
                if (j == null) { notFound(res); return; }
                req.setAttribute("job", j);
                java.util.List<Application> apps = applications.listByJob(j.getId());
                scoreApplicants(j, apps);
                req.setAttribute("applications", apps);
                req.setAttribute("statuses", Application.STATUSES);
                render(req, res, "company/applicants");   // same view, admin can act on any drive
            }
            // ---------- applications & interviews ----------
            case "applications" -> {
                String status = Web.str(req, "status");
                req.setAttribute("applications", applications.listAll(isValidStatus(status) ? status : null));
                req.setAttribute("statuses", Application.STATUSES);
                render(req, res, "admin/applications");
            }
            case "application/status" -> {
                Application a = applications.findById(Web.intParam(req, "id", 0));
                String status = Web.str(req, "status");
                if (post && a != null && isValidStatus(status)) {
                    applications.updateStatus(a.getId(), status);
                    if (isAjax(req)) { res.setStatus(HttpServletResponse.SC_NO_CONTENT); return; }
                    Web.flash(req, a.getStudentName() + " moved to " + status + ".");
                }
                if (isAjax(req)) { res.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
                String back = Web.str(req, "back");
                redirect(req, res, back.startsWith("/admin/") ? back : "/admin/applications");
            }
            case "interview/schedule" -> {
                Application a = applications.findById(Web.intParam(req, "applicationId", 0));
                if (post && a != null) scheduleInterview(interviews, a, req);
                redirect(req, res, "/admin/applicants?job=" + (a == null ? 0 : a.getJobId()) + "#details");
            }
            case "interview/result" -> {
                com.placement.model.Interview i = interviews.findById(Web.intParam(req, "id", 0));
                Application a = i == null ? null : applications.findById(i.getApplicationId());
                String result = Web.str(req, "result");
                if (post && a != null && java.util.List.of("PENDING", "PASSED", "FAILED").contains(result)) {
                    interviews.updateResult(i.getId(), result, Web.strOrNull(req, "notes"));
                    Web.flash(req, "Interview result saved.");
                }
                redirect(req, res, "/admin/applicants?job=" + (a == null ? 0 : a.getJobId()) + "#details");
            }
            case "interviews" -> {
                java.util.List<com.placement.model.Interview> all = interviews.listAll();
                req.setAttribute("interviews", all);
                req.setAttribute("cal", new com.placement.model.CalendarMonth(com.placement.model.CalendarMonth.parse(Web.str(req, "month")), all));
                render(req, res, "admin/interviews");
            }
            // ---------- reports ----------
            case "reports" -> {
                req.setAttribute("stats", reports.summary());
                req.setAttribute("byDept", reports.byDepartment());
                req.setAttribute("byCompany", reports.byCompany());
                req.setAttribute("funnel", reports.statusCounts());
                req.setAttribute("bands", reports.packageBands());
                render(req, res, "admin/reports");
            }
            case "reports/placements.csv" -> exportCsv(res);
            // ---------- announcements ----------
            case "announcements" -> {
                if (post) {
                    String title = Web.str(req, "title");
                    String body = Web.str(req, "body");
                    if (title.isEmpty() || body.isEmpty()) {
                        Web.flashError(req, "Title and message are both required.");
                    } else {
                        announcements.create(title, body, Web.currentUserId(req));
                        Web.flash(req, "Announcement posted.");
                    }
                    redirect(req, res, "/admin/announcements");
                    return;
                }
                req.setAttribute("announcements", announcements.latest(50));
                render(req, res, "admin/announcements");
            }
            case "announcement/delete" -> {
                if (post) announcements.delete(Web.intParam(req, "id", 0));
                redirect(req, res, "/admin/announcements");
            }
            default -> notFound(res);
        }
    }

    private void saveJob(HttpServletRequest req, HttpServletResponse res) throws Exception {
        int id = Web.intParam(req, "id", 0);
        Job j = id == 0 ? new Job() : jobs.findById(id);
        if (j == null) { notFound(res); return; }
        String error = readJobForm(req, j);
        j.setCompanyId(Web.intParam(req, "companyId", 0));
        if (error == null && companies.findById(j.getCompanyId()) == null) error = "Choose a company.";
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("job", j);
            req.setAttribute("companies", companies.listAll());
            render(req, res, "common/job-form");
            return;
        }
        if (id == 0) jobs.create(j); else jobs.update(j);
        Web.flash(req, id == 0 ? "Drive created." : "Drive updated.");
        redirect(req, res, "/admin/jobs");
    }

    private void exportCsv(HttpServletResponse res) throws Exception {
        res.setContentType("text/csv");
        res.setHeader("Content-Disposition", "attachment; filename=\"placements-" + LocalDate.now() + ".csv\"");
        PrintWriter out = res.getWriter();
        out.println("Name,Roll No,Department,Company,Role,CTC (LPA),Offer Date");
        for (String[] row : reports.placementRows()) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                if (i > 0) line.append(',');
                String v = row[i] == null ? "" : row[i];
                if (!v.isEmpty() && "=+-@".indexOf(v.charAt(0)) >= 0) v = "'" + v;   // block spreadsheet formulas
                line.append('"').append(v.replace("\"", "\"\"")).append('"');
            }
            out.println(line);
        }
    }
}
