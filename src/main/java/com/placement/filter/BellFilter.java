package com.placement.filter;

import com.placement.dao.NotificationDao;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/** Loads the unread count and latest notifications for the header bell on every signed-in page. */
public class BellFilter implements Filter {
    private final NotificationDao dao = new NotificationDao();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession s = req.getSession(false);
        Object uid = s == null ? null : s.getAttribute("userId");
        String path = req.getServletPath();
        if (uid instanceof Integer id && !path.startsWith("/assets/") && !path.equals("/search") && "GET".equals(req.getMethod())) {
            try {
                req.setAttribute("bellUnread", dao.unreadCount(id));
                req.setAttribute("bellItems", dao.latest(id, 6));
            } catch (Exception e) {
                req.setAttribute("bellUnread", 0);
                req.setAttribute("bellItems", List.of());
            }
        }
        chain.doFilter(request, response);
    }
}
