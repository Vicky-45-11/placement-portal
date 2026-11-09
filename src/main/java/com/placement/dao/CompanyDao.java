package com.placement.dao;

import com.placement.model.Company;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDao {
    private static final String BASE =
        "SELECT co.*, u.email, u.active, " +
        "  (SELECT COUNT(*) FROM jobs j WHERE j.company_id = co.id) AS job_count, " +
        "  (SELECT COUNT(*) FROM placements p WHERE p.company_id = co.id) AS hired_count " +
        "FROM companies co JOIN users u ON u.id = co.user_id ";

    public Company findByUserId(int userId) throws SQLException {
        return one(BASE + "WHERE co.user_id = ?", userId);
    }

    public Company findById(int id) throws SQLException {
        return one(BASE + "WHERE co.id = ?", id);
    }

    private Company one(String sql, int param) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Rows.company(rs) : null;
            }
        }
    }

    public List<Company> listAll() throws SQLException {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(BASE + "ORDER BY u.active, co.name");
             ResultSet rs = ps.executeQuery()) {
            List<Company> out = new ArrayList<>();
            while (rs.next()) out.add(Rows.company(rs));
            return out;
        }
    }

    public void update(Company co) throws SQLException {
        String sql = "UPDATE companies SET name=?, industry=?, website=?, location=?, description=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, co.getName());
            ps.setString(2, co.getIndustry());
            ps.setString(3, co.getWebsite());
            ps.setString(4, co.getLocation());
            ps.setString(5, co.getDescription());
            ps.setInt(6, co.getId());
            ps.executeUpdate();
        }
    }
}
