package com.techtrio.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class VendorBooking {

    private String bookingId;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String organizerId;
    private String organizerName;
    private String vendorId;
    private String vendorName;
    private String serviceCategory;
    private String amount;
    private int guests;
    private String location;
    private String status; // PENDING, CONFIRMED, REJECTED
    private String createdAt;
    private String updatedAt;

    // CUSTOMER DETAILS
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    public VendorBooking() {
    }

    public VendorBooking(String bookingId, String eventId, String eventName, String eventDate,
                         String organizerId, String organizerName, String vendorId, String vendorName,
                         String serviceCategory, String amount, int guests, String location) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.serviceCategory = serviceCategory;
        this.amount = amount;
        this.guests = guests;
        this.location = location;
        this.status = "PENDING";
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public int getGuests() { return guests; }
    public void setGuests(int guests) { this.guests = guests; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // CUSTOMER GETTERS & SETTERS
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
}