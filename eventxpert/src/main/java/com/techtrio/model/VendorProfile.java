package com.techtrio.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class VendorProfile {

    private String vendorId;
    private String fullName;
    private String email;
    private String businessName;
    private String phone;
    private String category;
    private String address;
    private String experience;
    private String gstNumber;
    private String availability;
    private String bio;
    private String createdAt;
    private String updatedAt;

    // Vendor Onboarding & State fields (Firestore)
    private boolean profileCompleted;
    private boolean hasProvidedService;
    private String onboardingStatus;
    private String currentProcess;

    // Additional user fields
    private String name;
    private String mobileNo;
    private String role;

    // Required by Firestore POJO deserialization
    public VendorProfile() {
    }

    public VendorProfile(
            String vendorId,
            String fullName,
            String email,
            String businessName,
            String phone,
            String category,
            String address,
            String experience,
            String gstNumber,
            String availability
    ) {
        this.vendorId = vendorId;
        this.fullName = fullName;
        this.email = email;
        this.businessName = businessName;
        this.phone = phone;
        this.category = category;
        this.address = address;
        this.experience = experience;
        this.gstNumber = gstNumber;
        this.availability = availability;
        this.profileCompleted = false;
        this.hasProvidedService = false;
        this.onboardingStatus = "NEW";
        this.currentProcess = "PROFILE";
    }

    // Getters and Setters
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public boolean isHasProvidedService() {
        return hasProvidedService;
    }

    public void setHasProvidedService(boolean hasProvidedService) {
        this.hasProvidedService = hasProvidedService;
    }

    public String getOnboardingStatus() {
        return onboardingStatus;
    }

    public void setOnboardingStatus(String onboardingStatus) {
        this.onboardingStatus = onboardingStatus;
    }

    public String getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(String currentProcess) {
        this.currentProcess = currentProcess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}