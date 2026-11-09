package com.placement.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Puts a random token in every session (exposed to JSPs as ${csrf}) and rejects POSTs
 * that do not send it back as the "_csrf" form field.
 */
public class CsrfFilter implements Filter {
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        String token = (String) session.getAttribute("csrf");
        if (token == null) {
            byte[] b = new byte[24];
            RANDOM.nextBytes(b);
            token = HexFormat.of().formatHex(b);
            session.setAttribute("csrf", token);
        }
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            // multipart forms carry the token in the query string (see resume upload form)
            String sent = req.getParameter("_csrf");
            if (sent == null || !MessageDigest.isEqual(sent.getBytes(), token.getBytes())) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Form expired. Go back, refresh the page and try again.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
