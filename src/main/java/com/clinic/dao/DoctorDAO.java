package com.clinic.dao;

import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.util.DBConnection;
import com.clinic.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    private UserDAO userDAO = new UserDAO();

    public boolean requestDoctorAccount(Doctor doctor) {
        if (userDAO.isEmailRegistered(doctor.getEmail())) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Create User
            String userSql = "INSERT INTO users (email, password, role) VALUES (?, ?, 'DOCTOR')";
            int userId = -1;
            try (PreparedStatement uStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                uStmt.setString(1, doctor.getEmail());
                uStmt.setString(2, PasswordUtil.hashPassword(doctor.getPassword()));
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

            // 2. Create Doctor with PENDING status
            String docSql = "INSERT INTO doctors (user_id, name, specialization, qualifications, contact, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
            try (PreparedStatement dStmt = conn.prepareStatement(docSql)) {
                dStmt.setInt(1, userId);
                dStmt.setString(2, doctor.getName());
                dStmt.setString(3, doctor.getSpecialization());
                dStmt.setString(4, doctor.getQualifications() != null ? doctor.getQualifications() : "MBBS");
                dStmt.setString(5, doctor.getContact());
                dStmt.executeUpdate();
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

    public boolean addDoctor(Doctor doctor) {
        if (userDAO.isEmailRegistered(doctor.getEmail())) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Create User
            String userSql = "INSERT INTO users (email, password, role) VALUES (?, ?, 'DOCTOR')";
            int userId = -1;
            try (PreparedStatement uStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                uStmt.setString(1, doctor.getEmail());
                uStmt.setString(2, PasswordUtil.hashPassword(doctor.getPassword()));
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

            // 2. Create Doctor with ACTIVE status
            String docSql = "INSERT INTO doctors (user_id, name, specialization, qualifications, contact, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
            try (PreparedStatement dStmt = conn.prepareStatement(docSql)) {
                dStmt.setInt(1, userId);
                dStmt.setString(2, doctor.getName());
                dStmt.setString(3, doctor.getSpecialization());
                dStmt.setString(4, doctor.getQualifications() != null ? doctor.getQualifications() : "MBBS");
                dStmt.setString(5, doctor.getContact());
                dStmt.executeUpdate();
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

    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, qualifications = ?, contact = ? WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getQualifications() != null ? doctor.getQualifications() : "MBBS");
            stmt.setString(4, doctor.getContact());
            stmt.setInt(5, doctor.getDoctorId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleDoctorStatus(int doctorId, String newStatus) {
        String sql = "UPDATE doctors SET status = ? WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, doctorId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Doctor getDoctorByUserId(int userId) {
        String sql = "SELECT d.*, u.email FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT d.*, u.email FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.*, u.email FROM doctors d JOIN users u ON d.user_id = u.user_id ORDER BY d.doctor_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Doctor> getPendingDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.*, u.email FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.status = 'PENDING' ORDER BY d.doctor_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Doctor> getAllActiveDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.*, u.email FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.status = 'ACTIVE' ORDER BY d.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Doctor> searchDoctors(String query) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.*, u.email FROM doctors d JOIN users u ON d.user_id = u.user_id " +
                     "WHERE d.status = 'ACTIVE' AND (CAST(d.doctor_id AS CHAR) LIKE ? OR LOWER(d.specialization) LIKE ? OR LOWER(d.name) LIKE ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String term = "%" + query.toLowerCase().trim() + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDoctor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Patient> getPatientsByDoctorId(int doctorId) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT DISTINCT p.*, u.email FROM patients p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "JOIN appointments a ON p.patient_id = a.patient_id " +
                     "WHERE a.doctor_id = ? ORDER BY p.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalDoctorsCount() {
        String sql = "SELECT COUNT(*) FROM doctors";
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

    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        Doctor doc = new Doctor();
        doc.setDoctorId(rs.getInt("doctor_id"));
        doc.setUserId(rs.getInt("user_id"));
        doc.setName(rs.getString("name"));
        doc.setSpecialization(rs.getString("specialization"));
        try {
            doc.setQualifications(rs.getString("qualifications"));
        } catch (SQLException ignored) {}
        doc.setContact(rs.getString("contact"));
        doc.setStatus(rs.getString("status"));
        doc.setEmail(rs.getString("email"));
        return doc;
    }
}
