<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Clinic Appointment Management System</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <a href="${pageContext.request.contextPath}/" class="brand-logo" style="justify-content: center; color: var(--secondary); margin-bottom: 1rem;">
                    <i class="fa-solid fa-heart-pulse"></i> ClinicCare
                </a>
                <h2>Welcome Back</h2>
                <p id="roleNotice">Select your role and sign in to access your dashboard</p>
            </div>

            <% String error = (String) request.getAttribute("error");
               if (error == null) error = request.getParameter("error");
               if (error != null) { %>
                <div class="alert alert-error alert-dismissible">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    <%= error %>
                </div>
            <% } %>

            <% String message = (String) request.getAttribute("message");
               if (message == null) message = request.getParameter("message");
               if (message != null) { %>
                <div class="alert alert-success alert-dismissible">
                    <i class="fa-solid fa-circle-check"></i>
                    <%= message %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label class="form-label" for="role"><i class="fa-solid fa-user-gear"></i> Select Role</label>
                    <select name="role" id="role" class="form-control" onchange="updateRoleNotice()" required>
                        <option value="PATIENT">Patient</option>
                        <option value="DOCTOR">Doctor</option>
                        <option value="ADMIN">System Admin</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="email"><i class="fa-solid fa-envelope"></i> Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="e.g. alice@gmail.com" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password"><i class="fa-solid fa-lock"></i> Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
                </div>

                <button type="submit" class="btn-primary" style="margin-top: 1rem;">
                    <i class="fa-solid fa-right-to-bracket"></i> Log In
                </button>
            </form>

            <!-- Role-Aware Account Creation Links -->
            <div style="margin-top: 1.75rem; border-top: 1px solid var(--border); padding-top: 1.25rem; display: flex; flex-direction: column; gap: 0.85rem; text-align: center; font-size: 0.9rem;">
                <div>
                    <span style="color: var(--text-muted);">Don't have a patient account?</span>
                    <a href="${pageContext.request.contextPath}/register.jsp" style="color: var(--primary); font-weight: 700; text-decoration: none; margin-left: 0.25rem;">
                        Register as Patient <i class="fa-solid fa-arrow-right"></i>
                    </a>
                </div>

                <div>
                    <span style="color: var(--text-muted);">Are you a doctor?</span>
                    <a href="${pageContext.request.contextPath}/doctor-register.jsp" style="color: var(--accent); font-weight: 700; text-decoration: none; margin-left: 0.25rem;">
                        Request Doctor Account <i class="fa-solid fa-stethoscope"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function updateRoleNotice() {
            const role = document.getElementById('role').value;
            const notice = document.getElementById('roleNotice');
            if (role === 'PATIENT') {
                notice.textContent = "Logging in as Patient. Access your appointments & schedule.";
            } else if (role === 'DOCTOR') {
                notice.textContent = "Logging in as Doctor. Access your clinic schedule & patients.";
            } else if (role === 'ADMIN') {
                notice.textContent = "Logging in as System Administrator. Manage clinic operations.";
            }
        }
        document.addEventListener('DOMContentLoaded', updateRoleNotice);
    </script>
</body>
</html>
