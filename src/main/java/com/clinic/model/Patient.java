package com.clinic.model;

public class Patient extends User {
    private int patientId;
    private String name;
    private int age;
    private String contact;

    public Patient() {
        super();
        setRole("PATIENT");
    }

    public Patient(int patientId, int userId, String email, String password, String name, int age, String contact) {
        super(userId, email, password, "PATIENT", null);
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.contact = contact;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
