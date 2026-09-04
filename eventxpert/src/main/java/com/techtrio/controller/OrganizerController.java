package com.techtrio.controller;

import java.util.List;
import java.util.Map;

import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.services.EventService;
import com.techtrio.services.OrganizerProfileService;

public class OrganizerController {

    private final OrganizerProfileService service = new OrganizerProfileService();
    private final EventService eventService = new EventService();

    public OrganizerState getOrganizerState(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return null;

        OrganizerProfile profile = service.getOrganizerProfile(uid);
        if (profile != null && profile.isProfileCompleted() && "EXISTING".equalsIgnoreCase(profile.getOnboardingStatus())) {
            return new OrganizerState(true, "EXISTING");
        }

        return new OrganizerState(false, "NEW");
    }

    public OrganizerProfile getOrganizerProfile(String uid) {
        return service.getOrganizerProfile(uid);
    }

    public boolean createOrganizerProfile(OrganizerProfile profile) {
        return service.saveOrganizerProfile(profile);
    }

    public boolean updateOrganizerProfileFields(String uid, Map<String, Object> updates) {
        return service.updateOrganizerProfileFields(uid, updates);
    }

    public boolean markOrganizerAsExisting(String uid) {
        return service.markOrganizerAsExisting(uid);
    }

    public List<Event> getAssignedEvents(String organizerId) {
        return eventService.getEventsByOrganizerId(organizerId);
    }

    public boolean acceptEventAssignment(String eventId, String organizerId, String organizerName) {
        return eventService.acceptEventAssignment(eventId, organizerId, organizerName);
    }

    public boolean acceptEventAssignment(String eventId) {
        return eventService.acceptEventAssignment(eventId);
    }

    public boolean rejectEventAssignment(String eventId) {
        return eventService.rejectEventAssignment(eventId);
    }

    // --- ORGANIZER BUDGET FINALIZATION & VENDOR SYNC ---
    public boolean updateEventBudgetBreakdown(String eventId, double allocatedBudget, Map<String, Object> budgetMap) {
        return eventService.updateEventBudgetBreakdown(eventId, allocatedBudget, budgetMap);
    }
}