# DESIGN DOCUMENT - CLINIC APPOINTMENT MANAGEMENT SYSTEM

## 1. System Overview
The **Clinic Appointment Management System** is an enterprise-grade Java Web Application designed for managing healthcare clinic workflows. It facilitates communication between clinic administrators, medical professionals (doctors), and patients. The system provides real-time slot availability locking, appointment booking, rescheduling, cancellation, and automated 24-hour upcoming appointment notifications.

---

## 2. Software Architecture

The application adopts the classical **Model-View-Controller (MVC)** architectural pattern:

```
[ Browser / User ] 
       │
       ▼ (HTTP Request)
┌─────────────────────────────────────────────────────────┐
│                      CONTROLLER                         │
│  Servlets & Filters (com.clinic.servlet / filter)       │
│  - LoginServlet, RegisterServlet, AdminServlet, etc.    │
└────────────────────────────┬────────────────────────────┘
                             │ (Calls Data Operations)
                             ▼
┌─────────────────────────────────────────────────────────┐
│                        MODEL                            │
│  Java Beans & DAOs (com.clinic.model / dao)             │
│  - User, Doctor, Patient, Appointment, Availability     │
│  - UserDAO, DoctorDAO, PatientDAO, AppointmentDAO       │
└────────────────────────────┬────────────────────────────┘
                             │ (JDBC PreparedStatement)
                             ▼
┌─────────────────────────────────────────────────────────┐
│                       DATABASE                          │
│  MySQL Database (clinic_db)                             │
└─────────────────────────────────────────────────────────┘
```

---

## 3. Class Design & OOP Principles

### 3.1 Inheritance Structure
- `User`: Base class containing shared user properties (`userId`, `email`, `password`, `role`, `createdAt`).
- `Admin`: Extends `User` with administrative capabilities.
- `Doctor`: Extends `User` with medical specialization (`doctorId`, `name`, `specialization`, `contact`, `status`).
- `Patient`: Extends `User` with patient profile info (`patientId`, `name`, `age`, `contact`).

### 3.2 Key Model Classes
1. **`User.java`**
   - **Attributes:** `int userId`, `String email`, `String password`, `String role`, `Timestamp createdAt`.
   - **Methods:** Getters/Setters, Constructors.

2. **`Doctor.java`**
   - **Attributes:** `int doctorId`, `String name`, `String specialization`, `String contact`, `String status`.
   - **Relationships:** Inherits `User`, associated with multiple `DoctorAvailability` slots and `Appointment` records.

3. **`Patient.java`**
   - **Attributes:** `int patientId`, `String name`, `int age`, `String contact`.
   - **Relationships:** Inherits `User`, associated with multiple `Appointment` records.

4. **`DoctorAvailability.java`**
   - **Attributes:** `int slotId`, `int doctorId`, `Date slotDate`, `Time startTime`, `Time endTime`, `String status` (`AVAILABLE` / `BOOKED`).

5. **`Appointment.java`**
   - **Attributes:** `int appointmentId`, `int patientId`, `int doctorId`, `int slotId`, `Date appointmentDate`, `Time appointmentTime`, `String status` (`SCHEDULED`, `RESCHEDULED`, `CANCELLED`, `COMPLETED`), `boolean isReminder`.

---

## 4. Database Schema (ER Design)

### Tables & Relationships

1. **`users` Table**
   - `user_id` (PK, INT AUTO_INCREMENT)
   - `email` (VARCHAR 100, UNIQUE)
   - `password` (VARCHAR 255, SHA-256 Hashed)
   - `role` (ENUM: 'ADMIN', 'DOCTOR', 'PATIENT')

2. **`doctors` Table**
   - `doctor_id` (PK, INT AUTO_INCREMENT)
   - `user_id` (FK -> `users.user_id` ON DELETE CASCADE)
   - `name` (VARCHAR 100)
   - `specialization` (VARCHAR 100)
   - `contact` (VARCHAR 20)
   - `status` (ENUM: 'ACTIVE', 'INACTIVE')

3. **`patients` Table**
   - `patient_id` (PK, INT AUTO_INCREMENT)
   - `user_id` (FK -> `users.user_id` ON DELETE CASCADE)
   - `name` (VARCHAR 100)
   - `age` (INT)
   - `contact` (VARCHAR 20)

4. **`doctor_availability` Table**
   - `slot_id` (PK, INT AUTO_INCREMENT)
   - `doctor_id` (FK -> `doctors.doctor_id` ON DELETE CASCADE)
   - `slot_date` (DATE)
   - `start_time` (TIME)
   - `end_time` (TIME)
   - `status` (ENUM: 'AVAILABLE', 'BOOKED')

5. **`appointments` Table**
   - `appointment_id` (PK, INT AUTO_INCREMENT)
   - `patient_id` (FK -> `patients.patient_id` ON DELETE CASCADE)
   - `doctor_id` (FK -> `doctors.doctor_id` ON DELETE CASCADE)
   - `slot_id` (FK -> `doctor_availability.slot_id` UNIQUE)
   - `appointment_date` (DATE)
   - `appointment_time` (TIME)
   - `status` (ENUM: 'SCHEDULED', 'RESCHEDULED', 'CANCELLED', 'COMPLETED')

---

## 5. Security & Authorization

1. **Password Hashing:** Passwords are never stored in plain text; SHA-256 digests are computed via `PasswordUtil.hashPassword()`.
2. **SQL Injection Protection:** All database operations execute strictly via `PreparedStatement` parameters.
3. **Session Authentication & Role Guard:** `AuthFilter` intercepts requests to `/admin/*`, `/doctor/*`, and `/patient/*` paths to ensure non-authenticated or cross-role access is blocked.
4. **Data Isolation:** Doctors can only view appointments and patients assigned to their `doctorId`. Patients can only view their own appointments and profile data.
