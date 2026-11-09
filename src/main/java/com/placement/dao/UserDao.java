package com.placement.dao;

import com.placement.model.Company;
import com.placement.model.Student;
import com.placement.model.User;
import com.placement.util.DB;
import com.placement.util.PasswordUtil;

import java.sql.*;

public class UserDao {

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Rows.user(rs) : null;
            }
        }
    }

    public User findById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Rows.user(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        return findByEmail(email) != null;
    }

    /** Creates the login row and the student profile in one transaction. */
    public void registerStudent(String name, String email, String password, Student s) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                int userId = insertUser(c, name, email, password, "STUDENT", true);
                String sql = "INSERT INTO students (user_id, roll_no, department, batch_year, cgpa, backlogs, phone, skills) "
                           + "VALUES (?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, s.getRollNo());
                    ps.setString(3, s.getDepartment());
                    ps.setInt(4, s.getBatchYear());
                    ps.setBigDecimal(5, s.getCgpa());
                    ps.setInt(6, s.getBacklogs());
                    ps.setString(7, s.getPhone());
                    ps.setString(8, s.getSkills());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Company accounts start inactive until the placement officer approves them. */
    public void registerCompany(String contactName, String email, String password, Company co) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                int userId = insertUser(c, contactName, email, password, "COMPANY", false);
                String sql = "INSERT INTO companies (user_id, name, industry, website, location, description) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, co.getName());
                    ps.setString(3, co.getIndustry());
                    ps.setString(4, co.getWebsite());
                    ps.setString(5, co.getLocation());
                    ps.setString(6, co.getDescription());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private int insertUser(Connection c, String name, String email, String password, String role, boolean active)
            throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role, active) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(password));
            ps.setString(4, role);
            ps.setBoolean(5, active);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public void setActive(int userId, boolean active) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET active = ? WHERE id = ?")) {
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void updateName(int userId, String name) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET name = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void updatePassword(int userId, String newPassword) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
            ps.setString(1, PasswordUtil.hash(newPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}
