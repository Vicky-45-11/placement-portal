package com.placement.dao;

import com.placement.model.Application;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationDao {
    private static final String BASE =
        "SELECT a.*, j.title AS job_title, j.ctc_lpa, j.company_id, co.name AS company_name, " +
        "  u.name AS student_name, u.email AS student_email, s.roll_no, s.department, s.cgpa, s.backlogs, " +
        "  s.skills, s.resume_file " +
        "FROM applications a " +
        "JOIN jobs j      ON j.id = a.job_id " +
        "JOIN companies co ON co.id = j.company_id " +
        "JOIN students s  ON s.id = a.student_id " +
        "JOIN users u     ON u.id = s.user_id ";

    private final InterviewDao interviewDao = new InterviewDao();

    public Application findById(int id) throws SQLException {
        List<Application> list = list(BASE + "WHERE a.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    /** A student's applications, newest first, each with its interview rounds. */
    public List<Application> listByStudent(int studentId) throws SQLException {
        List<Application> apps = list(BASE + "WHERE a.student_id = ? ORDER BY a.applied_at DESC", studentId);
        for (Application a : apps) a.setInterviews(interviewDao.listByApplication(a.getId()));
        return apps;
    }

    public List<Application> listByJob(int jobId) throws SQLException {
        List<Application> apps = list(BASE + "WHERE a.job_id = ? ORDER BY FIELD(a.status,'SELECTED','INTERVIEW','SHORTLISTED','APPLIED','REJECTED'), s.cgpa DESC", jobId);
        for (Application a : apps) a.setInterviews(interviewDao.listByApplication(a.getId()));
        return apps;
    }

    public List<Application> listAll(String status) throws SQLException {
        if (status == null || status.isEmpty()) return list(BASE + "ORDER BY a.applied_at DESC", null);
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(BASE + "WHERE a.status = ? ORDER BY a.applied_at DESC")) {
            ps.setString(1, status);
            return collect(ps);
        }
    }

    private List<Application> list(String sql, Integer param) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            return collect(ps);
        }
    }

    private List<Application> collect(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<Application> out = new ArrayList<>();
            while (rs.next()) out.add(Rows.application(rs));
            return out;
        }
    }

    /** jobId -> status for every job this student applied to. */
    public Map<Integer, String> statusByJobForStudent(int studentId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT job_id, status FROM applications WHERE student_id = ?")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, String> out = new HashMap<>();
                while (rs.next()) out.put(rs.getInt(1), rs.getString(2));
                return out;
            }
        }
    }

    public void apply(int jobId, int studentId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("INSERT INTO applications (job_id, student_id) VALUES (?,?)")) {
            ps.setInt(1, jobId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    public void withdraw(int applicationId, int studentId) throws SQLException {
        String sql = "DELETE FROM applications WHERE id = ? AND student_id = ? AND status = 'APPLIED'";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    /**
     * Changes an application's status. Moving to SELECTED writes the placement record;
     * moving away from SELECTED removes it. Both happen in one transaction.
     */
    public void updateStatus(int applicationId, String status) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement("UPDATE applications SET status = ? WHERE id = ?")) {
                    ps.setString(1, status);
                    ps.setInt(2, applicationId);
                    ps.executeUpdate();
                }
                if ("SELECTED".equals(status)) {
                    String sql = "INSERT IGNORE INTO placements (application_id, student_id, company_id, ctc_lpa, offer_date) "
                               + "SELECT a.id, a.student_id, j.company_id, j.ctc_lpa, CURDATE() "
                               + "FROM applications a JOIN jobs j ON j.id = a.job_id WHERE a.id = ?";
                    try (PreparedStatement ps = c.prepareStatement(sql)) {
                        ps.setInt(1, applicationId);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = c.prepareStatement("DELETE FROM placements WHERE application_id = ?")) {
                        ps.setInt(1, applicationId);
                        ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public Map<String, Integer> countByStatusForStudent(int studentId) throws SQLException {
        Map<String, Integer> out = new HashMap<>();
        for (String s : Application.STATUSES) out.put(s, 0);
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT status, COUNT(*) FROM applications WHERE student_id = ? GROUP BY status")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.put(rs.getString(1), rs.getInt(2));
            }
        }
        return out;
    }
}
