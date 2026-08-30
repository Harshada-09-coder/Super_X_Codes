package com.techtrio.controller;

import java.util.List;

import com.techtrio.model.AdminProfile;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.model.VendorProfile;
import com.techtrio.services.AdminProfileService;
import com.techtrio.services.CustomerProfileService;
import com.techtrio.services.EventService;
import com.techtrio.services.OrganizerProfileService;
import com.techtrio.services.VendorProfileService;

public class AdminController {

    private final AdminProfileService service = new AdminProfileService();
    private final CustomerProfileService customerService = new CustomerProfileService();
    private final EventService eventService = new EventService();
    private final OrganizerProfileService organizerService = new OrganizerProfileService();
    private final VendorProfileService vendorService = new VendorProfileService();

    // CREATE
    public boolean createAdminProfile(AdminProfile admin) {
        return service.createAdminProfile(admin);
    }

    // READ
    public AdminProfile getAdminProfile(String adminId) {
        return service.getAdminProfile(adminId);
    }

    // UPDATE
    public boolean updateAdminProfile(
            String adminId,
            String name,
            String email,
            String mobileNo,
            String address) {

        return service.updateAdminProfile(
                adminId,
                name,
                email,
                mobileNo,
                address
        );
    }

    // DELETE
    public boolean deleteAdminProfile(String adminId) {
        return service.deleteAdminProfile(adminId);
    }

    public boolean updateTwoFactorStatus(
            String adminId,
            boolean enabled) {

        return service.updateTwoFactorStatus(
                adminId,
                enabled
        );
    }

    // ==========================================
    // ADMIN CUSTOMER, ORGANIZER, VENDOR & EVENT MANAGEMENT
    // ==========================================

    public List<CustomerProfile> getAllCustomerProfiles() {
        return customerService.getAllCustomerProfiles();
    }

    public CustomerProfile getCustomerProfile(String customerId) {
        return customerService.getCustomerProfile(customerId);
    }

    public List<OrganizerProfile> getAllOrganizers() {
        return organizerService.getAllOrganizers();
    }

    public OrganizerProfile getOrganizerProfile(String organizerId) {
        return organizerService.getOrganizerProfile(organizerId);
    }

    public List<VendorProfile> getAllVendors() {
        return vendorService.getAllVendors();
    }

    public VendorProfile getVendorProfile(String vendorId) {
        return vendorService.getVendorProfile(vendorId);
    }

    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    public Event getEventById(String eventId) {
        return eventService.getEventById(eventId);
    }

    public List<Event> getEventsByCustomerId(String customerId) {
        return eventService.getEventsByCustomerId(customerId);
    }

    public List<OrganizerProfile> getAllActiveOrganizers() {
        return eventService.getAllActiveOrganizers();
    }

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName) {
        return eventService.assignOrganizerToEvent(eventId, organizerId, organizerName);
    }

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName, String adminUid, String reviewNotes) {
        return eventService.assignOrganizerToEvent(eventId, organizerId, organizerName, adminUid, reviewNotes);
    }

    public boolean updateEventStatus(String eventId, String status) {
        return eventService.updateEventStatus(eventId, status);
    }
}