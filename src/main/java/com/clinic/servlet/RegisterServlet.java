package com.clinic.servlet;

import com.clinic.dao.PatientDAO;
import com.clinic.model.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private PatientDAO patientDAO = new PatientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String ageStr = request.getParameter("age");
        String contact = request.getParameter("contact");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (name == null || ageStr == null || contact == null || email == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        int age = 0;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) {
                request.setAttribute("error", "Please enter a valid age.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Age must be a valid number.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        Patient patient = new Patient();
        patient.setName(name.trim());
        patient.setAge(age);
        patient.setContact(contact.trim());
        patient.setEmail(email.trim());
        patient.setPassword(password.trim());

        boolean success = patientDAO.registerPatient(patient);

        if (success) {
            request.setAttribute("message", "Registration successful! You can now log in.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Email is already registered. Please try another or log in.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
