package com.placement.model;

import java.time.LocalDateTime;

public class Interview {
    private int id;
    private int applicationId;
    private String roundName;
    private LocalDateTime scheduledAt;
    private String mode = "OFFLINE";
    private String venue;
    private String result = "PENDING";
    private String notes;
    // joined display fields
    private String studentName;
    private String rollNo;
    private String jobTitle;
    private String companyName;

    public String getScheduledAtText() { return Fmt.dateTime(scheduledAt); }
    public boolean isUpcoming() { return scheduledAt != null && scheduledAt.isAfter(LocalDateTime.now()); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
