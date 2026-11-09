package com.placement.model;

import java.math.BigDecimal;

public class Student {
    private int id;
    private int userId;
    private String name;
    private String email;
    private String rollNo;
    private String department;
    private int batchYear;
    private BigDecimal cgpa = BigDecimal.ZERO;
    private int backlogs;
    private String phone;
    private String skills;
    private String resumeFile;
    // filled by queries that join placements
    private String placedCompany;
    private BigDecimal placedCtc;

    public boolean isPlaced() { return placedCompany != null; }
    public boolean isHasResume() { return resumeFile != null && !resumeFile.isBlank(); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }
    public BigDecimal getCgpa() { return cgpa; }
    public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
    public int getBacklogs() { return backlogs; }
    public void setBacklogs(int backlogs) { this.backlogs = backlogs; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getResumeFile() { return resumeFile; }
    public void setResumeFile(String resumeFile) { this.resumeFile = resumeFile; }
    public String getPlacedCompany() { return placedCompany; }
    public void setPlacedCompany(String placedCompany) { this.placedCompany = placedCompany; }
    public BigDecimal getPlacedCtc() { return placedCtc; }
    public void setPlacedCtc(BigDecimal placedCtc) { this.placedCtc = placedCtc; }
}
