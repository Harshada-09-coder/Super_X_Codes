package com.techtrio.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CustomerProfile {

    private String customerId;
    private String name;
    private String email;
    private String mobileNo;
    private String role;
    private boolean profileCompleted;
    private boolean hasCreatedEvent;
    private String onboardingStatus;
    private String createdAt;
    private String updatedAt;

    public CustomerProfile() {
    }

    public CustomerProfile(String customerId, String name, String email, String mobileNo, String role) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.role = role;
        this.profileCompleted = true;
        this.hasCreatedEvent = false;
        this.onboardingStatus = "NEW";
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public boolean isHasCreatedEvent() {
        return hasCreatedEvent;
    }

    public void setHasCreatedEvent(boolean hasCreatedEvent) {
        this.hasCreatedEvent = hasCreatedEvent;
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