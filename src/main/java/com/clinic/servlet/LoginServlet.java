package com.clinic.servlet;

import com.clinic.dao.DoctorDAO;
import com.clinic.dao.PatientDAO;
import com.clinic.dao.UserDAO;
import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (email == null || password == null || role == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.authenticate(email.trim(), password.trim(), role.trim().toUpperCase());

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userRole", user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if ("DOCTOR".equals(user.getRole())) {
                Doctor doctor = doctorDAO.getDoctorByUserId(user.getUserId());
                if (doctor == null) {
                    session.invalidate();
                    request.setAttribute("error", "Unable to load your profile. Please check the database and try again.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
                if ("PENDING".equalsIgnoreCase(doctor.getStatus())) {
                    session.invalidate();
                    request.setAttribute("error", "Your doctor account request is pending Administrator review and approval.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
                if ("INACTIVE".equalsIgnoreCase(doctor.getStatus())) {
                    session.invalidate();
                    request.setAttribute("error", "Your doctor account is deactivated. Please contact admin.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
                session.setAttribute("doctor", doctor);
                response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
            } else if ("PATIENT".equals(user.getRole())) {
                Patient patient = patientDAO.getPatientByUserId(user.getUserId());
                if (patient == null) {
                    session.invalidate();
                    request.setAttribute("error", "Unable to load your profile. Please check the database and try again.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }
                session.setAttribute("patient", patient);
                response.sendRedirect(request.getContextPath() + "/patient/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } else {
            request.setAttribute("error", "Invalid credentials or role selection!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
