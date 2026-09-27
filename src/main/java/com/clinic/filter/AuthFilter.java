package com.clinic.filter;

import com.clinic.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/doctor/*", "/patient/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();

        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        if (user == null || role == null) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=Please login to access this page");
            return;
        }

        // Enforce strict role-based access control
        if (uri.contains("/admin/") && !"ADMIN".equals(role)) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=Unauthorized Access - Admin privileges required");
            return;
        }

        if (uri.contains("/doctor/") && !"DOCTOR".equals(role)) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=Unauthorized Access - Doctor account required");
            return;
        }

        if (uri.contains("/patient/") && !"PATIENT".equals(role)) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=Unauthorized Access - Patient account required");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
