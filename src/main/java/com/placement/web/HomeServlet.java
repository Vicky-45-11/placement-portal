package com.placement.web;

import com.placement.dao.JobDao;
import com.placement.dao.ReportDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Public landing page at "/". Logged-in users go straight to their dashboard. */
@WebServlet("")
public class HomeServlet extends BaseServlet {
    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        HttpSession s = req.getSession(false);
        String role = s == null ? null : (String) s.getAttribute("role");
        if (role != null) {
            redirect(req, res, "/" + role.toLowerCase() + "/");
            return;
        }
        req.setAttribute("stats", new ReportDao().summary());
        req.setAttribute("jobs", new JobDao().listOpen());
        req.setAttribute("offers", new ReportDao().recentOffers(12));
        render(req, res, "common/home");
    }
}
