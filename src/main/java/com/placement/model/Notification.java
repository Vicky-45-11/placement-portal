package com.placement.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String body;
    private String link;
    private boolean read;
    private LocalDateTime createdAt;
    private long ageMinutes = -1;   // computed by MySQL, so it is right whatever the time zones are

    public void setAgeMinutes(long m) { this.ageMinutes = m; }

    /** "just now", "12 min ago", "3 h ago", "2 days ago", then the date. */
    public String getAgo() {
        if (createdAt == null) return "";
        long min = Math.max(0, ageMinutes >= 0 ? ageMinutes : Duration.between(createdAt, LocalDateTime.now()).toMinutes());
        if (min < 1) return "just now";
        if (min < 60) return min + " min ago";
        if (min < 60 * 24) return (min / 60) + " h ago";
        long days = min / (60 * 24);
        if (days < 7) return days + (days == 1 ? " day ago" : " days ago");
        return Fmt.date(createdAt.toLocalDate());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
