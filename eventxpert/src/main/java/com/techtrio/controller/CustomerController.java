package com.techtrio.controller;

import java.util.List;

import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.services.CustomerProfileService;
import com.techtrio.services.EventService;

public class CustomerController {

    private final CustomerProfileService customerProfileService = new CustomerProfileService();
    private final EventService eventService = new EventService();

    public CustomerState getCustomerState(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return null;

        // Check if customer profile has created event
        CustomerProfile profile = customerProfileService.getCustomerProfile(uid);
        if (profile != null && profile.isHasCreatedEvent()) {
            return new CustomerState(true, "EXISTING");
        }

        // Double-check if any event document exists in 'events' collection
        List<Event> events = eventService.getEventsByCustomerId(uid);
        if (!events.isEmpty()) {
            customerProfileService.markCustomerAsExisting(uid);
            return new CustomerState(true, "EXISTING");
        }

        return new CustomerState(false, "NEW");
    }

    public boolean createEvent(Event event) {
        return eventService.createEvent(event);
    }

    public boolean markCustomerAsExisting(String customerId) {
        return customerProfileService.markCustomerAsExisting(customerId);
    }

    public List<Event> getEventsByCustomerId(String customerId) {
        return eventService.getEventsByCustomerId(customerId);
    }
}