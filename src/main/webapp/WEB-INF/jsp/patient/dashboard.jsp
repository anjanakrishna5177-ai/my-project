<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.clinic.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Dashboard - ClinicCare</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <% Patient patient = (Patient) session.getAttribute("patient"); %>

    <!-- Header Navbar -->
    <header class="app-navbar">
        <a href="${pageContext.request.contextPath}/patient/dashboard" class="brand-logo">
            <i class="fa-solid fa-heart-pulse"></i> Patient Portal
        </a>
        <div class="nav-user">
            <span class="user-badge"><i class="fa-solid fa-user-heart"></i> <%= (patient != null) ? patient.getName() : "Patient" %></span>
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
            <h1>Welcome, <%= (patient != null) ? patient.getName() : "Patient" %></h1>
            <p>Book new clinic consultations, manage your upcoming schedule & track reminders.</p>
        </div>

        <!-- 24-Hour Reminders -->
        <% List<Appointment> reminders = (List<Appointment>) request.getAttribute("reminders");
           if (reminders != null && !reminders.isEmpty()) { %>
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.1rem; margin-bottom: 0.75rem; color: #b45309;">
                    <i class="fa-solid fa-bell"></i> 24-Hour Appointment Reminders (<%= reminders.size() %>)
                </h3>
                <% for (Appointment r : reminders) { %>
                    <div class="reminder-card">
                        <div class="reminder-icon"><i class="fa-solid fa-clock"></i></div>
                        <div>
                            <strong>Reminder:</strong> You have an upcoming appointment with <strong><%= r.getDoctorName() %></strong> (<%= r.getDoctorSpecialization() %>) tomorrow on <strong><%= r.getAppointmentDate() %></strong> at <strong><%= r.getAppointmentTime() %></strong>.
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

        <!-- Tab Controls -->
        <div class="tabs-nav">
            <button class="tab-btn active" data-tab="book-tab"><i class="fa-solid fa-calendar-plus"></i> Search & Book Appointment</button>
            <button class="tab-btn" data-tab="my-appointments-tab"><i class="fa-solid fa-calendar-check"></i> My Appointments</button>
            <button class="tab-btn" data-tab="profile-tab"><i class="fa-solid fa-user"></i> My Profile</button>
        </div>

        <!-- TAB 1: SEARCH & BOOK APPOINTMENT -->
        <div id="book-tab" class="tab-content active">
            <!-- Search Bar -->
            <form action="${pageContext.request.contextPath}/patient/dashboard" method="get" style="display: flex; gap: 0.5rem; margin-bottom: 2rem; max-width: 550px;">
                <input type="text" name="search" class="form-control" placeholder="Search doctor by Name, ID or Specialization (e.g. Cardiology)..." value="${searchQuery != null ? searchQuery : ''}">
                <button type="submit" class="btn-primary" style="width: auto; padding: 0.75rem 1.5rem;"><i class="fa-solid fa-magnifying-glass"></i> Search</button>
            </form>

            <h3 style="margin-bottom: 1rem;"><i class="fa-solid fa-user-doctor"></i> Available Doctors</h3>
            <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.5rem; margin-bottom: 3rem;">
                <% List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
                   Integer selectedDocId = (Integer) request.getAttribute("selectedDoctorId");
                   if (doctors != null && !doctors.isEmpty()) {
                       for (Doctor d : doctors) { %>
                        <div style="background: white; border-radius: 16px; border: 1.5px solid <%= (selectedDocId != null && selectedDocId == d.getDoctorId()) ? "var(--primary)" : "var(--border)" %>; padding: 1.5rem; box-shadow: var(--shadow-sm);">
                            <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                                <div class="stat-icon icon-teal" style="width: 48px; height: 48px;"><i class="fa-solid fa-user-md"></i></div>
                                <div>
                                    <h4 style="font-size: 1.1rem; color: var(--secondary);"><%= d.getName() %></h4>
                                    <span class="badge badge-scheduled"><%= d.getSpecialization() %></span>
                                </div>
                            </div>
                            <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 1rem;">
                                <i class="fa-solid fa-id-card"></i> ID: #DOC-<%= d.getDoctorId() %><br>
                                <i class="fa-solid fa-phone"></i> Contact: <%= d.getContact() %>
                            </p>
                            <a href="${pageContext.request.contextPath}/patient/dashboard?selectedDoctorId=<%= d.getDoctorId() %>#available-slots-section" class="btn-secondary" style="width: 100%; text-align: center; background: var(--primary-light); color: var(--primary);">
                                <i class="fa-solid fa-clock"></i> View Available Slots
                            </a>
                        </div>
                <%     }
                   } else { %>
                        <p style="grid-column: 1 / -1; color: var(--text-muted);">No active doctors found matching your query.</p>
                <% } %>
            </div>

            <!-- Available Slots Display Section -->
            <div id="available-slots-section" style="background: white; border-radius: 16px; border: 1px solid var(--border); padding: 2rem; margin-top: 2rem;">
                <h3><i class="fa-solid fa-calendar-day"></i> Available Time Slots for Booking</h3>
                <% List<DoctorAvailability> availableSlots = (List<DoctorAvailability>) request.getAttribute("availableSlots");
                   if (availableSlots != null && !availableSlots.isEmpty()) { %>
                    <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">Select a slot below to confirm your appointment booking:</p>
                    <div class="slots-grid">
                        <% for (DoctorAvailability slot : availableSlots) { %>
                            <div class="slot-card available">
                                <div class="slot-time"><%= slot.getStartTime() %> - <%= slot.getEndTime() %></div>
                                <div class="slot-date"><i class="fa-regular fa-calendar"></i> <%= slot.getSlotDate() %></div>
                                <form action="${pageContext.request.contextPath}/patient/bookAppointment" method="post" style="margin-top: 1rem;">
                                    <input type="hidden" name="doctorId" value="<%= slot.getDoctorId() %>">
                                    <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                    <button type="submit" class="btn-primary" style="padding: 0.45rem; font-size: 0.85rem;">
                                        <i class="fa-solid fa-check-circle"></i> Book Slot
                                    </button>
                                </form>
                            </div>
                        <% } %>
                    </div>
                <% } else if (selectedDocId != null) { %>
                    <p style="color: var(--text-muted); margin-top: 1rem;">No available slots currently posted for this doctor. Please check back later or search another doctor.</p>
                <% } else { %>
                    <p style="color: var(--text-muted); margin-top: 1rem;">Click "View Available Slots" on any doctor card above to view available appointment times.</p>
                <% } %>
            </div>
        </div>

        <!-- TAB 2: MY APPOINTMENTS -->
        <div id="my-appointments-tab" class="tab-content">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Appt ID</th>
                            <th>Doctor Name</th>
                            <th>Specialization</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
                           if (appointments != null && !appointments.isEmpty()) {
                               for (Appointment a : appointments) { %>
                                <tr>
                                    <td>#APT-<%= a.getAppointmentId() %></td>
                                    <td><strong><%= a.getDoctorName() %></strong></td>
                                    <td><%= a.getDoctorSpecialization() %></td>
                                    <td><%= a.getAppointmentDate() %></td>
                                    <td><%= a.getAppointmentTime() %></td>
                                    <td>
                                        <span class="badge badge-<%= a.getStatus().toLowerCase() %>">
                                            <%= a.getStatus() %>
                                        </span>
                                    </td>
                                    <td>
                                        <% if ("SCHEDULED".equals(a.getStatus()) || "RESCHEDULED".equals(a.getStatus())) { %>
                                            <div style="display: flex; gap: 0.5rem;">
                                                <button class="btn-secondary" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;" onclick="openRescheduleModal(<%= a.getAppointmentId() %>, <%= a.getDoctorId() %>, '<%= a.getDoctorName() %>')">
                                                    <i class="fa-solid fa-arrows-rotate"></i> Reschedule
                                                </button>
                                                <a href="${pageContext.request.contextPath}/patient/cancelAppointment?appointmentId=<%= a.getAppointmentId() %>" class="btn-danger-sm" onclick="return confirm('Are you sure you want to cancel this appointment?')">
                                                    <i class="fa-solid fa-xmark"></i> Cancel
                                                </a>
                                            </div>
                                        <% } else { %>
                                            <span style="color: var(--text-muted); font-size: 0.85rem;">None</span>
                                        <% } %>
                                    </td>
                                </tr>
                        <%     }
                           } else { %>
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">You have not booked any appointments yet.</td>
                                </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- TAB 3: PROFILE -->
        <div id="profile-tab" class="tab-content">
            <div style="background: white; border-radius: 16px; border: 1px solid var(--border); padding: 2rem; max-width: 500px;">
                <h3 style="margin-bottom: 1.5rem;"><i class="fa-solid fa-address-card"></i> Patient Profile</h3>
                <div class="form-group">
                    <label class="form-label">Patient ID</label>
                    <input type="text" class="form-control" value="#PAT-<%= (patient != null) ? patient.getPatientId() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Full Name</label>
                    <input type="text" class="form-control" value="<%= (patient != null) ? patient.getName() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Age</label>
                    <input type="text" class="form-control" value="<%= (patient != null) ? patient.getAge() : "" %> yrs" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Contact Phone</label>
                    <input type="text" class="form-control" value="<%= (patient != null) ? patient.getContact() : "" %>" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">Email Address</label>
                    <input type="text" class="form-control" value="<%= (patient != null) ? patient.getEmail() : "" %>" readonly>
                </div>
            </div>
        </div>

    </main>

    <!-- Modal: Reschedule Appointment -->
    <div id="rescheduleModal" class="modal-backdrop">
        <div class="modal-box">
            <div class="modal-header">
                <h3><i class="fa-solid fa-arrows-rotate"></i> Reschedule Appointment</h3>
                <button class="modal-close" onclick="closeModal('rescheduleModal')">&times;</button>
            </div>
            <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1rem;">Select a new available slot for <strong id="rescheduleDoctorName"></strong>:</p>
            <form action="${pageContext.request.contextPath}/patient/rescheduleAppointment" method="post">
                <input type="hidden" name="appointmentId" id="rescheduleApptId">

                <div class="form-group">
                    <label class="form-label">Select New Available Slot</label>
                    <select name="newSlotId" id="rescheduleSlotSelect" class="form-control" required>
                        <option value="">-- Choose New Slot --</option>
                    </select>
                </div>

                <button type="submit" class="btn-primary" style="margin-top: 1rem;"><i class="fa-solid fa-check"></i> Confirm Reschedule</button>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function openRescheduleModal(apptId, docId, docName) {
            document.getElementById('rescheduleApptId').value = apptId;
            document.getElementById('rescheduleDoctorName').textContent = docName;

            // Fetch available slots for this doctor dynamically via link redirection or page params
            window.location.href = '${pageContext.request.contextPath}/patient/dashboard?selectedDoctorId=' + docId + '&rescheduleApptId=' + apptId + '#available-slots-section';
        }

        <%
            String rescheduleApptId = request.getParameter("rescheduleApptId");
            if (rescheduleApptId != null && availableSlots != null && !availableSlots.isEmpty()) {
        %>
            document.addEventListener('DOMContentLoaded', () => {
                document.getElementById('rescheduleApptId').value = '<%= rescheduleApptId %>';
                const select = document.getElementById('rescheduleSlotSelect');
                select.innerHTML = '';
                <% for (DoctorAvailability s : availableSlots) { %>
                    const opt = document.createElement('option');
                    opt.value = '<%= s.getSlotId() %>';
                    opt.textContent = '<%= s.getSlotDate() %> | <%= s.getStartTime() %> - <%= s.getEndTime() %>';
                    select.appendChild(opt);
                <% } %>
                openModal('rescheduleModal');
            });
        <% } %>
    </script>
</body>
</html>
