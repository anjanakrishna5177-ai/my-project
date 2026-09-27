# Clinic Appointment Management System

A Java Web Application built with **Java Servlets, JSP, JDBC, and MySQL** for managing healthcare clinic operations, doctor time slot availabilities, patient registrations, appointment bookings, rescheduling, cancellations, and 24-hour upcoming appointment reminders.

---

## 🚀 Key Features

### 🔐 1. Authentication & Security
- **Role-Based Access Control (RBAC):** Distinct dashboards for **ADMIN**, **DOCTOR**, and **PATIENT**.
- **Security:** Password hashing using SHA-256 (`PasswordUtil`), PreparedStatement SQL injection protection, and `AuthFilter` session verification.

### 👑 2. Admin Dashboard
- **Clinic Overview Stats:** View total registered doctors, patients, and scheduled appointments.
- **Doctor Management:** Add new doctors, edit doctor details, deactivate/activate doctor accounts.
- **Doctor Search:** Filter doctors by ID or specialization.
- **Patient Records:** View all patient profiles and contact details.
- **Schedule Management:** Add availability time slots for any active doctor.
- **24-Hour Reminders:** View all upcoming appointments scheduled within the next 24 hours.

### 🩺 3. Doctor Dashboard
- **Doctor Profile:** View doctor ID, name, specialization, contact, and email.
- **Appointment Management:** View upcoming and previous appointments.
- **Patient Directory:** View list of patients who have booked appointments with the doctor.
- **Time Slot Management:** Add custom availability slots (Date, Start Time, End Time) and remove unbooked slots.
- **Reminder Notices:** View 24-hour appointment alerts.

### 👤 4. Patient Dashboard & Booking Workflow
- **Patient Registration:** Self-service registration with validation and duplicate email check.
- **Doctor Search & Discovery:** Search active doctors by specialization or name.
- **Slot Selection & Booking:** View doctor availability and instantly book an available slot (transactions ensure no double-booking).
- **Rescheduling:** Select an existing appointment, pick a new available slot for the same doctor, auto-free the old slot, and update status to `RESCHEDULED`.
- **Cancellation:** Cancel appointments and instantly make the slot available again for other patients.
- **Upcoming Reminders:** View appointment alerts for consultations within the next 24 hours.

---

## 🛠️ Technology Stack

- **Backend:** Java 17 / Java EE (Java Servlets, JSP, JSTL, JDBC)
- **Database:** MySQL 8.x (`PreparedStatement` for all database interactions)
- **Frontend:** HTML5, Modern CSS3 (Vanilla CSS, Responsive Grid/Flexbox, Glassmorphism), Vanilla JavaScript
- **Web Server / Application Server:** Apache Tomcat 9.x / 10.x
- **Build Tool:** Apache Maven (`pom.xml`)

---

## 🗄️ Database Setup (MySQL)

1. Open your MySQL Client or Workbench.
2. Run the `database.sql` script included in the root folder:
   ```sql
   SOURCE path/to/clinic appointment sysytem/database.sql;
   ```
3. Default database name: `clinic_db`
4. Default credentials in `DBConnection.java`:
   - URL: `jdbc:mysql://localhost:3306/clinic_db`
   - User: `root`
   - Password: `root` (You can pass JVM options `-Ddb.user=your_user -Ddb.password=your_pass` if different).

---

## 🔑 Demo Test Accounts

| Role | Email Address | Password | Account Details |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@clinic.com` | `admin123` | System Administrator |
| **DOCTOR** | `john.smith@clinic.com` | `doc123` | Dr. John Smith (Cardiology) |
| **DOCTOR** | `sarah.connor@clinic.com` | `doc123` | Dr. Sarah Connor (Dermatology) |
| **PATIENT**| `alice.williams@gmail.com` | `patient123` | Alice Williams (Age 28) |
| **PATIENT**| `bob.miller@gmail.com` | `patient123` | Bob Miller (Age 34) |

---

## 🏃 How to Build and Run

### Option 1: Run with Apache Tomcat & Maven
1. Open terminal in the project root directory.
2. Package the WAR file using Maven:
   ```bash
   mvn clean package
   ```
3. Copy `target/clinic-appointment-system.war` to your Apache Tomcat `webapps/` folder.
4. Start Apache Tomcat and navigate to:
   ```
   http://localhost:8080/clinic-appointment-system/
   ```

### Option 2: Run directly in Eclipse / IntelliJ IDEA
1. Import project as **Existing Maven Project**.
2. Configure **Apache Tomcat Server** in your IDE.
3. Deploy project onto Tomcat and click **Run**.

---

## 🧪 Testing Checklist

- [x] **Patient Registration:** Register a new patient account, verify duplicate email prevention.
- [x] **Role Login:** Test login as Admin, Doctor, and Patient with SHA-256 password verification.
- [x] **Admin Actions:** Add a new doctor, deactivate a doctor, search doctors, add availability slots.
- [x] **Doctor Actions:** View doctor profile, view assigned patients, add/delete availability time slots.
- [x] **Patient Actions:** Search doctor by specialization, view slots, book appointment.
- [x] **Rescheduling:** Reschedule appointment to another slot, verify old slot freed.
- [x] **Cancellation:** Cancel appointment, verify slot freed.
- [x] **24-Hour Reminders:** Verify reminder banners on Admin, Doctor, and Patient dashboards.
- [x] **Role Authorization:** Verify non-admin users cannot access `/admin/*` pages.
