package com.techtrio.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class OrganizerProfile {

    private String organizerId;
    private String name;
    private String email;
    private String mobileNo;
    private String organizationName;
    private String businessType;
    private String address;
    private String gstNumber;
    private String role;
    private boolean profileCompleted;
    private String onboardingStatus; // NEW / EXISTING
    private String createdAt;
    private String updatedAt;

    public OrganizerProfile() {
    }

    public OrganizerProfile(
            String organizerId,
            String name,
            String email,
            String mobileNo,
            String organizationName,
            String businessType,
            String address,
            String gstNumber
    ) {
        this.organizerId = organizerId;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.organizationName = organizationName;
        this.businessType = businessType;
        this.address = address;
        this.gstNumber = gstNumber;
        this.role = "organizer";
        this.profileCompleted = false;
        this.onboardingStatus = "NEW";
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public String getOnboardingStatus() {
        return onboardingStatus;
    }

    public void setOnboardingStatus(String onboardingStatus) {
        this.onboardingStatus = onboardingStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}