package com.placement.dao;

import com.placement.model.ReportRow;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregate queries for the admin dashboard and reports. */
public class ReportDao {

    public Map<String, Object> summary() throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        try (Connection c = DB.get(); Statement st = c.createStatement()) {
            m.put("students",     scalar(st, "SELECT COUNT(*) FROM students"));
            m.put("placed",       scalar(st, "SELECT COUNT(DISTINCT student_id) FROM placements"));
            m.put("companies",    scalar(st, "SELECT COUNT(*) FROM companies co JOIN users u ON u.id = co.user_id WHERE u.active"));
            m.put("pendingCompanies", scalar(st, "SELECT COUNT(*) FROM users WHERE role='COMPANY' AND NOT active"));
            m.put("openJobs",     scalar(st, "SELECT COUNT(*) FROM jobs WHERE status='OPEN' AND deadline >= CURDATE()"));
            m.put("applications", scalar(st, "SELECT COUNT(*) FROM applications"));
            m.put("upcomingInterviews", scalar(st, "SELECT COUNT(*) FROM interviews WHERE scheduled_at >= NOW()"));
            m.put("highestCtc",   scalar(st, "SELECT MAX(ctc_lpa) FROM placements"));
            m.put("averageCtc",   scalar(st, "SELECT ROUND(AVG(ctc_lpa),2) FROM placements"));
        }
        long students = ((Number) m.get("students")).longValue();
        long placed = ((Number) m.get("placed")).longValue();
        m.put("placedPercent", students == 0 ? 0 : Math.round(placed * 100.0 / students));
        return m;
    }

    private Object scalar(Statement st, String sql) throws SQLException {
        try (ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            Object v = rs.getObject(1);
            return v == null ? 0 : v;
        }
    }

    public List<ReportRow> byDepartment() throws SQLException {
        String sql = "SELECT department AS label, COUNT(*) AS total, COUNT(ctc_lpa) AS placed, "
                   + "AVG(ctc_lpa) AS avg_ctc, MAX(ctc_lpa) AS max_ctc "
                   + "FROM (SELECT student_id, department, MAX(ctc_lpa) AS ctc_lpa FROM v_student_placement "
                   + "      GROUP BY student_id, department) t GROUP BY department ORDER BY department";
        return rows(sql);
    }

    public List<ReportRow> byCompany() throws SQLException {
        String sql = "SELECT co.name AS label, COUNT(DISTINCT a.id) AS total, COUNT(DISTINCT p.id) AS placed, "
                   + "AVG(p.ctc_lpa) AS avg_ctc, MAX(p.ctc_lpa) AS max_ctc "
                   + "FROM companies co LEFT JOIN jobs j ON j.company_id = co.id "
                   + "LEFT JOIN applications a ON a.job_id = j.id "
                   + "LEFT JOIN placements p ON p.application_id = a.id "
                   + "GROUP BY co.id, co.name ORDER BY placed DESC, co.name";
        return rows(sql);
    }

    private List<ReportRow> rows(String sql) throws SQLException {
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<ReportRow> out = new ArrayList<>();
            while (rs.next()) {
                ReportRow r = new ReportRow();
                r.setLabel(rs.getString("label"));
                r.setTotal(rs.getInt("total"));
                r.setPlaced(rs.getInt("placed"));
                r.setAvgCtc(rs.getBigDecimal("avg_ctc"));
                r.setMaxCtc(rs.getBigDecimal("max_ctc"));
                out.add(r);
            }
            return out;
        }
    }

    /** Every placement as CSV-ready rows: name, roll, dept, company, ctc, offer date. */
    public List<String[]> placementRows() throws SQLException {
        String sql = "SELECT u.name, s.roll_no, s.department, co.name, j.title, p.ctc_lpa, p.offer_date "
                   + "FROM placements p JOIN students s ON s.id = p.student_id JOIN users u ON u.id = s.user_id "
                   + "JOIN companies co ON co.id = p.company_id JOIN applications a ON a.id = p.application_id "
                   + "JOIN jobs j ON j.id = a.job_id ORDER BY s.department, s.roll_no";
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<String[]> out = new ArrayList<>();
            while (rs.next()) {
                String[] row = new String[7];
                for (int i = 0; i < 7; i++) row[i] = rs.getString(i + 1);
                out.add(row);
            }
            return out;
        }
    }

    /** Applications per pipeline stage, in pipeline order. */
    public Map<String, Integer> statusCounts() throws SQLException {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (String st : com.placement.model.Application.STATUSES) m.put(st, 0);
        try (Connection c = DB.get(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT status, COUNT(*) FROM applications GROUP BY status")) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        }
        return m;
    }

    /** Number of offers in each package band (LPA). */
    public Map<String, Integer> packageBands() throws SQLException {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("< 4", 0); m.put("4 - 6", 0); m.put("6 - 8", 0); m.put("8 - 10", 0); m.put("10 +", 0);
        String sql = "SELECT CASE WHEN ctc_lpa < 4 THEN '< 4' WHEN ctc_lpa < 6 THEN '4 - 6' WHEN ctc_lpa < 8 THEN '6 - 8' "
                   + "WHEN ctc_lpa < 10 THEN '8 - 10' ELSE '10 +' END AS band, COUNT(*) FROM placements GROUP BY band";
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        }
        return m;
    }

    /** Latest offers for the public "offer wall": first name, department, company, package. */
    public List<String[]> recentOffers(int limit) throws SQLException {
        String sql = "SELECT SUBSTRING_INDEX(u.name, ' ', 1), s.department, co.name, p.ctc_lpa FROM placements p "
                   + "JOIN students s ON s.id = p.student_id JOIN users u ON u.id = s.user_id "
                   + "JOIN companies co ON co.id = p.company_id ORDER BY p.offer_date DESC, p.id DESC LIMIT ?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<String[]> out = new ArrayList<>();
                while (rs.next()) out.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
                return out;
            }
        }
    }
}
