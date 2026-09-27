package com.clinic.dao;

import com.clinic.model.Patient;
import com.clinic.util.DBConnection;
import com.clinic.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private UserDAO userDAO = new UserDAO();

    public boolean registerPatient(Patient patient) {
        if (userDAO.isEmailRegistered(patient.getEmail())) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert User record
            String userSql = "INSERT INTO users (email, password, role) VALUES (?, ?, 'PATIENT')";
            int userId = -1;
            try (PreparedStatement uStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                uStmt.setString(1, patient.getEmail());
                uStmt.setString(2, PasswordUtil.hashPassword(patient.getPassword()));
                uStmt.executeUpdate();

                try (ResultSet rs = uStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            if (userId <= 0) {
                conn.rollback();
                return false;
            }

            // 2. Insert Patient record
            String patientSql = "INSERT INTO patients (user_id, name, age, contact) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pStmt = conn.prepareStatement(patientSql)) {
                pStmt.setInt(1, userId);
                pStmt.setString(2, patient.getName());
                pStmt.setInt(3, patient.getAge());
                pStmt.setString(4, patient.getContact());
                pStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public Patient getPatientByUserId(int userId) {
        String sql = "SELECT p.*, u.email FROM patients p JOIN users u ON p.user_id = u.user_id WHERE p.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Patient p = new Patient();
                    p.setPatientId(rs.getInt("patient_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setName(rs.getString("name"));
                    p.setAge(rs.getInt("age"));
                    p.setContact(rs.getString("contact"));
                    p.setEmail(rs.getString("email"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Patient getPatientById(int patientId) {
        String sql = "SELECT p.*, u.email FROM patients p JOIN users u ON p.user_id = u.user_id WHERE p.patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Patient p = new Patient();
                    p.setPatientId(rs.getInt("patient_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setName(rs.getString("name"));
                    p.setAge(rs.getInt("age"));
                    p.setContact(rs.getString("contact"));
                    p.setEmail(rs.getString("email"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT p.*, u.email FROM patients p JOIN users u ON p.user_id = u.user_id ORDER BY p.patient_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Patient p = new Patient();
                p.setPatientId(rs.getInt("patient_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                p.setContact(rs.getString("contact"));
                p.setEmail(rs.getString("email"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalPatientsCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
