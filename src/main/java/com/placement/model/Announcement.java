package com.placement.model;

import java.time.LocalDateTime;

public class Announcement {
    private int id;
    private String title;
    private String body;
    private LocalDateTime createdAt;

    public String getCreatedAtText() { return Fmt.dateTime(createdAt); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
