package com.placement.web;

import com.placement.dao.*;
import com.placement.model.Application;
import com.placement.model.Company;
import com.placement.model.Interview;
import com.placement.model.Job;
import com.placement.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/company/*")
public class CompanyServlet extends BaseServlet {
    private final CompanyDao companies = new CompanyDao();
    private final JobDao jobs = new JobDao();
    private final ApplicationDao applications = new ApplicationDao();
    private final InterviewDao interviews = new InterviewDao();

    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Company me = companies.findById((Integer) req.getSession().getAttribute("companyId"));
        req.setAttribute("me", me);
        switch (action) {
            case "" -> {
                List<Job> myJobs = jobs.listByCompany(me.getId());
                req.setAttribute("jobs", myJobs);
                req.setAttribute("applicantTotal", myJobs.stream().mapToInt(Job::getApplicantCount).sum());
                List<Interview> upcoming = interviews.listByCompany(me.getId()).stream()
                        .filter(Interview::isUpcoming).sorted(java.util.Comparator.comparing(Interview::getScheduledAt)).toList();
                req.setAttribute("interviews", upcoming);
                java.util.Map<Integer, Integer> waiting = applications.unreviewedByJob(me.getId());
                req.setAttribute("waiting", waiting);
                int waitingTotal = waiting.values().stream().mapToInt(Integer::intValue).sum();
                com.placement.model.NextStep next;
                if (myJobs.isEmpty()) {
                    next = com.placement.model.NextStep.todo("Post your first drive", "Set the role, package, CGPA cut-off and skills. "
                            + "Students who qualify see it straight away.", "/company/job/new", "Post a drive");
                } else if (waitingTotal > 0) {
                    Job top = myJobs.stream().filter(j -> waiting.containsKey(j.getId()))
                            .max(java.util.Comparator.comparingInt(j -> waiting.get(j.getId()))).orElse(myJobs.get(0));
                    next = com.placement.model.NextStep.todo(waitingTotal + " new applicant" + (waitingTotal == 1 ? "" : "s") + " to review",
                            waiting.get(top.getId()) + " waiting on " + top.getTitle() + ". Drag them to Shortlisted or Rejected on the board.",
                            "/company/applicants?job=" + top.getId(), "Open hiring board");
                } else if (!upcoming.isEmpty()) {
                    Interview i = upcoming.get(0);
                    next = com.placement.model.NextStep.todo("Next interview: " + i.getStudentName(), i.getRoundName() + " round for " + i.getJobTitle()
                            + " · " + i.getScheduledAtText() + ". Record the result after it ends.", "/company/interviews", "Open calendar");
                } else {
                    next = com.placement.model.NextStep.done("You're all caught up", "Every applicant has been reviewed. New applications show up in the bell.",
                            "/company/jobs", "My drives");
                }
                req.setAttribute("next", next);
                render(req, res, "company/dashboard");
            }
            case "profile" -> { if (post) saveProfile(me, req, res); else render(req, res, "company/profile"); }
            case "jobs" -> {
                req.setAttribute("jobs", jobs.listByCompany(me.getId()));
                render(req, res, "company/jobs");
            }
            case "job/new" -> {
                Job j = new Job();
                j.setDeadline(java.time.LocalDate.now().plusDays(14));
                req.setAttribute("job", j);
                render(req, res, "common/job-form");
            }
            case "job/edit" -> {
                Job j = ownJob(me, Web.intParam(req, "id", 0));
                if (j == null) { notFound(res); return; }
                req.setAttribute("job", j);
                render(req, res, "common/job-form");
            }
            case "job/save" -> { if (post) saveJob(me, req, res); else redirect(req, res, "/company/jobs"); }
            case "job/status" -> {
                Job j = ownJob(me, Web.intParam(req, "id", 0));
                if (post && j != null) {
                    jobs.setStatus(j.getId(), "OPEN".equals(j.getStatus()) ? "CLOSED" : "OPEN");
                    Web.flash(req, "Drive status updated.");
                }
                redirect(req, res, "/company/jobs");
            }
            case "applicants" -> {
                Job j = ownJob(me, Web.intParam(req, "job", 0));
                if (j == null) { notFound(res); return; }
                req.setAttribute("job", j);
                java.util.List<Application> apps = applications.listByJob(j.getId());
                scoreApplicants(j, apps);
                req.setAttribute("applications", apps);
                req.setAttribute("statuses", Application.STATUSES);
                render(req, res, "company/applicants");
            }
            case "application/status" -> {
                Application a = ownApplication(me, Web.intParam(req, "id", 0));
                String status = Web.str(req, "status");
                if (post && a != null && isValidStatus(status)) {
                    applications.updateStatus(a.getId(), status);
                    if (!status.equals(a.getStatus()))
                        com.placement.util.Notify.student(a.getId(), com.placement.util.Notify.statusText(status, a.getJobTitle(), a.getCompanyName()), "/student/applications");
                    if (isAjax(req)) { res.setStatus(HttpServletResponse.SC_NO_CONTENT); return; }
                    Web.flash(req, a.getStudentName() + " moved to " + status + ".");
                }
                if (isAjax(req)) { res.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
                redirect(req, res, "/company/applicants?job=" + (a == null ? 0 : a.getJobId()) + "#details");
            }
            case "interview/schedule" -> {
                Application a = ownApplication(me, Web.intParam(req, "applicationId", 0));
                if (post && a != null) scheduleInterview(interviews, a, req);
                redirect(req, res, "/company/applicants?job=" + (a == null ? 0 : a.getJobId()) + "#details");
            }
            case "interview/result" -> {
                Interview i = interviews.findById(Web.intParam(req, "id", 0));
                Application a = i == null ? null : ownApplication(me, i.getApplicationId());
                String result = Web.str(req, "result");
                if (post && a != null && List.of("PENDING", "PASSED", "FAILED").contains(result)) {
                    interviews.updateResult(i.getId(), result, Web.strOrNull(req, "notes"));
                    if (!result.equals(i.getResult()) && !"PENDING".equals(result))
                        com.placement.util.Notify.student(a.getId(), "Result for your " + i.getRoundName() + " round (" + a.getJobTitle() + ", "
                                + a.getCompanyName() + "): " + ("PASSED".equals(result) ? "passed." : "not cleared."), "/student/applications");
                    Web.flash(req, "Interview result saved.");
                }
                redirect(req, res, "/company/applicants?job=" + (a == null ? 0 : a.getJobId()) + "#details");
            }
            case "interviews" -> {
                java.util.List<Interview> all = interviews.listByCompany(me.getId());
                req.setAttribute("interviews", all);
                req.setAttribute("cal", new com.placement.model.CalendarMonth(com.placement.model.CalendarMonth.parse(Web.str(req, "month")), all));
                render(req, res, "company/interviews");
            }
            default -> notFound(res);
        }
    }

    private Job ownJob(Company me, int jobId) throws Exception {
        Job j = jobs.findById(jobId);
        return j != null && j.getCompanyId() == me.getId() ? j : null;
    }

    private Application ownApplication(Company me, int applicationId) throws Exception {
        Application a = applications.findById(applicationId);
        return a != null && a.getCompanyId() == me.getId() ? a : null;
    }

    private void saveJob(Company me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        int id = Web.intParam(req, "id", 0);
        Job j = id == 0 ? new Job() : ownJob(me, id);
        if (j == null) { notFound(res); return; }
        String error = readJobForm(req, j);
        j.setCompanyId(me.getId());
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("job", j);
            render(req, res, "common/job-form");
            return;
        }
        if (id == 0) jobs.create(j); else jobs.update(j);
        Web.flash(req, id == 0 ? "Drive posted. Students can now apply." : "Drive updated.");
        redirect(req, res, "/company/jobs");
    }

    private void saveProfile(Company me, HttpServletRequest req, HttpServletResponse res) throws Exception {
        me.setName(Web.str(req, "name"));
        me.setIndustry(Web.str(req, "industry"));
        me.setWebsite(Web.str(req, "website"));
        me.setLocation(Web.str(req, "location"));
        me.setDescription(Web.str(req, "description"));
        if (me.getName().isEmpty()) {
            Web.flashError(req, "Company name is required.");
        } else {
            companies.update(me);
            req.getSession().setAttribute("companyName", me.getName());
            Web.flash(req, "Company profile saved.");
        }
        redirect(req, res, "/company/profile");
    }
}
