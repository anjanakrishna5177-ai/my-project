package com.clinic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static String getDbUrl() {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            return dbUrl.trim();
        }

        String host = System.getenv("DB_HOST");
        if (host != null && !host.trim().isEmpty()) {
            String port = System.getenv("DB_PORT");
            if (port == null || port.trim().isEmpty()) {
                port = "3306";
            }
            String dbName = System.getenv("DB_NAME");
            if (dbName == null || dbName.trim().isEmpty()) {
                dbName = "clinic_db";
            }
            return "jdbc:mysql://" + host.trim() + ":" + port.trim() + "/" + dbName.trim() + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }

        return System.getProperty("db.url", "jdbc:mysql://localhost:3306/clinic_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    }

    private static String getDbUser() {
        String user = System.getenv("DB_USER");
        if (user != null && !user.trim().isEmpty()) {
            return user.trim();
        }
        return System.getProperty("db.user", "clinic_app");
    }

    private static String getDbPassword() {
        String pass = System.getenv("DB_PASSWORD");
        if (pass != null) {
            return pass;
        }
        return System.getProperty("db.password", "ClinicApp@123");
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
