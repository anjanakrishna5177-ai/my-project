package com.clinic.model;

public class Doctor extends User {
    private int doctorId;
    private String name;
    private String specialization;
    private String qualifications;
    private String contact;
    private String status; // PENDING, ACTIVE, INACTIVE

    public Doctor() {
        super();
        setRole("DOCTOR");
    }

    public Doctor(int doctorId, int userId, String email, String password, String name, String specialization, String qualifications, String contact, String status) {
        super(userId, email, password, "DOCTOR", null);
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.qualifications = qualifications;
        this.contact = contact;
        this.status = status;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
