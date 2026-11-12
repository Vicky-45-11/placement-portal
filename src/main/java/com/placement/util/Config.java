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

    public static String dbUrl() {
        // Railway's MySQL plugin exposes MYSQLHOST/MYSQLPORT/MYSQLDATABASE; use them when DB_URL is not set.
        String host = System.getenv("MYSQLHOST");
        if (isBlank(System.getenv("DB_URL")) && !isBlank(host)) {
            String port = envOr("MYSQLPORT", "3306");
            String db = envOr("MYSQLDATABASE", "railway");
            return "jdbc:mysql://" + host + ":" + port + "/" + db
                    + "?useSSL=false&allowPublicKeyRetrieval=true&connectionTimeZone=SERVER";
        }
        return get("db.url", "DB_URL", "jdbc:mysql://localhost:3306/placement_db");
    }
    public static String dbUser()     { return isBlank(System.getenv("DB_USER")) && !isBlank(System.getenv("MYSQLUSER")) ? System.getenv("MYSQLUSER") : get("db.user", "DB_USER", "root"); }
    public static String dbPassword() { return isBlank(System.getenv("DB_PASSWORD")) && System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : get("db.password", "DB_PASSWORD", ""); }
    /** When true, an empty database gets the tables and sample data on startup (used for cloud hosting). */
    public static boolean autoInitDb() { return "true".equalsIgnoreCase(get("db.autoInit", "DB_AUTO_INIT", "false")); }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String envOr(String name, String fallback) { String v = System.getenv(name); return isBlank(v) ? fallback : v; }
    public static String uploadDir()  { return get("upload.dir", "UPLOAD_DIR", "uploads"); }
}
