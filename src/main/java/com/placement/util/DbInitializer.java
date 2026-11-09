package com.placement.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

/**
 * For hosted deployments (DB_AUTO_INIT=true): if the database has no `users` table yet,
 * runs schema.sql once to create the tables and sample data. Statements that only make
 * sense on your own MySQL server (DROP/CREATE DATABASE, USE, CREATE USER, GRANT) are skipped,
 * so the hosted database name and user are used as they are.
 */
@WebListener
public class DbInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (!Config.autoInitDb()) return;
        try (Connection c = DB.get(); Statement st = c.createStatement()) {
            try (ResultSet rs = c.getMetaData().getTables(c.getCatalog(), null, "users", new String[]{"TABLE"})) {
                if (rs.next()) {
                    event.getServletContext().log("DbInitializer: tables already exist, skipping setup.");
                    return;
                }
            }
            String sql;
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
                if (in == null) throw new IllegalStateException("schema.sql missing from the WAR");
                sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            int count = 0;
            for (String raw : stripComments(sql).split(";\\s*\\n")) {
                String stmt = raw.trim();
                if (stmt.isEmpty() || isServerLevel(stmt)) continue;
                st.execute(stmt);
                count++;
            }
            event.getServletContext().log("DbInitializer: created schema and sample data (" + count + " statements).");
        } catch (Exception e) {
            event.getServletContext().log("DbInitializer: database setup failed", e);
        }
    }

    private static String stripComments(String s) {
        StringBuilder out = new StringBuilder();
        for (String line : s.split("\n")) {
            String t = line.trim();
            if (t.startsWith("--")) continue;
            int idx = line.indexOf(" -- ");
            out.append(idx >= 0 ? line.substring(0, idx) : line).append('\n');
        }
        return out.toString();
    }

    private static boolean isServerLevel(String stmt) {
        String s = stmt.toUpperCase(Locale.ROOT);
        return s.startsWith("DROP DATABASE") || s.startsWith("CREATE DATABASE") || s.startsWith("USE ")
                || s.startsWith("CREATE USER") || s.startsWith("GRANT") || s.startsWith("FLUSH");
    }
}
