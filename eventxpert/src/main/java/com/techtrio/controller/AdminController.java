package com.techtrio.controller;

import java.util.List;

import com.techtrio.model.AdminProfile;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.services.AdminProfileService;
import com.techtrio.services.CustomerProfileService;
import com.techtrio.services.EventService;

public class AdminController {

    private final AdminProfileService service = new AdminProfileService();
    private final CustomerProfileService customerService = new CustomerProfileService();
    private final EventService eventService = new EventService();

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
    // ADMIN CUSTOMER & EVENT MANAGEMENT
    // ==========================================

    public List<CustomerProfile> getAllCustomerProfiles() {
        return customerService.getAllCustomerProfiles();
    }

    public CustomerProfile getCustomerProfile(String customerId) {
        return customerService.getCustomerProfile(customerId);
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

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName) {
        return eventService.assignOrganizerToEvent(eventId, organizerId, organizerName);
    }

    public boolean updateEventStatus(String eventId, String status) {
        return eventService.updateEventStatus(eventId, status);
    }
}