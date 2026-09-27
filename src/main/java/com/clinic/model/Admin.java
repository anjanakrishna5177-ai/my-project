package com.clinic.model;

import java.sql.Timestamp;

public class Admin extends User {
    private String adminName;

    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int userId, String email, String password, Timestamp createdAt, String adminName) {
        super(userId, email, password, "ADMIN", createdAt);
        this.adminName = adminName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
