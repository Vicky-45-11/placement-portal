package com.placement.util;

import com.placement.dao.NotificationDao;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fire-and-forget wrappers: a failed notification is logged but never breaks the action
 * (status change, application, interview) that triggered it.
 */
public final class Notify {
    private static final Logger LOG = Logger.getLogger(Notify.class.getName());
    private static final NotificationDao DAO = new NotificationDao();
    private Notify() {}

    @FunctionalInterface private interface Send { void run() throws Exception; }

    private static void safe(Send s) {
        try { s.run(); } catch (Exception e) { LOG.log(Level.WARNING, "Could not save notification", e); }
    }

    public static void student(int applicationId, String body, String link) { safe(() -> DAO.toStudentOfApplication(applicationId, body, link)); }
    public static void company(int jobId, String body, String link)         { safe(() -> DAO.toCompanyOfJob(jobId, body, link)); }
    public static void user(int userId, String body, String link)           { safe(() -> DAO.toUser(userId, body, link)); }
    public static void admins(String body, String link)                     { safe(() -> DAO.toAdmins(body, link)); }
    public static void allStudents(String body, String link)                { safe(() -> DAO.toAllStudents(body, link)); }

    /** Human wording for a status change, as the student reads it. */
    public static String statusText(String status, String jobTitle, String company) {
        String job = jobTitle + " at " + company;
        return switch (status) {
            case "SHORTLISTED" -> "You've been shortlisted for " + job + ".";
            case "INTERVIEW"   -> "You've moved to the interview stage for " + job + ".";
            case "SELECTED"    -> "Congratulations! You've been selected for " + job + ".";
            case "REJECTED"    -> "Your application for " + job + " was not taken forward.";
            default            -> "Your application for " + job + " is back to APPLIED.";
        };
    }
}
