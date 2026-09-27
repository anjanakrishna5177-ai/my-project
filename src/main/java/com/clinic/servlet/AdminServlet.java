package com.clinic.servlet;

import com.clinic.dao.*;
import com.clinic.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    private AdminDAO adminDAO = new AdminDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private AvailabilityDAO availabilityDAO = new AvailabilityDAO();

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
            case "/toggleDoctor":
                toggleDoctor(request, response);
                break;
            case "/approveDoctor":
                approveDoctor(request, response);
                break;
            case "/rejectDoctor":
                rejectDoctor(request, response);
                break;
            case "/removeSlot":
                removeSlot(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        switch (pathInfo) {
            case "/addDoctor":
                addDoctor(request, response);
                break;
            case "/editDoctor":
                editDoctor(request, response);
                break;
            case "/addSlot":
                addSlot(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                break;
        }
    }

    private void loadDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchQuery = request.getParameter("search");
        List<Doctor> doctors;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            doctors = doctorDAO.searchDoctors(searchQuery.trim());
            request.setAttribute("searchQuery", searchQuery);
        } else {
            doctors = doctorDAO.getAllDoctors();
        }

        List<Doctor> pendingDoctors = doctorDAO.getPendingDoctors();
        Map<String, Integer> stats = adminDAO.getDashboardStats();
        List<Patient> patients = patientDAO.getAllPatients();
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        List<Appointment> reminders = appointmentDAO.getAllUpcomingReminders();

        request.setAttribute("stats", stats);
        request.setAttribute("doctors", doctors);
        request.setAttribute("pendingDoctors", pendingDoctors);
        request.setAttribute("patients", patients);
        request.setAttribute("appointments", appointments);
        request.setAttribute("reminders", reminders);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(request, response);
    }

    private void addDoctor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String specialization = request.getParameter("specialization");
        String qualifications = request.getParameter("qualifications");
        String contact = request.getParameter("contact");

        Doctor doc = new Doctor();
        doc.setName(name);
        doc.setEmail(email);
        doc.setPassword(password);
        doc.setSpecialization(specialization);
        doc.setQualifications(qualifications != null ? qualifications : "MBBS");
        doc.setContact(contact);

        boolean success = doctorDAO.addDoctor(doc);
        if (success) {
            request.getSession().setAttribute("msgSuccess", "Doctor added successfully!");
        } else {
            request.getSession().setAttribute("msgError", "Failed to add doctor. Email may already be in use.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void approveDoctor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int doctorId = Integer.parseInt(request.getParameter("id"));
        boolean success = doctorDAO.toggleDoctorStatus(doctorId, "ACTIVE");
        if (success) {
            request.getSession().setAttribute("msgSuccess", "Doctor account approved successfully!");
        } else {
            request.getSession().setAttribute("msgError", "Failed to approve doctor request.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void rejectDoctor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int doctorId = Integer.parseInt(request.getParameter("id"));
        boolean success = doctorDAO.toggleDoctorStatus(doctorId, "INACTIVE");
        if (success) {
            request.getSession().setAttribute("msgSuccess", "Doctor account request rejected.");
        } else {
            request.getSession().setAttribute("msgError", "Failed to reject doctor request.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void editDoctor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        String name = request.getParameter("name");
        String specialization = request.getParameter("specialization");
        String contact = request.getParameter("contact");

        Doctor doc = new Doctor();
        doc.setDoctorId(doctorId);
        doc.setName(name);
        doc.setSpecialization(specialization);
        doc.setContact(contact);

        boolean success = doctorDAO.updateDoctor(doc);
        if (success) {
            request.getSession().setAttribute("msgSuccess", "Doctor updated successfully!");
        } else {
            request.getSession().setAttribute("msgError", "Failed to update doctor details.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void toggleDoctor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int doctorId = Integer.parseInt(request.getParameter("id"));
        String currentStatus = request.getParameter("status");
        String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "INACTIVE" : "ACTIVE";

        boolean success = doctorDAO.toggleDoctorStatus(doctorId, newStatus);
        if (success) {
            request.getSession().setAttribute("msgSuccess", "Doctor status updated to " + newStatus + "!");
        } else {
            request.getSession().setAttribute("msgError", "Failed to update doctor status.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void addSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            Date date = Date.valueOf(request.getParameter("slotDate"));
            Time startTime = Time.valueOf(request.getParameter("startTime") + ":00");
            Time endTime = Time.valueOf(request.getParameter("endTime") + ":00");

            DoctorAvailability slot = new DoctorAvailability();
            slot.setDoctorId(doctorId);
            slot.setSlotDate(date);
            slot.setStartTime(startTime);
            slot.setEndTime(endTime);

            boolean success = availabilityDAO.addSlot(slot);
            if (success) {
                request.getSession().setAttribute("msgSuccess", "Availability slot created successfully!");
            } else {
                request.getSession().setAttribute("msgError", "Failed to create availability slot.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("msgError", "Invalid date or time format.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }

    private void removeSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int slotId = Integer.parseInt(request.getParameter("slotId"));
        boolean success = availabilityDAO.removeSlot(slotId, 0); // 0 means admin override
        if (success) {
            request.getSession().setAttribute("msgSuccess", "Slot removed successfully!");
        } else {
            request.getSession().setAttribute("msgError", "Cannot remove slot. It may be already booked.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
