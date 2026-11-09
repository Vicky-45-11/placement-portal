package com.placement.model;

public class Company {
    private int id;
    private int userId;
    private String name;
    private String industry;
    private String website;
    private String location;
    private String description;
    private String email;
    private boolean active;
    private int jobCount;
    private int hiredCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getJobCount() { return jobCount; }
    public void setJobCount(int jobCount) { this.jobCount = jobCount; }
    public int getHiredCount() { return hiredCount; }
    public void setHiredCount(int hiredCount) { this.hiredCount = hiredCount; }
}
