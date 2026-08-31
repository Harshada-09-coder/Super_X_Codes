package com.techtrio.model;

import java.util.ArrayList;
import java.util.List;

public class ConflictResult {

    private boolean conflict;

    private boolean organizerConflict;
    private boolean vendorConflict;
    private boolean venueConflict;
    private boolean timeConflict;

    private List<Event> conflictingEvents = new ArrayList<>();

    public ConflictResult() {
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }

    public boolean isOrganizerConflict() {
        return organizerConflict;
    }

    public void setOrganizerConflict(boolean organizerConflict) {
        this.organizerConflict = organizerConflict;
    }

    public boolean isVendorConflict() {
        return vendorConflict;
    }

    public void setVendorConflict(boolean vendorConflict) {
        this.vendorConflict = vendorConflict;
    }

    public boolean isVenueConflict() {
        return venueConflict;
    }

    public void setVenueConflict(boolean venueConflict) {
        this.venueConflict = venueConflict;
    }

    public boolean isTimeConflict() {
        return timeConflict;
    }

    public void setTimeConflict(boolean timeConflict) {
        this.timeConflict = timeConflict;
    }

    public List<Event> getConflictingEvents() {
        return conflictingEvents;
    }

    public void setConflictingEvents(
            List<Event> conflictingEvents) {

        this.conflictingEvents = conflictingEvents;
    }
}