package com.placement.dao;

import com.placement.model.Notification;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * In-app notifications. The send* methods resolve who should hear about an event
 * (the student behind an application, the recruiter behind a drive, every admin) in SQL.
 */
public class NotificationDao {

    public List<Notification> latest(int userId, int limit) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT n.*, TIMESTAMPDIFF(MINUTE, n.created_at, NOW()) AS age_min FROM notifications n WHERE user_id = ? ORDER BY created_at DESC, id DESC LIMIT ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Notification> out = new ArrayList<>();
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getInt("id"));
                    n.setBody(rs.getString("body"));
                    n.setLink(rs.getString("link"));
                    n.setRead(rs.getBoolean("is_read"));
                    n.setCreatedAt(Rows.ts(rs, "created_at"));
                    n.setAgeMinutes(rs.getLong("age_min"));
                    out.add(n);
                }
                return out;
            }
        }
    }

    public int unreadCount(int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    /** Marks one notification read and returns its link, or null if it is not this user's. */
    public String open(int id, int userId) throws SQLException {
        try (Connection c = DB.get()) {
            String link = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT link FROM notifications WHERE id = ? AND user_id = ?")) {
                ps.setInt(1, id);
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    link = rs.getString(1);
                }
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE notifications SET is_read = TRUE WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return link == null ? "" : link;
        }
    }

    public void markAllRead(int userId) throws SQLException {
        exec("UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE", userId);
    }

    public void toStudentOfApplication(int applicationId, String body, String link) throws SQLException {
        insertSelect("SELECT s.user_id, ?, ? FROM applications a JOIN students s ON s.id = a.student_id WHERE a.id = ?",
                body, link, applicationId);
    }

    public void toCompanyOfJob(int jobId, String body, String link) throws SQLException {
        insertSelect("SELECT co.user_id, ?, ? FROM jobs j JOIN companies co ON co.id = j.company_id WHERE j.id = ?",
                body, link, jobId);
    }

    public void toUser(int userId, String body, String link) throws SQLException {
        insertSelect("SELECT id, ?, ? FROM users WHERE id = ?", body, link, userId);
    }

    public void toAdmins(String body, String link) throws SQLException {
        insertSelect("SELECT id, ?, ? FROM users WHERE role = 'ADMIN' AND active = TRUE", body, link, null);
    }

    public void toAllStudents(String body, String link) throws SQLException {
        insertSelect("SELECT id, ?, ? FROM users WHERE role = 'STUDENT' AND active = TRUE", body, link, null);
    }

    private void insertSelect(String select, String body, String link, Integer id) throws SQLException {
        String b = body.length() > 300 ? body.substring(0, 297) + "..." : body;
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO notifications (user_id, body, link) " + select)) {
            ps.setString(1, b);
            ps.setString(2, link);
            if (id != null) ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    private void exec(String sql, int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
