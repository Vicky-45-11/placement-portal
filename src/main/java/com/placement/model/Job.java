package com.placement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

public class Job {
    private int id;
    private int companyId;
    private String companyName;
    private String title;
    private String description;
    private String jobType = "FULL_TIME";
    private BigDecimal ctcLpa;
    private String location;
    private BigDecimal minCgpa = BigDecimal.ZERO;
    private int maxBacklogs;
    private String eligibleDepartments = "ALL";
    private LocalDate deadline;
    private String status = "OPEN";
    private int applicantCount;
    // per-student fields, set when listing jobs for a student
    private String applicationStatus;
    private String ineligibleReason;

    /** True while the drive is open and the deadline has not passed. */
    public boolean isAcceptingApplications() {
        return "OPEN".equals(status) && deadline != null && !deadline.isBefore(LocalDate.now());
    }

    /** Returns why the student cannot apply, or null when they are eligible. */
    public String checkEligibility(Student s) {
        if (!isAcceptingApplications()) return "Applications are closed";
        if (s.getCgpa().compareTo(minCgpa) < 0) return "Needs CGPA " + minCgpa + " or above";
        if (s.getBacklogs() > maxBacklogs) return "Allows at most " + maxBacklogs + " backlog(s)";
        if (!isDepartmentEligible(s.getDepartment())) return "Open to " + eligibleDepartments + " only";
        return null;
    }

    public boolean isDepartmentEligible(String dept) {
        if (eligibleDepartments == null || eligibleDepartments.isBlank()
                || "ALL".equalsIgnoreCase(eligibleDepartments.trim())) return true;
        return Arrays.stream(eligibleDepartments.split(","))
                .map(String::trim).anyMatch(d -> d.equalsIgnoreCase(dept));
    }

    public String getDeadlineText() { return Fmt.date(deadline); }
    public String getJobTypeLabel() { return "INTERNSHIP".equals(jobType) ? "Internship" : "Full time"; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public BigDecimal getCtcLpa() { return ctcLpa; }
    public void setCtcLpa(BigDecimal ctcLpa) { this.ctcLpa = ctcLpa; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getMinCgpa() { return minCgpa; }
    public void setMinCgpa(BigDecimal minCgpa) { this.minCgpa = minCgpa; }
    public int getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(int maxBacklogs) { this.maxBacklogs = maxBacklogs; }
    public String getEligibleDepartments() { return eligibleDepartments; }
    public void setEligibleDepartments(String eligibleDepartments) { this.eligibleDepartments = eligibleDepartments; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getApplicantCount() { return applicantCount; }
    public void setApplicantCount(int applicantCount) { this.applicantCount = applicantCount; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public String getIneligibleReason() { return ineligibleReason; }
    public void setIneligibleReason(String ineligibleReason) { this.ineligibleReason = ineligibleReason; }
}
