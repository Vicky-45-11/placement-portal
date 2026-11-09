package com.placement.dao;

import com.placement.model.Interview;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterviewDao {
    private static final String BASE =
        "SELECT i.*, u.name AS student_name, s.roll_no, j.title AS job_title, co.name AS company_name " +
        "FROM interviews i " +
        "JOIN applications a ON a.id = i.application_id " +
        "JOIN students s     ON s.id = a.student_id " +
        "JOIN users u        ON u.id = s.user_id " +
        "JOIN jobs j         ON j.id = a.job_id " +
        "JOIN companies co   ON co.id = j.company_id ";

    public Interview findById(int id) throws SQLException {
        List<Interview> l = list(BASE + "WHERE i.id = ?", id);
        return l.isEmpty() ? null : l.get(0);
    }

    public List<Interview> listByApplication(int applicationId) throws SQLException {
        return list(BASE + "WHERE i.application_id = ? ORDER BY i.scheduled_at", applicationId);
    }

    public List<Interview> listByCompany(int companyId) throws SQLException {
        return list(BASE + "WHERE j.company_id = ? ORDER BY i.scheduled_at DESC", companyId);
    }

    public List<Interview> listUpcomingForStudent(int studentId) throws SQLException {
        return list(BASE + "WHERE a.student_id = ? AND i.scheduled_at >= NOW() ORDER BY i.scheduled_at", studentId);
    }

    public List<Interview> listAll() throws SQLException {
        return list(BASE + "ORDER BY i.scheduled_at DESC", null);
    }

    private List<Interview> list(String sql, Integer param) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                List<Interview> out = new ArrayList<>();
                while (rs.next()) out.add(Rows.interview(rs));
                return out;
            }
        }
    }

    /** Schedules a round and moves the application to INTERVIEW in one transaction. */
    public void schedule(Interview i) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                String sql = "INSERT INTO interviews (application_id, round_name, scheduled_at, mode, venue, notes) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, i.getApplicationId());
                    ps.setString(2, i.getRoundName());
                    ps.setTimestamp(3, Timestamp.valueOf(i.getScheduledAt()));
                    ps.setString(4, i.getMode());
                    ps.setString(5, i.getVenue());
                    ps.setString(6, i.getNotes());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE applications SET status = 'INTERVIEW' WHERE id = ? AND status IN ('APPLIED','SHORTLISTED')")) {
                    ps.setInt(1, i.getApplicationId());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public void updateResult(int interviewId, String result, String notes) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE interviews SET result=?, notes=? WHERE id=?")) {
            ps.setString(1, result);
            ps.setString(2, notes);
            ps.setInt(3, interviewId);
            ps.executeUpdate();
        }
    }
}
