package com.techtrio.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.Guest;
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
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("events").document(event.getEventId());

            Map<String, Object> data = new HashMap<>();
            data.put("eventId", event.getEventId());
            data.put("customerId", event.getCustomerId());
            data.put("eventName", event.getEventName());
            data.put("eventType", event.getEventType());
            data.put("category", event.getCategory());
            data.put("guests", event.getGuests());
            data.put("eventDate", event.getEventDate());
            data.put("time", event.getTime());
            data.put("requirements", event.getRequirements());
            data.put("notes", event.getNotes());
            data.put("city", event.getCity());
            data.put("locality", event.getLocality());
            data.put("venueType", event.getVenueType());
            data.put("budget", event.getBudget());
            data.put("status", event.getStatus());
            data.put("organizerStatus", event.getOrganizerStatus());
            data.put("aiPlanJson", event.getAiPlanJson());
            data.put("theme", event.getTheme());

            // GUEST MANAGEMENT FIRESTORE MAPPING
            data.put("guestMode", event.getGuestMode());
            data.put("guestLimit", event.getGuestLimit());
            data.put("checkedInCount", 0);
            data.put("qrToken", event.getQrToken());

            List<Map<String, Object>> guestDataList = new ArrayList<>();
            if (event.getGuestList() != null) {
                for (Guest g : event.getGuestList()) {
                    Map<String, Object> gMap = new HashMap<>();
                    gMap.put("id", g.getId());
                    gMap.put("name", g.getName());
                    gMap.put("mobile", g.getMobile());
                    gMap.put("email", g.getEmail());
                    gMap.put("type", g.getType());
                    gMap.put("checkedIn", g.isCheckedIn());
                    gMap.put("checkInTime", g.getCheckInTime());
                    guestDataList.add(gMap);
                }
            }
            data.put("guestList", guestDataList);

            docRef.set(data).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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