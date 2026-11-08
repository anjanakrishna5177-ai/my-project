package com.clinic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static String getDbUrl() {

        String dbUrl = System.getenv("DB_URL");

        if (dbUrl != null && !dbUrl.trim().isEmpty()) {

            dbUrl = dbUrl.trim();

            // Convert mysql:// to JDBC format if necessary
            if (dbUrl.startsWith("mysql://")) {
                dbUrl = "jdbc:mysql://" + dbUrl.substring("mysql://".length());
            }

            // Ensure JDBC prefix exists
            if (!dbUrl.startsWith("jdbc:mysql://")) {
                throw new IllegalStateException(
                        "DB_URL must start with jdbc:mysql://"
                );
            }

            // Add SSL requirement for Aiven if no SSL option exists
            if (!dbUrl.contains("ssl-mode=")
                    && !dbUrl.contains("sslMode=")
                    && !dbUrl.contains("useSSL=")) {

                dbUrl += dbUrl.contains("?")
                        ? "&ssl-mode=REQUIRED"
                        : "?ssl-mode=REQUIRED";
            }

            return dbUrl;
        }

        // Local development fallback
        return "jdbc:mysql://localhost:3306/clinic_db"
                + "?useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=UTC";
    }

    private static String getDbUser() {

        String user = System.getenv("DB_USER");

        if (user == null || user.trim().isEmpty()) {
            throw new IllegalStateException(
                    "DB_USER environment variable is not configured."
            );
        }

        return user.trim();
    }

    private static String getDbPassword() {

        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isEmpty()) {
            throw new IllegalStateException(
                    "DB_PASSWORD environment variable is not configured."
            );
        }

        return password;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found.",
                    e
            );
        }
    }

    public static Connection getConnection() throws SQLException {

        String url = getDbUrl();

        System.out.println("Attempting database connection...");
        System.out.println(
                "Database host configured: "
                        + url.replaceAll("://.*?@", "://***@")
        );

        Connection connection =
                DriverManager.getConnection(
                        url,
                        getDbUser(),
                        getDbPassword()
                );

        System.out.println("Database connection successful.");

        return connection;
    }
}