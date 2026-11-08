package com.clinic.dao;

import com.clinic.model.User;
import com.clinic.util.DBConnection;
import com.clinic.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    // Authenticate user during login
    public User authenticate(String email, String rawPassword, String role) {

        String sql = "SELECT * FROM users WHERE email = ? AND role = ?";

        System.out.println("========== LOGIN AUTHENTICATION STARTED ==========");
        System.out.println("Email received: " + email);
        System.out.println("Role received: " + role);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("Database connection obtained successfully.");

            stmt.setString(1, email);
            stmt.setString(2, role);

            System.out.println("Executing authentication query...");

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    System.out.println("User found in database.");

                    String storedHash = rs.getString("password");

                    System.out.println("Database role: " + rs.getString("role"));
                    System.out.println("Stored password hash length: "
                            + (storedHash != null ? storedHash.length() : "null"));

                    if (storedHash != null
                            && PasswordUtil.verifyPassword(
                                    rawPassword,
                                    storedHash.trim())) {

                        System.out.println("Password verification successful.");
                        System.out.println("LOGIN SUCCESSFUL");

                        User user = new User();

                        user.setUserId(rs.getInt("user_id"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(storedHash);
                        user.setRole(rs.getString("role"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));

                        System.out.println("========== LOGIN AUTHENTICATION ENDED ==========");

                        return user;

                    } else {

                        System.out.println("Password verification FAILED.");
                    }

                } else {

                    System.out.println(
                            "No user found with this email and role combination."
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println("========== DATABASE ERROR DURING LOGIN ==========");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Error Message: " + e.getMessage());

            e.printStackTrace();

            System.err.println("================================================");

        } catch (Exception e) {

            System.err.println("========== UNEXPECTED LOGIN ERROR ==========");
            System.err.println("Error Message: " + e.getMessage());

            e.printStackTrace();

            System.err.println("=============================================");
        }

        System.out.println("LOGIN FAILED");
        System.out.println("========== LOGIN AUTHENTICATION ENDED ==========");

        return null;
    }


    // Check whether an email is already registered
    public boolean isEmailRegistered(String email) {

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "DATABASE ERROR WHILE CHECKING REGISTERED EMAIL"
            );

            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Error Message: " + e.getMessage());

            e.printStackTrace();
        }

        return false;
    }


    // Register a new user
    public int registerUser(String email,
                            String rawPassword,
                            String role) {

        String sql =
                "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            String hashedPassword =
                    PasswordUtil.hashPassword(rawPassword);

            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {

                try (ResultSet rs = stmt.getGeneratedKeys()) {

                    if (rs.next()) {

                        System.out.println(
                                "User registered successfully: " + email
                        );

                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "DATABASE ERROR DURING USER REGISTRATION"
            );

            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Error Message: " + e.getMessage());

            e.printStackTrace();
        }

        return -1;
    }
}