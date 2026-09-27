<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Registration - Clinic Appointment Management System</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-card" style="max-width: 550px;">
            <div class="auth-header">
                <a href="${pageContext.request.contextPath}/" class="brand-logo" style="justify-content: center; color: var(--secondary); margin-bottom: 1rem;">
                    <i class="fa-solid fa-heart-pulse"></i> ClinicCare
                </a>
                <h2>Create Patient Account</h2>
                <p>Fill in your details to register as a new patient</p>
            </div>

            <% String error = (String) request.getAttribute("error");
               if (error != null) { %>
                <div class="alert alert-error alert-dismissible">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    <%= error %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-group">
                    <label class="form-label" for="name"><i class="fa-solid fa-user"></i> Full Name</label>
                    <input type="text" id="name" name="name" class="form-control" placeholder="e.g. Alice Williams" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" for="age"><i class="fa-solid fa-cake-candles"></i> Age</label>
                        <input type="number" id="age" name="age" class="form-control" min="1" max="120" placeholder="28" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="contact"><i class="fa-solid fa-phone"></i> Contact Phone</label>
                        <input type="text" id="contact" name="contact" class="form-control" placeholder="+1 555-0199" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="email"><i class="fa-solid fa-envelope"></i> Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="alice@gmail.com" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" for="password"><i class="fa-solid fa-lock"></i> Password</label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="confirmPassword"><i class="fa-solid fa-shield-check"></i> Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="••••••••" required>
                    </div>
                </div>

                <button type="submit" class="btn-primary" style="margin-top: 1rem; background: var(--accent);">
                    <i class="fa-solid fa-user-plus"></i> Complete Registration
                </button>
            </form>

            <div style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--text-muted);">
                Already have an account?
                <a href="${pageContext.request.contextPath}/login.jsp" style="color: var(--primary); font-weight: 700; text-decoration: none;">
                    Log In Here
                </a>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
