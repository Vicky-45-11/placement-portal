package com.placement.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Small helpers for reading form input and flashing messages. */
public final class Web {
    private Web() {}

    public static String str(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    public static String strOrNull(HttpServletRequest req, String name) {
        String v = str(req, name);
        return v.isEmpty() ? null : v;
    }

    public static int intParam(HttpServletRequest req, String name, int fallback) {
        try { return Integer.parseInt(str(req, name)); } catch (NumberFormatException e) { return fallback; }
    }

    public static BigDecimal decimal(HttpServletRequest req, String name) {
        String v = str(req, name);
        if (v.isEmpty()) return null;
        try { return new BigDecimal(v); } catch (NumberFormatException e) { return null; }
    }

    public static LocalDate date(HttpServletRequest req, String name) {
        String v = str(req, name);
        if (v.isEmpty()) return null;
        try { return LocalDate.parse(v); } catch (Exception e) { return null; }
    }

    public static LocalDateTime dateTime(HttpServletRequest req, String name) {
        String v = str(req, name);
        if (v.isEmpty()) return null;
        try { return LocalDateTime.parse(v); } catch (Exception e) { return null; }
    }

    public static void flash(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flash", message);
    }

    public static void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    public static int currentUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        Object id = s == null ? null : s.getAttribute("userId");
        return id == null ? -1 : (Integer) id;
    }
}
