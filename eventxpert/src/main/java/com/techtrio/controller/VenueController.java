package com.techtrio.controller;

import com.techtrio.model.VenueModel;
import com.techtrio.services.VenueService;

import java.util.List;

public class VenueController {

    private final VenueService venueService;

    public VenueController() {
        this.venueService = new VenueService();
    }

    public List<VenueModel> getNearbyVenues(String city, String venueType, int minCapacity) {
        return venueService.getNearbyVenues(city, venueType, minCapacity);
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