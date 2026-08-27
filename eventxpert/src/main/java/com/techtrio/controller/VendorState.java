package com.techtrio.controller;

public class VendorState {

    private boolean profileCompleted;
    private boolean hasProvidedService;
    private String onboardingStatus;
    private String currentProcess;

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

    public boolean hasProvidedService() {
        return hasProvidedService;
    }

    public String getOnboardingStatus() {
        return onboardingStatus;
    }

    public String getCurrentProcess() {
        return currentProcess;
    }
}