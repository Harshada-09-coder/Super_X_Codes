package com.techtrio.controller;

import com.techtrio.model.VenueModel;
import com.techtrio.services.VenueService;

import java.util.List;

public class VenueController {

    private final VenueService venueService;

    public VenueController() {
        this.venueService = new VenueService();
    }

    public List<VenueModel> getNearbyVenues(String city, String area, String venueType, int minCapacity, double targetBudget) {
        return venueService.getNearbyVenues(city, area, venueType, minCapacity, targetBudget);
    }

    public boolean sendVenueSuggestions(String eventId, List<VenueModel> suggestedVenues) {
        return venueService.sendVenueSuggestionsToCustomer(eventId, suggestedVenues);
    }

    public boolean confirmCustomerVenueChoice(String eventId, VenueModel selectedVenue, String organizerName) {
        return venueService.confirmCustomerVenueChoice(eventId, selectedVenue, organizerName);
    }

    public boolean requestVenueChange(String eventId, String changeNotes) {
        return venueService.requestVenueChange(eventId, changeNotes);
    }
}