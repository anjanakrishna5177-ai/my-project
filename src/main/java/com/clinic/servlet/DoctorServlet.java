package com.clinic.servlet;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.AvailabilityDAO;
import com.clinic.dao.DoctorDAO;
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
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet("/doctor/*")
public class DoctorServlet extends HttpServlet {

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
            case "/deleteSlot":
                deleteSlot(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        switch (pathInfo) {
            case "/addSlot":
                addSlot(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
                break;
        }
    }

    private void loadDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Doctor doctor = (Doctor) session.getAttribute("doctor");

        if (doctor == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or doctor profile not found. Please log in again.");
            return;
        }

        // Refresh doctor profile from DB
        Doctor freshDoctor = doctorDAO.getDoctorById(doctor.getDoctorId());
        session.setAttribute("doctor", freshDoctor);

        List<DoctorAvailability> slots = availabilityDAO.getSlotsByDoctorId(doctor.getDoctorId());
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctorId(doctor.getDoctorId());
        List<Patient> patients = doctorDAO.getPatientsByDoctorId(doctor.getDoctorId());
        List<Appointment> reminders = appointmentDAO.getUpcomingRemindersForDoctor(doctor.getDoctorId());

        request.setAttribute("slots", slots);
        request.setAttribute("appointments", appointments);
        request.setAttribute("patients", patients);
        request.setAttribute("reminders", reminders);

        request.getRequestDispatcher("/WEB-INF/jsp/doctor/dashboard.jsp").forward(request, response);
    }

    private void addSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Doctor doctor = (Doctor) session.getAttribute("doctor");

        if (doctor == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or doctor profile not found. Please log in again.");
            return;
        }

        try {
            Date date = Date.valueOf(request.getParameter("slotDate"));
            Time startTime = Time.valueOf(request.getParameter("startTime") + ":00");
            Time endTime = Time.valueOf(request.getParameter("endTime") + ":00");

            DoctorAvailability slot = new DoctorAvailability();
            slot.setDoctorId(doctor.getDoctorId());
            slot.setSlotDate(date);
            slot.setStartTime(startTime);
            slot.setEndTime(endTime);

            boolean success = availabilityDAO.addSlot(slot);
            if (success) {
                session.setAttribute("msgSuccess", "Availability slot created!");
            } else {
                session.setAttribute("msgError", "Failed to create slot.");
            }
        } catch (Exception e) {
            session.setAttribute("msgError", "Invalid date or time parameters.");
        }
        response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
    }

    private void deleteSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Doctor doctor = (Doctor) session.getAttribute("doctor");

        if (doctor == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired or doctor profile not found. Please log in again.");
            return;
        }

        int slotId = Integer.parseInt(request.getParameter("slotId"));
        boolean success = availabilityDAO.removeSlot(slotId, doctor.getDoctorId());

        if (success) {
            session.setAttribute("msgSuccess", "Slot removed successfully!");
        } else {
            session.setAttribute("msgError", "Could not remove slot. It may be booked or invalid.");
        }
        response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
    }
}
