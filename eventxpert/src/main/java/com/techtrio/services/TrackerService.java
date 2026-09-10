package com.techtrio.services;

import com.techtrio.controller.VendorController;
import com.techtrio.model.Event;
import com.techtrio.model.VendorBooking;

import java.util.*;

public class TrackerService {

    public static class EventMilestoneProgress {
        public int progressPercentage;
        public boolean isEventCreated;
        public boolean isOrganizerAssigned;
        public boolean isVenueConfirmed;
        public boolean areVendorsLocked;
        public boolean isPaymentCompleted;
        public boolean isPayNowUnlocked;
        public String statusLabel;

        public EventMilestoneProgress() {
            this.progressPercentage = 20;
            this.isEventCreated = true;
            this.isOrganizerAssigned = false;
            this.isVenueConfirmed = false;
            this.areVendorsLocked = false;
            this.isPaymentCompleted = false;
            this.isPayNowUnlocked = false;
            this.statusLabel = "Planning";
        }
    }

    /**
     * Calculates Real-Time Milestone Progress for any event
     */
    public EventMilestoneProgress calculateProgress(Event event) {
        EventMilestoneProgress p = new EventMilestoneProgress();
        if (event == null) return p;

        p.isEventCreated = true;
        int score = 20; // Base score for event creation

        // 1. Check Organizer Assignment
        String oStatus = event.getOrganizerStatus() != null ? event.getOrganizerStatus().toUpperCase() : "";
        if ("ACCEPTED".equals(oStatus) || (event.getOrganizerId() != null && !event.getOrganizerId().isBlank())) {
            p.isOrganizerAssigned = true;
            score += 20; // 40%
        }

        // 2. Check Venue Confirmation
        String vStatus = event.getVenueStatus() != null ? event.getVenueStatus().toUpperCase() : "";
        if ("CONFIRMED".equals(vStatus) && event.getConfirmedVenueName() != null && !event.getConfirmedVenueName().startsWith("Pending")) {
            p.isVenueConfirmed = true;
            score += 20; // 60%
        }

        // 3. Check Vendor Bookings Lock
        List<String> requiredServices = getRequiredServiceList(event);
        boolean allVendorsAccepted = false;

        if (!requiredServices.isEmpty() && event.getEventId() != null) {
            try {
                VendorController vendorController = new VendorController();
                List<VendorBooking> bookings = vendorController.getEventVendorBookings(event.getEventId());

                int confirmedCount = 0;
                if (bookings != null) {
                    for (VendorBooking vb : bookings) {
                        String bSt = vb.getStatus() != null ? vb.getStatus().toUpperCase() : "";
                        if ("CONFIRMED".equals(bSt) || "ACCEPTED".equals(bSt) || "PAID".equals(bSt)) {
                            confirmedCount++;
                        }
                    }
                }
                // जर कमीतकमी १ किंवा सर्व्हिसेसचे व्हेंडर्स लॉक झाले असतील
                if (confirmedCount > 0 && confirmedCount >= requiredServices.size()) {
                    allVendorsAccepted = true;
                }
            } catch (Exception ignored) {}
        }

        if (allVendorsAccepted) {
            p.areVendorsLocked = true;
            score += 20; // 80%
        }

        // 4. Check Payment Status
        String pStatus = event.getStatus() != null ? event.getStatus().toUpperCase() : "";
        if ("CONFIRMED".equals(pStatus) || "PAID".equals(pStatus) || "APPROVED".equals(pStatus)) {
            p.isPaymentCompleted = true;
            score = 100; // 100%
        }

        p.progressPercentage = Math.min(100, score);

        // 5. Pay Now Unlock Condition: Venue must be confirmed AND Vendors locked (or Organizer approved)
        p.isPayNowUnlocked = (p.isVenueConfirmed && p.areVendorsLocked) || "APPROVED".equalsIgnoreCase(event.getStatus()) || "CONFIRMED".equalsIgnoreCase(event.getStatus());

        p.statusLabel = p.progressPercentage == 100 ? "Ready / Confirmed" : (p.progressPercentage >= 60 ? "In Execution" : "Planning");

        return p;
    }

    public List<String> getRequiredServiceList(Event event) {
        List<String> list = new ArrayList<>();
        if (event != null && event.getRequirements() != null && !event.getRequirements().isBlank()) {
            for (String s : event.getRequirements().split(",\\s*")) {
                if (!s.trim().isEmpty()) list.add(s.trim());
            }
        }
        return list;
    }
}