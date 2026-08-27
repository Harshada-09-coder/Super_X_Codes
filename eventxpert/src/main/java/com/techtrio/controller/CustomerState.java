package com.techtrio.controller;

public class CustomerState {

    private final boolean hasCreatedEvent;
    private final String onboardingStatus;

    public CustomerState(boolean hasCreatedEvent, String onboardingStatus) {
        this.hasCreatedEvent = hasCreatedEvent;
        this.onboardingStatus = onboardingStatus;
    }

    public boolean hasCreatedEvent() {
        return hasCreatedEvent;
    }

    public String getOnboardingStatus() {
        return onboardingStatus;
    }
}