package com.clinic.dao;

import com.clinic.model.DoctorAvailability;
import com.clinic.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAO {

    public boolean addSlot(DoctorAvailability slot) {
        String sql = "INSERT INTO doctor_availability (doctor_id, slot_date, start_time, end_time, status) VALUES (?, ?, ?, ?, 'AVAILABLE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slot.getDoctorId());
            stmt.setDate(2, slot.getSlotDate());
            stmt.setTime(3, slot.getStartTime());
            stmt.setTime(4, slot.getEndTime());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeSlot(int slotId, int doctorId) {
        // Only allow removing if slot is AVAILABLE and belongs to doctor (or doctorId == 0 for admin override)
        String sql;
        if (doctorId > 0) {
            sql = "DELETE FROM doctor_availability WHERE slot_id = ? AND doctor_id = ? AND status = 'AVAILABLE'";
        } else {
            sql = "DELETE FROM doctor_availability WHERE slot_id = ? AND status = 'AVAILABLE'";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            if (doctorId > 0) {
                stmt.setInt(2, doctorId);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DoctorAvailability> getSlotsByDoctorId(int doctorId) {
        List<DoctorAvailability> list = new ArrayList<>();
        String sql = "SELECT da.*, d.name AS doctor_name, d.specialization FROM doctor_availability da " +
                     "JOIN doctors d ON da.doctor_id = d.doctor_id " +
                     "WHERE da.doctor_id = ? ORDER BY da.slot_date ASC, da.start_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSlot(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DoctorAvailability> getAvailableSlotsByDoctorId(int doctorId) {
        List<DoctorAvailability> list = new ArrayList<>();
        String sql = "SELECT da.*, d.name AS doctor_name, d.specialization FROM doctor_availability da " +
                     "JOIN doctors d ON da.doctor_id = d.doctor_id " +
                     "WHERE da.doctor_id = ? AND da.status = 'AVAILABLE' AND da.slot_date >= CURDATE() " +
                     "ORDER BY da.slot_date ASC, da.start_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSlot(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public DoctorAvailability getSlotById(int slotId) {
        String sql = "SELECT da.*, d.name AS doctor_name, d.specialization FROM doctor_availability da " +
                     "JOIN doctors d ON da.doctor_id = d.doctor_id WHERE da.slot_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSlot(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DoctorAvailability mapSlot(ResultSet rs) throws SQLException {
        DoctorAvailability slot = new DoctorAvailability();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setDoctorId(rs.getInt("doctor_id"));
        slot.setSlotDate(rs.getDate("slot_date"));
        slot.setStartTime(rs.getTime("start_time"));
        slot.setEndTime(rs.getTime("end_time"));
        slot.setStatus(rs.getString("status"));
        slot.setDoctorName(rs.getString("doctor_name"));
        slot.setDoctorSpecialization(rs.getString("specialization"));
        return slot;
    }
}
