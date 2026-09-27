package com.clinic.servlet;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.AvailabilityDAO;
import com.clinic.dao.DoctorDAO;
import com.clinic.dao.PatientDAO;
import com.clinic.model.Appointment;
import com.clinic.model.Doctor;
import com.clinic.model.DoctorAvailability;
import com.clinic.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/patient/*")
public class PatientServlet extends HttpServlet {

    private PatientDAO patientDAO = new PatientDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private AvailabilityDAO availabilityDAO = new AvailabilityDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            pathInfo = "/dashboard";
        }

        switch (pathInfo) {
            case "/dashboard":
                loadDashboard(request, response);
                break;
            case "/cancelAppointment":
                cancelAppointment(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/patient/dashboard");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        switch (pathInfo) {
            case "/bookAppointment":
                bookAppointment(request, response);
                break;
            case "/rescheduleAppointment":
                rescheduleAppointment(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/patient/dashboard");
                break;
        }
    }

    private void loadDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Patient patient = (Patient) session.getAttribute("patient");

        if (patient == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or patient profile not found. Please log in again.");
            return;
        }

        // Refresh patient profile
        Patient freshPatient = patientDAO.getPatientById(patient.getPatientId());
        session.setAttribute("patient", freshPatient);

        String searchQuery = request.getParameter("search");
        List<Doctor> doctors;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            doctors = doctorDAO.searchDoctors(searchQuery.trim());
            request.setAttribute("searchQuery", searchQuery);
        } else {
            doctors = doctorDAO.getAllActiveDoctors();
        }

        // Selected doctor for slot display
        String docIdParam = request.getParameter("selectedDoctorId");
        if (docIdParam != null && !docIdParam.isEmpty()) {
            try {
                int docId = Integer.parseInt(docIdParam);
                List<DoctorAvailability> availableSlots = availabilityDAO.getAvailableSlotsByDoctorId(docId);
                request.setAttribute("availableSlots", availableSlots);
                request.setAttribute("selectedDoctorId", docId);
            } catch (NumberFormatException ignored) {}
        }

        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(patient.getPatientId());
        List<Appointment> reminders = appointmentDAO.getUpcomingRemindersForPatient(patient.getPatientId());

        request.setAttribute("doctors", doctors);
        request.setAttribute("appointments", appointments);
        request.setAttribute("reminders", reminders);

        request.getRequestDispatcher("/WEB-INF/jsp/patient/dashboard.jsp").forward(request, response);
    }

    private void bookAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Patient patient = (Patient) session.getAttribute("patient");

        if (patient == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or patient profile not found. Please log in again.");
            return;
        }

        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            int slotId = Integer.parseInt(request.getParameter("slotId"));

            boolean success = appointmentDAO.bookAppointment(patient.getPatientId(), doctorId, slotId);
            if (success) {
                session.setAttribute("msgSuccess", "Appointment booked successfully!");
            } else {
                session.setAttribute("msgError", "Failed to book appointment. The slot may no longer be available.");
            }
        } catch (Exception e) {
            session.setAttribute("msgError", "Invalid request parameters.");
        }

        response.sendRedirect(request.getContextPath() + "/patient/dashboard");
    }

    private void rescheduleAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Patient patient = (Patient) session.getAttribute("patient");

        if (patient == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or patient profile not found. Please log in again.");
            return;
        }

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            int newSlotId = Integer.parseInt(request.getParameter("newSlotId"));

            boolean success = appointmentDAO.rescheduleAppointment(appointmentId, newSlotId);
            if (success) {
                session.setAttribute("msgSuccess", "Appointment rescheduled successfully!");
            } else {
                session.setAttribute("msgError", "Failed to reschedule appointment. Slot may be unavailable.");
            }
        } catch (Exception e) {
            session.setAttribute("msgError", "Invalid parameters for rescheduling.");
        }

        response.sendRedirect(request.getContextPath() + "/patient/dashboard");
    }

    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Patient patient = (Patient) session.getAttribute("patient");

        if (patient == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or patient profile not found. Please log in again.");
            return;
        }

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            boolean success = appointmentDAO.cancelAppointment(appointmentId);

            if (success) {
                session.setAttribute("msgSuccess", "Appointment cancelled successfully.");
            } else {
                session.setAttribute("msgError", "Unable to cancel appointment.");
            }
        } catch (Exception e) {
            session.setAttribute("msgError", "Invalid appointment ID.");
        }

        response.sendRedirect(request.getContextPath() + "/patient/dashboard");
    }
}
