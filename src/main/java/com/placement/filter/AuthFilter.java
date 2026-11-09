package com.placement.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Role-based access: /admin/* needs ADMIN, /student/* needs STUDENT, /company/* needs COMPANY.
 * /account and /resume only need a logged-in user (the servlet checks finer rules).
 */
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String role = session == null ? null : (String) session.getAttribute("role");

        if (role == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String path = req.getServletPath();
        String required = path.startsWith("/admin") ? "ADMIN"
                        : path.startsWith("/student") ? "STUDENT"
                        : path.startsWith("/company") ? "COMPANY" : null;
        if (required != null && !required.equals(role)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have access to this page.");
            return;
        }
        res.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
