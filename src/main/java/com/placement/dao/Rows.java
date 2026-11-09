package com.placement.dao;

import com.placement.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Maps JDBC ResultSet rows to model objects. Column aliases come from the DAO queries. */
final class Rows {
    private Rows() {}

    static LocalDateTime ts(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toLocalDateTime();
    }

    static User user(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("active"));
        return u;
    }

    static Student student(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setRollNo(rs.getString("roll_no"));
        s.setDepartment(rs.getString("department"));
        s.setBatchYear(rs.getInt("batch_year"));
        s.setCgpa(rs.getBigDecimal("cgpa"));
        s.setBacklogs(rs.getInt("backlogs"));
        s.setPhone(rs.getString("phone"));
        s.setSkills(rs.getString("skills"));
        s.setResumeFile(rs.getString("resume_file"));
        s.setPlacedCompany(rs.getString("placed_company"));
        s.setPlacedCtc(rs.getBigDecimal("placed_ctc"));
        return s;
    }

    static Company company(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setName(rs.getString("name"));
        c.setIndustry(rs.getString("industry"));
        c.setWebsite(rs.getString("website"));
        c.setLocation(rs.getString("location"));
        c.setDescription(rs.getString("description"));
        c.setEmail(rs.getString("email"));
        c.setActive(rs.getBoolean("active"));
        c.setJobCount(rs.getInt("job_count"));
        c.setHiredCount(rs.getInt("hired_count"));
        return c;
    }

    static Job job(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setCompanyId(rs.getInt("company_id"));
        j.setCompanyName(rs.getString("company_name"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setJobType(rs.getString("job_type"));
        j.setCtcLpa(rs.getBigDecimal("ctc_lpa"));
        j.setLocation(rs.getString("location"));
        j.setMinCgpa(rs.getBigDecimal("min_cgpa"));
        j.setMaxBacklogs(rs.getInt("max_backlogs"));
        j.setEligibleDepartments(rs.getString("eligible_departments"));
        j.setRequiredSkills(rs.getString("required_skills"));
        j.setDeadline(rs.getDate("deadline").toLocalDate());
        j.setStatus(rs.getString("status"));
        j.setApplicantCount(rs.getInt("applicant_count"));
        return j;
    }

    static Application application(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setJobId(rs.getInt("job_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setCompanyId(rs.getInt("company_id"));
        a.setStatus(rs.getString("status"));
        a.setAppliedAt(ts(rs, "applied_at"));
        a.setUpdatedAt(ts(rs, "updated_at"));
        a.setJobTitle(rs.getString("job_title"));
        a.setCompanyName(rs.getString("company_name"));
        a.setCtcLpa(rs.getBigDecimal("ctc_lpa"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentEmail(rs.getString("student_email"));
        a.setRollNo(rs.getString("roll_no"));
        a.setDepartment(rs.getString("department"));
        a.setCgpa(rs.getBigDecimal("cgpa"));
        a.setBacklogs(rs.getInt("backlogs"));
        a.setSkills(rs.getString("skills"));
        a.setResumeFile(rs.getString("resume_file"));
        return a;
    }

    static Interview interview(ResultSet rs) throws SQLException {
        Interview i = new Interview();
        i.setId(rs.getInt("id"));
        i.setApplicationId(rs.getInt("application_id"));
        i.setRoundName(rs.getString("round_name"));
        i.setScheduledAt(ts(rs, "scheduled_at"));
        i.setMode(rs.getString("mode"));
        i.setVenue(rs.getString("venue"));
        i.setResult(rs.getString("result"));
        i.setNotes(rs.getString("notes"));
        i.setStudentName(rs.getString("student_name"));
        i.setRollNo(rs.getString("roll_no"));
        i.setJobTitle(rs.getString("job_title"));
        i.setCompanyName(rs.getString("company_name"));
        return i;
    }
}
