package com.techtrio.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.services.CustomerProfileService;
import com.techtrio.services.EventService;

public class CustomerController {

    private final CustomerProfileService customerService = new CustomerProfileService();
    private final EventService eventService = new EventService();

    // ==========================================
    // CUSTOMER STATE & ONBOARDING
    // ==========================================

    public CustomerState getCustomerState(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return new CustomerState(false, "NEW");

        List<Event> customerEvents = eventService.getEventsByCustomerId(uid);
        boolean hasEvents = customerEvents != null && !customerEvents.isEmpty();

        CustomerProfile profile = customerService.getCustomerProfile(uid);
        String status = (profile != null && profile.getOnboardingStatus() != null)
                ? profile.getOnboardingStatus()
                : (hasEvents ? "EXISTING" : "NEW");

        return new CustomerState(hasEvents, status);
    }

    public boolean markCustomerAsExisting(String customerId) {
        if (customerId == null || customerId.isBlank()) return false;

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileCompleted", true);
        updates.put("onboardingStatus", "EXISTING");

        return customerService.updateCustomerProfileFields(customerId, updates);
    }

    // ==========================================
    // CUSTOMER PROFILE OPERATIONS
    // ==========================================

    public boolean createCustomerProfile(CustomerProfile profile) {
        return customerService.saveCustomerProfile(profile);
    }

    public CustomerProfile getCustomerProfile(String customerId) {
        return customerService.getCustomerProfile(customerId);
    }

    public List<CustomerProfile> getAllCustomerProfiles() {
        return customerService.getAllCustomerProfiles();
    }

    public boolean updateCustomerProfileFields(String customerId, Map<String, Object> updates) {
        return customerService.updateCustomerProfileFields(customerId, updates);
    }

    // ==========================================
    // CUSTOMER EVENT OPERATIONS (Firestore "events")
    // ==========================================

    public boolean createEvent(Event event) {
        return eventService.createEvent(event);
    }

    public boolean saveEvent(Event event) {
        return eventService.createEvent(event);
    }

    public Event getEventById(String eventId) {
        return eventService.getEventById(eventId);
    }

    public List<Event> getEventsByCustomerId(String customerId) {
        return eventService.getEventsByCustomerId(customerId);
    }

    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    public boolean updateEventStatus(String eventId, String status) {
        return eventService.updateEventStatus(eventId, status);
    }
}