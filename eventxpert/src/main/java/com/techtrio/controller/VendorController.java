package com.techtrio.controller;

import java.util.List;
import java.util.Map;

import com.techtrio.model.VendorBooking;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorBookingService;
import com.techtrio.services.VendorProfileService;

public class VendorController {

    private final VendorProfileService vendorProfileService = new VendorProfileService();
    private final VendorBookingService vendorBookingService = new VendorBookingService();

    // =========================================================
    // VENDOR STATE METHOD FOR LOGIN PAGE (RESOLVES LOGIN ERROR)
    // =========================================================
    public VendorState getVendorState(String uid, String idToken) {
        if (uid == null || uid.isBlank()) {
            return new VendorState(false, false, "NEW", "NONE");
        }

        try {
            VendorProfile profile = vendorProfileService.getVendorProfile(uid);
            if (profile != null) {
                return new VendorState(
                        profile.isProfileCompleted(),
                        profile.isHasProvidedService(),
                        profile.getOnboardingStatus() != null ? profile.getOnboardingStatus() : "NEW",
                        "NONE"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new VendorState(false, false, "NEW", "NONE");
    }

    public VendorState getVendorState(String uid) {
        return getVendorState(uid, null);
    }

    // =========================================================
    // VENDOR BOOKINGS & FIRESTORE METHODS
    // =========================================================
    public boolean requestVendorBooking(VendorBooking booking) {
        return vendorBookingService.createVendorBooking(booking);
    }

    public VendorBooking getBookingById(String bookingId) {
        return vendorBookingService.getBookingById(bookingId);
    }

    public List<VendorBooking> getVendorBookings(String vendorId) {
        return vendorBookingService.getBookingsByVendorId(vendorId);
    }

    // ⭐ हि मेथड ॲड केली आहे ज्यामुळे EarningsPaymentsV मधील एरर निघून जाईल
    public List<VendorBooking> getVendorBookingsByVendorId(String vendorId) {
        return vendorBookingService.getBookingsByVendorId(vendorId);
    }

    public List<VendorBooking> getEventVendorBookings(String eventId) {
        return vendorBookingService.getBookingsByEventId(eventId);
    }

    public boolean acceptBookingRequest(String bookingId) {
        return vendorBookingService.updateBookingStatus(bookingId, "CONFIRMED");
    }

    public boolean rejectBookingRequest(String bookingId) {
        return vendorBookingService.updateBookingStatus(bookingId, "REJECTED");
    }

    public boolean markVendorAsActive(String uid) {
        if (uid == null || uid.isBlank()) return false;
        return vendorProfileService.markVendorAsActive(uid);
    }

    public boolean createVendorProfile(VendorProfile profile, String idToken) {
        if (profile == null) return false;
        return vendorProfileService.saveVendorProfile(profile);
    }

    public VendorProfile getVendorProfile(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return null;
        return vendorProfileService.getVendorProfile(uid);
    }

    public boolean updateVendorProfileFields(String uid, Map<String, Object> updates) {
        if (uid == null || uid.isBlank()) return false;
        return vendorProfileService.updateVendorProfileFields(uid, updates);
    }
}