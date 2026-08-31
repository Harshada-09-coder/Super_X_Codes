package com.techtrio.controller;

public class OrganizerState {

    private final boolean profileCompleted;
    private final String onboardingStatus;

    public OrganizerState(boolean profileCompleted, String onboardingStatus) {
        this.profileCompleted = profileCompleted;
        this.onboardingStatus = onboardingStatus;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public String getOnboardingStatus() {
        return onboardingStatus;
    }
}