package com.clinic.dao;

import com.clinic.model.Appointment;
import com.clinic.model.DoctorAvailability;
import com.clinic.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public boolean bookAppointment(int patientId, int doctorId, int slotId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Fetch and check availability slot with row lock
            String checkSlotSql = "SELECT * FROM doctor_availability WHERE slot_id = ? AND doctor_id = ? AND status = 'AVAILABLE' FOR UPDATE";
            Date slotDate = null;
            Time startTime = null;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSlotSql)) {
                checkStmt.setInt(1, slotId);
                checkStmt.setInt(2, doctorId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        slotDate = rs.getDate("slot_date");
                        startTime = rs.getTime("start_time");
                    } else {
                        conn.rollback();
                        return false; // Slot not available or wrong doctor
                    }
                }
            }

            // 2. Mark slot as BOOKED
            String updateSlotSql = "UPDATE doctor_availability SET status = 'BOOKED' WHERE slot_id = ?";
            try (PreparedStatement uStmt = conn.prepareStatement(updateSlotSql)) {
                uStmt.setInt(1, slotId);
                uStmt.executeUpdate();
            }

            // 3. Insert Appointment record
            String insertApptSql = "INSERT INTO appointments (patient_id, doctor_id, slot_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, 'SCHEDULED')";
            try (PreparedStatement apptStmt = conn.prepareStatement(insertApptSql)) {
                apptStmt.setInt(1, patientId);
                apptStmt.setInt(2, doctorId);
                apptStmt.setInt(3, slotId);
                apptStmt.setDate(4, slotDate);
                apptStmt.setTime(5, startTime);
                apptStmt.executeUpdate();
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

    public boolean rescheduleAppointment(int appointmentId, int newSlotId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Fetch current appointment details
            String apptSql = "SELECT * FROM appointments WHERE appointment_id = ? FOR UPDATE";
            int oldSlotId = -1;
            int doctorId = -1;
            int patientId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(apptSql)) {
                stmt.setInt(1, appointmentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        oldSlotId = rs.getInt("slot_id");
                        doctorId = rs.getInt("doctor_id");
                        patientId = rs.getInt("patient_id");
                        String currentStatus = rs.getString("status");
                        if ("CANCELLED".equals(currentStatus) || "COMPLETED".equals(currentStatus)) {
                            conn.rollback();
                            return false;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Lock and verify new slot for the same doctor
            String checkNewSlotSql = "SELECT * FROM doctor_availability WHERE slot_id = ? AND doctor_id = ? AND status = 'AVAILABLE' FOR UPDATE";
            Date newDate = null;
            Time newTime = null;
            try (PreparedStatement stmt = conn.prepareStatement(checkNewSlotSql)) {
                stmt.setInt(1, newSlotId);
                stmt.setInt(2, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        newDate = rs.getDate("slot_date");
                        newTime = rs.getTime("start_time");
                    } else {
                        conn.rollback();
                        return false; // New slot not available
                    }
                }
            }

            // 3. Free old slot
            String freeOldSql = "UPDATE doctor_availability SET status = 'AVAILABLE' WHERE slot_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(freeOldSql)) {
                stmt.setInt(1, oldSlotId);
                stmt.executeUpdate();
            }

            // 4. Book new slot
            String bookNewSql = "UPDATE doctor_availability SET status = 'BOOKED' WHERE slot_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(bookNewSql)) {
                stmt.setInt(1, newSlotId);
                stmt.executeUpdate();
            }

            // 5. Update appointment record
            String updateApptSql = "UPDATE appointments SET slot_id = ?, appointment_date = ?, appointment_time = ?, status = 'RESCHEDULED' WHERE appointment_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateApptSql)) {
                stmt.setInt(1, newSlotId);
                stmt.setDate(2, newDate);
                stmt.setTime(3, newTime);
                stmt.setInt(4, appointmentId);
                stmt.executeUpdate();
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

    public boolean cancelAppointment(int appointmentId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Fetch appointment slot_id
            String apptSql = "SELECT slot_id, status FROM appointments WHERE appointment_id = ? FOR UPDATE";
            int slotId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(apptSql)) {
                stmt.setInt(1, appointmentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        slotId = rs.getInt("slot_id");
                        String status = rs.getString("status");
                        if ("CANCELLED".equals(status)) {
                            conn.rollback();
                            return false;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Set appointment status to CANCELLED
            String cancelSql = "UPDATE appointments SET status = 'CANCELLED' WHERE appointment_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(cancelSql)) {
                stmt.setInt(1, appointmentId);
                stmt.executeUpdate();
            }

            // 3. Free doctor availability slot
            String freeSlotSql = "UPDATE doctor_availability SET status = 'AVAILABLE' WHERE slot_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(freeSlotSql)) {
                stmt.setInt(1, slotId);
                stmt.executeUpdate();
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

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name AS doctor_name, d.specialization, d.contact AS doctor_contact, p.name AS patient_name, p.contact AS patient_contact " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name AS doctor_name, d.specialization, d.contact AS doctor_contact, p.name AS patient_name, p.contact AS patient_contact " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.doctor_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name AS doctor_name, d.specialization, d.contact AS doctor_contact, p.name AS patient_name, p.contact AS patient_contact " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getUpcomingRemindersForPatient(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name AS doctor_name, d.specialization, d.contact AS doctor_contact, p.name AS patient_name, p.contact AS patient_contact " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.patient_id = ? AND a.status IN ('SCHEDULED', 'RESCHEDULED') " +
                     "AND TIMESTAMP(a.appointment_date, a.appointment_time) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 24 HOUR) " +
                     "ORDER BY a.appointment_date ASC, a.appointment_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appt = mapAppointment(rs);
                    appt.setReminder(true);
                    list.add(appt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getUpcomingRemindersForDoctor(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name AS doctor_name, d.specialization, d.contact AS doctor_contact, p.name AS patient_name, p.contact AS patient_contact " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.doctor_id = ? AND a.status IN ('SCHEDULED', 'RESCHEDULED') " +
                     "AND TIMESTAMP(a.appointment_date, a.appointment_time) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 24 HOUR) " +
                     "ORDER BY a.appointment_date ASC, a.appointment_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appt = mapAppointment(rs);
                    appt.setReminder(true);
                    list.add(appt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getAllUpcomingReminders() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name AS doctor_name, d.specialization, d.contact AS doctor_contact, p.name AS patient_name, p.contact AS patient_contact " +
                     "FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE a.status IN ('SCHEDULED', 'RESCHEDULED') " +
                     "AND TIMESTAMP(a.appointment_date, a.appointment_time) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 24 HOUR) " +
                     "ORDER BY a.appointment_date ASC, a.appointment_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment appt = mapAppointment(rs);
                appt.setReminder(true);
                list.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalAppointmentsCount() {
        String sql = "SELECT COUNT(*) FROM appointments";
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

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment appt = new Appointment();
        appt.setAppointmentId(rs.getInt("appointment_id"));
        appt.setPatientId(rs.getInt("patient_id"));
        appt.setDoctorId(rs.getInt("doctor_id"));
        appt.setSlotId(rs.getInt("slot_id"));
        appt.setAppointmentDate(rs.getDate("appointment_date"));
        appt.setAppointmentTime(rs.getTime("appointment_time"));
        appt.setStatus(rs.getString("status"));
        appt.setCreatedAt(rs.getTimestamp("created_at"));
        appt.setPatientName(rs.getString("patient_name"));
        appt.setPatientContact(rs.getString("patient_contact"));
        appt.setDoctorName(rs.getString("doctor_name"));
        appt.setDoctorSpecialization(rs.getString("specialization"));
        appt.setDoctorContact(rs.getString("doctor_contact"));
        return appt;
    }
}
