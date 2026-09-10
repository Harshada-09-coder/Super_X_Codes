package com.techtrio.services;

import java.util.List;
import com.techtrio.model.ConflictResult;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;
import com.techtrio.model.Conflict;

public class ConflictDetectionService {

    private final EventService eventService = new EventService();
    private final ConflictService conflictService = new ConflictService();
    private final VendorBookingService vendorBookingService = new VendorBookingService();

    /**
     * Conflict Detection is completely bypassed/disabled.
     * Always returns a clean ConflictResult with no conflicts,
     * ensuring no event or booking is ever blocked.
     */
    public ConflictResult checkAllConflicts(Event newEvent) {
        ConflictResult result = new ConflictResult();
        result.setConflict(false);
        result.setOrganizerConflict(false);
        result.setVendorConflict(false);
        result.setVenueConflict(false);
        result.setTimeConflict(false);
        return result;
    }

    // Helper stubs maintained to prevent any compilation or linkage issues
    private boolean sameVenue(Event event1, Event event2) {
        return false;
    }

    private boolean sameOrganizer(Event event1, Event event2) {
        return false;
    }

    private boolean isTimeOverlap(String newTime, String existingTime) {
        return false;
    }

    private int convertToMinutes(String time) {
        return -1;
    }

    private void saveDetectedConflict(Event newEvent, Event existingEvent, String type,
                                      String severity, String resource, String description) {
        // No-op: Do not save any conflicts to the database
    }

    private void addConflictingEvent(ConflictResult result, Event existingEvent) {
        // No-op
    }

    private void checkVendorConflicts(Event newEvent, ConflictResult result) {
        // No-op
    }

    private void saveVendorConflict(Event newEvent, VendorBooking existingBooking) {
        // No-op
    }
}