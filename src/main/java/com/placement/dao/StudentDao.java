package com.placement.dao;

import com.placement.model.Student;
import com.placement.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private static final String BASE =
        "SELECT s.*, u.name, u.email, " +
        "  (SELECT c.name FROM placements p JOIN companies c ON c.id = p.company_id " +
        "    WHERE p.student_id = s.id ORDER BY p.ctc_lpa DESC LIMIT 1) AS placed_company, " +
        "  (SELECT MAX(p.ctc_lpa) FROM placements p WHERE p.student_id = s.id) AS placed_ctc " +
        "FROM students s JOIN users u ON u.id = s.user_id ";

    public Student findByUserId(int userId) throws SQLException {
        return one(BASE + "WHERE s.user_id = ?", userId);
    }

    public Student findById(int id) throws SQLException {
        return one(BASE + "WHERE s.id = ?", id);
    }

    private Student one(String sql, int param) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Rows.student(rs) : null;
            }
        }
    }

    /** Lists students, optionally filtered by department, placement status and a name/roll search. */
    public List<Student> list(String department, String placed, String search) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM (" + BASE + ") t WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (department != null && !department.isEmpty()) {
            sql.append(" AND department = ?");
            params.add(department);
        }
        if ("yes".equals(placed)) sql.append(" AND placed_company IS NOT NULL");
        if ("no".equals(placed)) sql.append(" AND placed_company IS NULL");
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (name LIKE ? OR roll_no LIKE ?)");
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }
        sql.append(" ORDER BY department, roll_no");
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> out = new ArrayList<>();
                while (rs.next()) out.add(Rows.student(rs));
                return out;
            }
        }
    }

    public boolean rollNoExists(String rollNo) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT 1 FROM students WHERE roll_no = ?")) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void updateProfile(Student s) throws SQLException {
        String sql = "UPDATE students SET department=?, batch_year=?, cgpa=?, backlogs=?, phone=?, skills=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getDepartment());
            ps.setInt(2, s.getBatchYear());
            ps.setBigDecimal(3, s.getCgpa());
            ps.setInt(4, s.getBacklogs());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getSkills());
            ps.setInt(7, s.getId());
            ps.executeUpdate();
        }
    }

    public void updateResume(int studentId, String fileName) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE students SET resume_file=? WHERE id=?")) {
            ps.setString(1, fileName);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    public void delete(int studentId) throws SQLException {
        // deleting the user cascades to the student profile, applications and placements
        String sql = "DELETE u FROM users u JOIN students s ON s.user_id = u.id WHERE s.id = ?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        }
    }
}
