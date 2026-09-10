package com.techtrio.model;

import com.google.cloud.Timestamp;

public class AdminProfile {
    private boolean twoFactorEnabled;

    private String adminId;
    private String name;
    private String email;
    private String mobileNo;
    private String address;
    private String role; // Firestore मधील 'role' फील्डसाठी जोडले आहे
    private Timestamp createdAt;

    // Firestore साठी required
    public AdminProfile() {
    }

    public AdminProfile(
            String adminId,
            String name,
            String email,
            String mobileNo,
            String address,
            Timestamp createdAt) {

        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.address = address;
        this.createdAt = createdAt;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getmobileNo() {
        return mobileNo;
    }

    public void setmobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}