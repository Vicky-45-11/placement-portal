package com.placement.dao;

import com.placement.model.Job;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDao {
    private static final String BASE =
        "SELECT j.*, co.name AS company_name, " +
        "  (SELECT COUNT(*) FROM applications a WHERE a.job_id = j.id) AS applicant_count " +
        "FROM jobs j JOIN companies co ON co.id = j.company_id ";

    public Job findById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(BASE + "WHERE j.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Rows.job(rs) : null;
            }
        }
    }

    /** Open drives whose deadline has not passed, from approved companies. */
    public List<Job> listOpen() throws SQLException {
        return list(BASE + "JOIN users u ON u.id = co.user_id "
                + "WHERE j.status = 'OPEN' AND j.deadline >= CURDATE() AND u.active = TRUE ORDER BY j.deadline", null);
    }

    public List<Job> listByCompany(int companyId) throws SQLException {
        return list(BASE + "WHERE j.company_id = ? ORDER BY j.created_at DESC", companyId);
    }

    public List<Job> listAll() throws SQLException {
        return list(BASE + "ORDER BY j.created_at DESC", null);
    }

    private List<Job> list(String sql, Integer param) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                List<Job> out = new ArrayList<>();
                while (rs.next()) out.add(Rows.job(rs));
                return out;
            }
        }
    }

    public void create(Job j) throws SQLException {
        String sql = "INSERT INTO jobs (company_id, title, description, job_type, ctc_lpa, location, min_cgpa, "
                   + "max_backlogs, eligible_departments, required_skills, deadline, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bind(ps, j);
            ps.setString(i, j.getStatus());
            ps.executeUpdate();
        }
    }

    public void update(Job j) throws SQLException {
        String sql = "UPDATE jobs SET company_id=?, title=?, description=?, job_type=?, ctc_lpa=?, location=?, min_cgpa=?, "
                   + "max_backlogs=?, eligible_departments=?, required_skills=?, deadline=?, status=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bind(ps, j);
            ps.setString(i++, j.getStatus());
            ps.setInt(i, j.getId());
            ps.executeUpdate();
        }
    }

    private int bind(PreparedStatement ps, Job j) throws SQLException {
        int i = 1;
        ps.setInt(i++, j.getCompanyId());
        ps.setString(i++, j.getTitle());
        ps.setString(i++, j.getDescription());
        ps.setString(i++, j.getJobType());
        ps.setBigDecimal(i++, j.getCtcLpa());
        ps.setString(i++, j.getLocation());
        ps.setBigDecimal(i++, j.getMinCgpa());
        ps.setInt(i++, j.getMaxBacklogs());
        ps.setString(i++, j.getEligibleDepartments());
        ps.setString(i++, j.getRequiredSkills());
        ps.setDate(i++, Date.valueOf(j.getDeadline()));
        return i;
    }

    public void setStatus(int jobId, String status) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE jobs SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, jobId);
            ps.executeUpdate();
        }
    }

    public void delete(int jobId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM jobs WHERE id=?")) {
            ps.setInt(1, jobId);
            ps.executeUpdate();
        }
    }
}
