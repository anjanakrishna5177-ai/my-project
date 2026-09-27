-- =========================================================
-- CLINIC APPOINTMENT MANAGEMENT SYSTEM DATABASE SCHEMA
-- =========================================================

CREATE DATABASE IF NOT EXISTS `clinic_db`;
USE `clinic_db`;

-- Drop existing tables if re-initialising
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `doctor_availability`;
DROP TABLE IF EXISTS `patients`;
DROP TABLE IF EXISTS `doctors`;
DROP TABLE IF EXISTS `users`;

-- 1. USERS TABLE
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. DOCTORS TABLE
CREATE TABLE `doctors` (
    `doctor_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `specialization` VARCHAR(100) NOT NULL,
    `qualifications` VARCHAR(255) DEFAULT 'MBBS',
    `contact` VARCHAR(20) NOT NULL,
    `status` ENUM('PENDING', 'ACTIVE', 'INACTIVE') DEFAULT 'PENDING',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. PATIENTS TABLE
CREATE TABLE `patients` (
    `patient_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `age` INT NOT NULL,
    `contact` VARCHAR(20) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. DOCTOR AVAILABILITY SLOTS TABLE
CREATE TABLE `doctor_availability` (
    `slot_id` INT AUTO_INCREMENT PRIMARY KEY,
    `doctor_id` INT NOT NULL,
    `slot_date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `status` ENUM('AVAILABLE', 'BOOKED') DEFAULT 'AVAILABLE',
    FOREIGN KEY (`doctor_id`) REFERENCES `doctors`(`doctor_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. APPOINTMENTS TABLE
CREATE TABLE `appointments` (
    `appointment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `slot_id` INT NOT NULL UNIQUE,
    `appointment_date` DATE NOT NULL,
    `appointment_time` TIME NOT NULL,
    `status` ENUM('SCHEDULED', 'RESCHEDULED', 'CANCELLED', 'COMPLETED') DEFAULT 'SCHEDULED',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`patient_id`) REFERENCES `patients`(`patient_id`) ON DELETE CASCADE,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctors`(`doctor_id`) ON DELETE CASCADE,
    FOREIGN KEY (`slot_id`) REFERENCES `doctor_availability`(`slot_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- SAMPLE DATA INSERTS
-- Default Password for all demo accounts:
-- admin@clinic.com   -> admin123
-- john@clinic.com    -> doc123
-- sarah@clinic.com   -> doc123
-- alice@gmail.com    -> patient123
-- bob@gmail.com      -> patient123
-- SHA-256 HASHES:
-- admin123  : 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- doc123    : 84f5091a1a9e69c6e5e89d129fa4e2a7b8e5c0e1a1efcfb0bfb4344d471ef280
-- patient123: 214a132470776b6e492f254b0fa04021237a34ae5e7144e58b90c1f5d688cf81
-- =========================================================

-- Insert Users
INSERT INTO `users` (`user_id`, `email`, `password`, `role`) VALUES
(1, 'admin@clinic.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
(2, 'john.smith@clinic.com', '84f5091a1a9e69c6e5e89d129fa4e2a7b8e5c0e1a1efcfb0bfb4344d471ef280', 'DOCTOR'),
(3, 'sarah.connor@clinic.com', '84f5091a1a9e69c6e5e89d129fa4e2a7b8e5c0e1a1efcfb0bfb4344d471ef280', 'DOCTOR'),
(4, 'alice.williams@gmail.com', '214a132470776b6e492f254b0fa04021237a34ae5e7144e58b90c1f5d688cf81', 'PATIENT'),
(5, 'bob.miller@gmail.com', '214a132470776b6e492f254b0fa04021237a34ae5e7144e58b90c1f5d688cf81', 'PATIENT');

-- Insert Doctors
INSERT INTO `doctors` (`doctor_id`, `user_id`, `name`, `specialization`, `qualifications`, `contact`, `status`) VALUES
(1, 2, 'Dr. John Smith', 'Cardiology', 'MBBS, MD (Cardiology)', '+1 555-0192', 'ACTIVE'),
(2, 3, 'Dr. Sarah Connor', 'Dermatology', 'MBBS, DVL', '+1 555-0198', 'ACTIVE');

-- Insert Patients
INSERT INTO `patients` (`patient_id`, `user_id`, `name`, `age`, `contact`) VALUES
(1, 4, 'Alice Williams', 28, '+1 555-0321'),
(2, 5, 'Bob Miller', 34, '+1 555-0432');

-- Insert Availability Slots (Including today/tomorrow dates for reminders testing)
INSERT INTO `doctor_availability` (`slot_id`, `doctor_id`, `slot_date`, `start_time`, `end_time`, `status`) VALUES
(1, 1, CURDATE() + INTERVAL 1 DAY, '09:00:00', '09:30:00', 'BOOKED'),
(2, 1, CURDATE() + INTERVAL 1 DAY, '10:00:00', '10:30:00', 'AVAILABLE'),
(3, 1, CURDATE() + INTERVAL 2 DAY, '14:00:00', '14:30:00', 'AVAILABLE'),
(4, 2, CURDATE() + INTERVAL 1 DAY, '11:00:00', '11:30:00', 'BOOKED'),
(5, 2, CURDATE() + INTERVAL 3 DAY, '15:00:00', '15:30:00', 'AVAILABLE');

-- Insert Sample Appointments
INSERT INTO `appointments` (`appointment_id`, `patient_id`, `doctor_id`, `slot_id`, `appointment_date`, `appointment_time`, `status`) VALUES
(1, 1, 1, 1, CURDATE() + INTERVAL 1 DAY, '09:00:00', 'SCHEDULED'),
(2, 2, 2, 4, CURDATE() + INTERVAL 1 DAY, '11:00:00', 'SCHEDULED');
