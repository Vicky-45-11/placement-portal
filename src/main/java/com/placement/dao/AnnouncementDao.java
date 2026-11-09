package com.placement.dao;

import com.placement.model.Announcement;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDao {

    public List<Announcement> latest(int limit) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM announcements ORDER BY created_at DESC, id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Announcement> out = new ArrayList<>();
                while (rs.next()) {
                    Announcement a = new Announcement();
                    a.setId(rs.getInt("id"));
                    a.setTitle(rs.getString("title"));
                    a.setBody(rs.getString("body"));
                    a.setCreatedAt(Rows.ts(rs, "created_at"));
                    out.add(a);
                }
                return out;
            }
        }
    }

    public void create(String title, String body, int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO announcements (title, body, created_by) VALUES (?,?,?)")) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM announcements WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
