<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.clinic.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - ClinicCare</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Header Navbar -->
    <header class="app-navbar">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="brand-logo">
            <i class="fa-solid fa-heart-pulse"></i> ClinicCare Admin
        </a>
        <div class="nav-user">
            <span class="user-badge"><i class="fa-solid fa-user-shield"></i> System Admin</span>
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

        <div class="dashboard-header">
            <h1>Admin Control Panel</h1>
            <p>Manage medical staff, patient accounts, clinic appointments & schedule slots.</p>
        </div>

        <!-- 24-Hour Reminders Banner -->
        <% List<Appointment> reminders = (List<Appointment>) request.getAttribute("reminders");
           if (reminders != null && !reminders.isEmpty()) { %>
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.1rem; margin-bottom: 0.75rem; color: #b45309;">
                    <i class="fa-solid fa-bell"></i> Upcoming 24-Hour Appointment Reminders (<%= reminders.size() %>)
                </h3>
                <% for (Appointment r : reminders) { %>
                    <div class="reminder-card">
                        <div class="reminder-icon"><i class="fa-solid fa-clock"></i></div>
                        <div>
                            <strong>Reminder:</strong> Patient <strong><%= r.getPatientName() %></strong> has an appointment scheduled with <strong><%= r.getDoctorName() %></strong> (<%= r.getDoctorSpecialization() %>) on <strong><%= r.getAppointmentDate() %></strong> at <strong><%= r.getAppointmentTime() %></strong>.
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

        <!-- Statistics Cards -->
        <% Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats"); %>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon icon-blue"><i class="fa-solid fa-user-doctor"></i></div>
                <div class="stat-info">
                    <h3><%= (stats != null) ? stats.get("totalDoctors") : 0 %></h3>
                    <p>Total Registered Doctors</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon icon-teal"><i class="fa-solid fa-hospital-user"></i></div>
                <div class="stat-info">
                    <h3><%= (stats != null) ? stats.get("totalPatients") : 0 %></h3>
                    <p>Total Registered Patients</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon icon-amber"><i class="fa-solid fa-calendar-check"></i></div>
                <div class="stat-info">
                    <h3><%= (stats != null) ? stats.get("totalAppointments") : 0 %></h3>
                    <p>Total Appointments</p>
                </div>
            </div>
        </div>

        <!-- Tab Controls -->
        <div class="tabs-nav">
            <button class="tab-btn active" data-tab="doctors-tab"><i class="fa-solid fa-user-doctor"></i> Manage Doctors</button>
            <button class="tab-btn" data-tab="patients-tab"><i class="fa-solid fa-users"></i> Registered Patients</button>
            <button class="tab-btn" data-tab="appointments-tab"><i class="fa-solid fa-calendar-days"></i> All Appointments</button>
            <button class="tab-btn" data-tab="availability-tab"><i class="fa-solid fa-clock"></i> Manage Availability Slots</button>
        </div>

        <!-- TAB 1: DOCTORS MANAGEMENT -->
        <div id="doctors-tab" class="tab-content active">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" style="display: flex; gap: 0.5rem; flex: 1; max-width: 450px;">
                    <input type="text" name="search" class="form-control" placeholder="Search by Doctor ID, Name or Specialization..." value="${searchQuery != null ? searchQuery : ''}">
                    <button type="submit" class="btn-primary" style="width: auto; padding: 0.75rem 1.25rem;"><i class="fa-solid fa-magnifying-glass"></i></button>
                </form>

                <button class="btn-primary" style="width: auto; padding: 0.75rem 1.5rem;" onclick="openModal('addDoctorModal')">
                    <i class="fa-solid fa-plus"></i> Add New Doctor
                </button>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Doc ID</th>
                            <th>Doctor Name</th>
                            <th>Specialization</th>
                            <th>Email</th>
                            <th>Contact</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
                           if (doctors != null && !doctors.isEmpty()) {
                               for (Doctor d : doctors) { %>
                                <tr>
                                    <td>#DOC-<%= d.getDoctorId() %></td>
                                    <td><strong><%= d.getName() %></strong></td>
                                    <td><span class="badge badge-scheduled"><%= d.getSpecialization() %></span></td>
                                    <td><%= d.getEmail() %></td>
                                    <td><%= d.getContact() %></td>
                                    <td>
                                        <span class="badge <%= "ACTIVE".equalsIgnoreCase(d.getStatus()) ? "badge-active" : "badge-inactive" %>">
                                            <%= d.getStatus() %>
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 0.5rem;">
                                            <a href="${pageContext.request.contextPath}/admin/toggleDoctor?id=<%= d.getDoctorId() %>&status=<%= d.getStatus() %>" class="btn-secondary" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;">
                                                <i class="fa-solid fa-power-off"></i> <%= "ACTIVE".equalsIgnoreCase(d.getStatus()) ? "Deactivate" : "Activate" %>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                        <%     }
                           } else { %>
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No doctors found.</td>
                                </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- TAB 2: PATIENTS LIST -->
        <div id="patients-tab" class="tab-content">
            <div style="margin-bottom: 1rem;">
                <input type="text" id="patientSearchInput" onkeyup="filterTable('patientSearchInput', 'patientsTable')" class="form-control" placeholder="Quick search patients..." style="max-width: 400px;">
            </div>
            <div class="table-responsive">
                <table class="data-table" id="patientsTable">
                    <thead>
                        <tr>
                            <th>Patient ID</th>
                            <th>Name</th>
                            <th>Age</th>
                            <th>Contact Phone</th>
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
                                    <td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No registered patients yet.</td>
                                </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- TAB 3: ALL APPOINTMENTS -->
        <div id="appointments-tab" class="tab-content">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Appt ID</th>
                            <th>Patient</th>
                            <th>Doctor</th>
                            <th>Specialization</th>
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
                                    <td><strong><%= a.getPatientName() %></strong> (<%= a.getPatientContact() %>)</td>
                                    <td><%= a.getDoctorName() %></td>
                                    <td><%= a.getDoctorSpecialization() %></td>
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
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No appointments scheduled yet.</td>
                                </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- TAB 4: MANAGE DOCTOR SLOTS -->
        <div id="availability-tab" class="tab-content">
            <div style="background: white; padding: 2rem; border-radius: 16px; border: 1px solid var(--border); max-width: 600px; margin-bottom: 2rem;">
                <h3 style="margin-bottom: 1rem;"><i class="fa-solid fa-plus-circle"></i> Add Availability Slot for Doctor</h3>
                <form action="${pageContext.request.contextPath}/admin/addSlot" method="post">
                    <div class="form-group">
                        <label class="form-label" for="slotDoctorId">Select Doctor</label>
                        <select name="doctorId" id="slotDoctorId" class="form-control" required>
                            <% if (doctors != null) {
                                for (Doctor d : doctors) {
                                    if ("ACTIVE".equalsIgnoreCase(d.getStatus())) { %>
                                        <option value="<%= d.getDoctorId() %>"><%= d.getName() %> (<%= d.getSpecialization() %>)</option>
                            <%      }
                                }
                               } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="slotDate">Date</label>
                        <input type="date" name="slotDate" id="slotDate" class="form-control" required>
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                        <div class="form-group">
                            <label class="form-label" for="startTime">Start Time</label>
                            <input type="time" name="startTime" id="startTime" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="endTime">End Time</label>
                            <input type="time" name="endTime" id="endTime" class="form-control" required>
                        </div>
                    </div>

                    <button type="submit" class="btn-primary" style="margin-top: 0.5rem;"><i class="fa-solid fa-calendar-plus"></i> Create Slot</button>
                </form>
            </div>
        </div>

    </main>

    <!-- Modal: Add New Doctor -->
    <div id="addDoctorModal" class="modal-backdrop">
        <div class="modal-box">
            <div class="modal-header">
                <h3><i class="fa-solid fa-user-doctor"></i> Register New Doctor</h3>
                <button class="modal-close" onclick="closeModal('addDoctorModal')">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/addDoctor" method="post">
                <div class="form-group">
                    <label class="form-label">Doctor Name</label>
                    <input type="text" name="name" class="form-control" placeholder="Dr. Jane Doe" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Specialization</label>
                    <input type="text" name="specialization" class="form-control" placeholder="e.g. Neurology, Cardiology" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Contact Phone</label>
                    <input type="text" name="contact" class="form-control" placeholder="+1 555-0188" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Email Address</label>
                    <input type="email" name="email" class="form-control" placeholder="jane@clinic.com" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Initial Password</label>
                    <input type="password" name="password" class="form-control" placeholder="••••••••" required>
                </div>
                <button type="submit" class="btn-primary" style="margin-top: 1rem;"><i class="fa-solid fa-check"></i> Add Doctor</button>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
