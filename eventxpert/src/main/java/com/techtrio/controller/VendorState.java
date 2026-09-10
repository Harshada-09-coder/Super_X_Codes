package com.techtrio.controller;

public class VendorState {

    private boolean profileCompleted;
    private boolean hasProvidedService;
    private String onboardingStatus;
    private String currentProcess;

    public VendorState() {
    }

    public VendorState(
            boolean profileCompleted,
            boolean hasProvidedService,
            String onboardingStatus,
            String currentProcess) {

        this.profileCompleted = profileCompleted;
        this.hasProvidedService = hasProvidedService;
        this.onboardingStatus = onboardingStatus;
        this.currentProcess = currentProcess;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public boolean hasProvidedService() {
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
}