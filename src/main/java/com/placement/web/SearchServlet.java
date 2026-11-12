package com.placement.web;

import com.placement.util.DB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON search behind the Ctrl+K palette. Each role only sees what it is allowed to:
 * admins search students, recruiters and drives; recruiters search their own drives and
 * applicants; students search open drives.
 */
@WebServlet("/search")
public class SearchServlet extends BaseServlet {
    private record Hit(String group, String label, String sub, String href) {}

    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        String q = req.getParameter("q") == null ? "" : req.getParameter("q").trim();
        List<Hit> hits = new ArrayList<>();
        if (q.length() >= 2 && q.length() <= 60) {
            String like = "%" + q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            HttpSession s = req.getSession(false);
            String role = (String) s.getAttribute("role");
            String ctx = req.getContextPath();
            try (Connection c = DB.get()) {
                switch (role) {
                    case "ADMIN" -> {
                        query(c, hits, "Students", "SELECT s.id, u.name, CONCAT(s.roll_no,' · ',s.department,' · CGPA ',s.cgpa) FROM students s JOIN users u ON u.id = s.user_id "
                                + "WHERE u.name LIKE ? OR s.roll_no LIKE ? OR s.skills LIKE ? ORDER BY u.name LIMIT 5", ctx + "/admin/student?id=", like, 3, null);
                        query(c, hits, "Drives", "SELECT j.id, j.title, CONCAT(co.name,' · ',j.ctc_lpa,' LPA · ',j.status) FROM jobs j JOIN companies co ON co.id = j.company_id "
                                + "WHERE j.title LIKE ? OR co.name LIKE ? OR j.required_skills LIKE ? ORDER BY j.deadline DESC LIMIT 5", ctx + "/admin/applicants?job=", like, 3, null);
                        query(c, hits, "Recruiters", "SELECT co.id, co.name, CONCAT(COALESCE(co.industry,''),' · ',COALESCE(co.location,'')) FROM companies co "
                                + "WHERE co.name LIKE ? OR co.industry LIKE ? ORDER BY co.name LIMIT 4", ctx + "/admin/companies#c", like, 2, null);
                    }
                    case "COMPANY" -> {
                        Integer cid = (Integer) s.getAttribute("companyId");
                        query(c, hits, "Your drives", "SELECT j.id, j.title, CONCAT(j.ctc_lpa,' LPA · ',j.status) FROM jobs j WHERE j.company_id = ? AND j.title LIKE ? ORDER BY j.deadline DESC LIMIT 5",
                                ctx + "/company/applicants?job=", like, 1, cid);
                        query(c, hits, "Applicants", "SELECT a.job_id, u.name, CONCAT(j.title,' · ',a.status,' · CGPA ',s.cgpa) FROM applications a JOIN jobs j ON j.id = a.job_id "
                                + "JOIN students s ON s.id = a.student_id JOIN users u ON u.id = s.user_id WHERE j.company_id = ? AND (u.name LIKE ? OR s.roll_no LIKE ? OR s.skills LIKE ?) ORDER BY u.name LIMIT 6",
                                ctx + "/company/applicants?job=", like, 3, cid);
                    }
                    case "STUDENT" -> query(c, hits, "Open drives", "SELECT j.id, j.title, CONCAT(co.name,' · ',j.ctc_lpa,' LPA · closes ',DATE_FORMAT(j.deadline,'%d %b')) FROM jobs j JOIN companies co ON co.id = j.company_id "
                            + "WHERE j.status = 'OPEN' AND j.deadline >= CURDATE() AND (j.title LIKE ? OR co.name LIKE ? OR j.required_skills LIKE ? OR j.location LIKE ?) ORDER BY j.deadline LIMIT 8",
                            ctx + "/student/job?id=", like, 4, null);
                    default -> { }
                }
            }
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < hits.size(); i++) {
            Hit h = hits.get(i);
            if (i > 0) json.append(',');
            json.append("{\"group\":").append(str(h.group())).append(",\"label\":").append(str(h.label()))
                .append(",\"sub\":").append(str(h.sub())).append(",\"href\":").append(str(h.href())).append('}');
        }
        json.append(']');
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(json.toString());
    }

    /** Runs a search query: optional leading int param (company id), then `likes` copies of the LIKE term. */
    private static void query(Connection c, List<Hit> out, String group, String sql, String hrefPrefix,
                              String like, int likes, Integer leadingId) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            if (leadingId != null) ps.setInt(i++, leadingId);
            for (int k = 0; k < likes; k++) ps.setString(i++, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(new Hit(group, rs.getString(2), rs.getString(3), hrefPrefix + rs.getInt(1)));
            }
        }
    }

    private static String str(String v) {
        if (v == null) return "\"\"";
        StringBuilder b = new StringBuilder("\"");
        for (char ch : v.toCharArray()) {
            switch (ch) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                case '<' -> b.append("\\u003c");
                default -> { if (ch < 0x20) b.append(String.format("\\u%04x", (int) ch)); else b.append(ch); }
            }
        }
        return b.append('"').toString();
    }
}
