package com.placement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static final List<String> STATUSES =
            List.of("APPLIED", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED");

    private int id;
    private int jobId;
    private int studentId;
    private int companyId;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    // joined display fields
    private String jobTitle;
    private String companyName;
    private BigDecimal ctcLpa;
    private String studentName;
    private String studentEmail;
    private String rollNo;
    private String department;
    private BigDecimal cgpa;
    private int backlogs;
    private String skills;
    private String resumeFile;
    private List<Interview> interviews = new ArrayList<>();

    /** Position in the pipeline: 0 applied, 1 shortlisted, 2 interview, 3 selected, -1 rejected. */
    public int getStageIndex() {
        return "REJECTED".equals(status) ? -1 : Math.max(0, STATUSES.indexOf(status));
    }

    public String getAppliedAtText() { return Fmt.dateTime(appliedAt); }
    public String getUpdatedAtText() { return Fmt.dateTime(updatedAt); }
    public boolean isHasResume() { return resumeFile != null && !resumeFile.isBlank(); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public BigDecimal getCtcLpa() { return ctcLpa; }
    public void setCtcLpa(BigDecimal ctcLpa) { this.ctcLpa = ctcLpa; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public BigDecimal getCgpa() { return cgpa; }
    public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
    public int getBacklogs() { return backlogs; }
    public void setBacklogs(int backlogs) { this.backlogs = backlogs; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getResumeFile() { return resumeFile; }
    public void setResumeFile(String resumeFile) { this.resumeFile = resumeFile; }
    public List<Interview> getInterviews() { return interviews; }
    public void setInterviews(List<Interview> interviews) { this.interviews = interviews; }
}
