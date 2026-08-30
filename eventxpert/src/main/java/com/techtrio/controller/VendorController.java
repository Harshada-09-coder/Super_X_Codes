package com.techtrio.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.techtrio.model.VendorBooking;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorBookingService;
import com.techtrio.services.VendorProfileService;

public class VendorController {

    private final String PROJECT_ID = "eventxpert-6f453";
    public static final String COLLECTION_NAME = "Vendors";
    private final HttpClient client = HttpClient.newHttpClient();
    private final VendorProfileService vendorProfileService = new VendorProfileService();
    private final VendorBookingService vendorBookingService = new VendorBookingService();

    // =========================================================
    // VENDOR BOOKING & ORGANIZER CONNECTION METHODS
    // =========================================================
    public boolean requestVendorBooking(VendorBooking booking) {
        return vendorBookingService.createVendorBooking(booking);
    }

    public List<VendorBooking> getVendorBookings(String vendorId) {
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

    // =========================================================
    // GET VENDOR STATE FROM FIRESTORE
    // =========================================================
    public VendorState getVendorState(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return null;

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

    public boolean markVendorAsActive(String uid) {
        if (uid == null || uid.isBlank()) return false;
        return vendorProfileService.markVendorAsActive(uid);
    }

    public boolean markServiceProvided(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return false;
        return vendorProfileService.markVendorAsActive(uid);
    }

    public boolean completeVendorProfile(String uid, String idToken) {
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