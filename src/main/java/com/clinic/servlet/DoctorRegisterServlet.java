package com.clinic.servlet;

import com.clinic.dao.DoctorDAO;
import com.clinic.model.Doctor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/registerDoctor")
public class DoctorRegisterServlet extends HttpServlet {

    private DoctorDAO doctorDAO = new DoctorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/doctor-register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String contact = request.getParameter("contact");
        String specialization = request.getParameter("specialization");
        String qualifications = request.getParameter("qualifications");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (name == null || email == null || contact == null || specialization == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            request.getRequestDispatcher("/doctor-register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match!");
            request.getRequestDispatcher("/doctor-register.jsp").forward(request, response);
            return;
        }

        Doctor doctor = new Doctor();
        doctor.setName(name.trim());
        doctor.setEmail(email.trim());
        doctor.setContact(contact.trim());
        doctor.setSpecialization(specialization.trim());
        doctor.setQualifications(qualifications != null ? qualifications.trim() : "MBBS");
        doctor.setPassword(password.trim());

        boolean success = doctorDAO.requestDoctorAccount(doctor);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/doctor-register.jsp?submitted=true");
        } else {
            request.setAttribute("error", "Email is already registered. Please try another or contact Administrator.");
            request.getRequestDispatcher("/doctor-register.jsp").forward(request, response);
        }
    }
}
