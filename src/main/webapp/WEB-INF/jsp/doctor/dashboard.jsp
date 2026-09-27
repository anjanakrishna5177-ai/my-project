<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.clinic.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Doctor Portal - ClinicCare</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <% Doctor doc = (Doctor) session.getAttribute("doctor"); %>

    <!-- Header Navbar -->
    <header class="app-navbar">
        <a href="${pageContext.request.contextPath}/doctor/dashboard" class="brand-logo">
            <i class="fa-solid fa-heart-pulse"></i> Doctor Portal
        </a>
        <div class="nav-user">
            <span class="user-badge"><i class="fa-solid fa-stethoscope"></i> <%= (doc != null) ? doc.getName() : "Doctor" %></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">
                <i class="fa-solid fa-right-from-bracket"></i> Logout
            </a>
        </div>
    </header>

    <main class="dashboard-container">
        <!-- Toast Messages -->
        <% String msgSuccess = (String) session.getAttribute("msgSuccess");
           if (msgSuccess != null) {
               session.removeAttribute("msgSuccess"); %>
            <div class="alert alert-success alert-dismissible">
                <i class="fa-solid fa-circle-check"></i> <%= msgSuccess %>
            </div>
        <% } %>

        <% String msgError = (String) session.getAttribute("msgError");
           if (msgError != null) {
               session.removeAttribute("msgError"); %>
            <div class="alert alert-error alert-dismissible">
                <i class="fa-solid fa-triangle-exclamation"></i> <%= msgError %>
            </div>
        <% } %>

        <div class="dashboard-header" style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1>Welcome, <%= (doc != null) ? doc.getName() : "Doctor" %></h1>
                <p>Specialization: <strong><%= (doc != null) ? doc.getSpecialization() : "" %></strong> | ID: <strong>#DOC-<%= (doc != null) ? doc.getDoctorId() : "" %></strong></p>
            </div>
            <button class="btn-primary" style="width: auto; padding: 0.75rem 1.5rem;" onclick="openModal('addSlotModal')">
                <i class="fa-solid fa-clock-rotate-left"></i> Add Available Slot
            </button>
        </div>

        <!-- 24-Hour Reminders -->
        <% List<Appointment> reminders = (List<Appointment>) request.getAttribute("reminders");
           if (reminders != null && !reminders.isEmpty()) { %>
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.1rem; margin-bottom: 0.75rem; color: #b45309;">
                    <i class="fa-solid fa-bell"></i> Upcoming 24-Hour Reminders (<%= reminders.size() %>)
                </h3>
                <% for (Appointment r : reminders) { %>
                    <div class="reminder-card">
                        <div class="reminder-icon"><i class="fa-solid fa-clock"></i></div>
                        <div>
                            <strong>Upcoming Appointment:</strong> You have a consultation with <strong><%= r.getPatientName() %></strong> (Contact: <%= r.getPatientContact() %>) tomorrow on <strong><%= r.getAppointmentDate() %></strong> at <strong><%= r.getAppointmentTime() %></strong>.
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

        <!-- Tab Controls -->
        <div class="tabs-nav">
            <button class="tab-btn active" data-tab="my-appointments"><i class="fa-solid fa-calendar-check"></i> My Appointments</button>
            <button class="tab-btn" data-tab="my-slots"><i class="fa-solid fa-clock"></i> Available Slots</button>
            <button class="tab-btn" data-tab="my-patients"><i class="fa-solid fa-hospital-user"></i> My Patients</button>
            <button class="tab-btn" data-tab="my-profile"><i class="fa-solid fa-user-doctor"></i> Profile Info</button>
        </div>

        <!-- TAB 1: APPOINTMENTS -->
        <div id="my-appointments" class="tab-content active">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Appt ID</th>
                            <th>Patient Name</th>
                            <th>Contact</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
                           if (appointments != null && !appointments.isEmpty()) {
                               for (Appointment a : appointments) { %>
                                <tr>
                                    <td>#APT-<%= a.getAppointmentId() %></td>
                                    <td><strong><%= a.getPatientName() %></strong></td>
                                    <td><%= a.getPatientContact() %></td>
                                    <td><%= a.getAppointmentDate() %></td>
                                    <td><%= a.getAppointmentTime() %></td>
                                    <td>
                                        <span class="badge badge-<%= a.getStatus().toLowerCase() %>">
                                            <%= a.getStatus() %>
                                        </span>
                                    </td>
                                </tr>
                        <%     }
                           } else { %>
                                <tr>
                                    <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2rem;">No appointments scheduled for you yet.</td>
                                </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- TAB 2: SLOTS -->
        <div id="my-slots" class="tab-content">
            <div class="slots-grid">
                <% List<DoctorAvailability> slots = (List<DoctorAvailability>) request.getAttribute("slots");
                   if (slots != null && !slots.isEmpty()) {
                       for (DoctorAvailability slot : slots) { %>
                        <div class="slot-card <%= slot.getStatus().toLowerCase() %>">
                            <div class="slot-time"><%= slot.getStartTime() %> - <%= slot.getEndTime() %></div>
                            <div class="slot-date"><i class="fa-regular fa-calendar"></i> <%= slot.getSlotDate() %></div>
                            <div style="margin-top: 0.75rem;">
                                <span class="badge badge-<%= slot.getStatus().toLowerCase() %>"><%= slot.getStatus() %></span>
                            </div>
                            <% if ("AVAILABLE".equals(slot.getStatus())) { %>
                                <div style="margin-top: 0.75rem;">
                                    <a href="${pageContext.request.contextPath}/doctor/deleteSlot?slotId=<%= slot.getSlotId() %>" class="btn-danger-sm">
                                        <i class="fa-solid fa-trash"></i> Delete Slot
                                    </a>
                                </div>
                            <% } %>
                        </div>
                <%     }
                   } else { %>
                        <p style="grid-column: 1 / -1; color: var(--text-muted);">No availability slots created yet. Click "Add Available Slot" above.</p>
                <% } %>
            </div>
        </div>

        <!-- TAB 3: PATIENTS -->
        <div id="my-patients" class="tab-content">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Patient ID</th>
                            <th>Name</th>
                            <th>Age</th>
                            <th>Contact</th>
                            <th>Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Patient> patients = (List<Patient>) request.getAttribute("patients");
                           if (patients != null && !patients.isEmpty()) {
                               for (Patient p : patients) { %>
                                <tr>
                                    <td>#PAT-<%= p.getPatientId() %></td>
                                    <td><strong><%= p.getName() %></strong></td>
                                    <td><%= p.getAge() %> yrs</td>
                                    <td><%= p.getContact() %></td>
                                    <td><%= p.getEmail() %></td>
                                </tr>
                        <%     }
                           } else { %>
                                <tr>
                                    <td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No patient records associated with your appointments yet.</td>
                                </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- TAB 4: PROFILE -->
        <div id="my-profile" class="tab-content">
            <div style="background: white; border-radius: 16px; border: 1px solid var(--border); padding: 2rem; max-width: 500px;">
                <h3 style="margin-bottom: 1.5rem;"><i class="fa-solid fa-address-card"></i> Doctor Profile Information</h3>
                <div class="form-group">
                    <label class="form-label">Doctor ID</label>
                    <input type="text" class="form-control" value="#DOC-<%= (doc != null) ? doc.getDoctorId() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Name</label>
                    <input type="text" class="form-control" value="<%= (doc != null) ? doc.getName() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Specialization</label>
                    <input type="text" class="form-control" value="<%= (doc != null) ? doc.getSpecialization() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Contact Phone</label>
                    <input type="text" class="form-control" value="<%= (doc != null) ? doc.getContact() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Email Address</label>
                    <input type="text" class="form-control" value="<%= (doc != null) ? doc.getEmail() : "" %>" readonly>
                </div>
            </div>
        </div>

    </main>

    <!-- Modal: Add Available Time Slot -->
    <div id="addSlotModal" class="modal-backdrop">
        <div class="modal-box">
            <div class="modal-header">
                <h3><i class="fa-solid fa-clock"></i> Add Available Time Slot</h3>
                <button class="modal-close" onclick="closeModal('addSlotModal')">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/doctor/addSlot" method="post">
                <div class="form-group">
                    <label class="form-label">Slot Date</label>
                    <input type="date" name="slotDate" class="form-control" required>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Start Time</label>
                        <input type="time" name="startTime" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">End Time</label>
                        <input type="time" name="endTime" class="form-control" required>
                    </div>
                </div>
                <button type="submit" class="btn-primary" style="margin-top: 1rem;"><i class="fa-solid fa-calendar-check"></i> Save Availability Slot</button>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
