package com.placement.web;

import com.placement.dao.NotificationDao;
import com.placement.util.Web;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** The bell: full list, open one (marks it read and follows its link), mark all read. */
@WebServlet("/notifications/*")
public class NotificationServlet extends BaseServlet {
    private final NotificationDao dao = new NotificationDao();

    @Override
    protected void handle(String action, boolean post, HttpServletRequest req, HttpServletResponse res) throws Exception {
        int me = Web.currentUserId(req);
        switch (action) {
            case "" -> {
                req.setAttribute("items", dao.latest(me, 100));
                render(req, res, "common/notifications");
            }
            case "open" -> {
                String link = dao.open(Web.intParam(req, "id", 0), me);
                // only follow links inside this app
                redirect(req, res, link != null && link.startsWith("/") && !link.startsWith("//") ? link : "/notifications");
            }
            case "read-all" -> {
                if (post) dao.markAllRead(me);
                if (isAjax(req)) { res.setStatus(HttpServletResponse.SC_NO_CONTENT); return; }
                String back = Web.str(req, "back");
                redirect(req, res, back.startsWith("/") && !back.startsWith("//") ? back : "/notifications");
            }
            default -> notFound(res);
        }
    }
}
