package com.clinic.dao;

import java.util.HashMap;
import java.util.Map;

public class AdminDAO {
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalDoctors", doctorDAO.getTotalDoctorsCount());
        stats.put("totalPatients", patientDAO.getTotalPatientsCount());
        stats.put("totalAppointments", appointmentDAO.getTotalAppointmentsCount());
        return stats;
    }
}
