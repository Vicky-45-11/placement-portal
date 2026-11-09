package com.placement.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings from db.properties on the classpath, with environment
 * variables (DB_URL, DB_USER, DB_PASSWORD, UPLOAD_DIR) taking priority.
 * Nothing secret is hard-coded in the source.
 */
public final class Config {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not read db.properties: " + e.getMessage());
        }
    }

    private Config() {}

    public static String get(String key, String envName, String fallback) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) return env;
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        return PROPS.getProperty(key, fallback);
    }

    public static String dbUrl()      { return get("db.url", "DB_URL", "jdbc:mysql://localhost:3306/placement_db"); }
    public static String dbUser()     { return get("db.user", "DB_USER", "root"); }
    public static String dbPassword() { return get("db.password", "DB_PASSWORD", ""); }
    public static String uploadDir()  { return get("upload.dir", "UPLOAD_DIR", "uploads"); }
}
