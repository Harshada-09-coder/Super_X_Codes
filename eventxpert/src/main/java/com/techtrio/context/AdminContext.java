package com.techtrio.context;

public class AdminContext {

    private static String selectedCustomerId;
    private static String selectedEventId;
    private static String selectedOrganizerId;
    private static String selectedVendorId;

    public static synchronized void setSelectedCustomerId(String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            selectedCustomerId = customerId.trim();
        }
    }

    public static synchronized String getSelectedCustomerId() {
        return selectedCustomerId;
    }

    public static synchronized void setSelectedEventId(String eventId) {
        if (eventId != null && !eventId.isBlank()) {
            selectedEventId = eventId.trim();
        }
    }

    public static synchronized String getSelectedEventId() {
        return selectedEventId;
    }

    public static synchronized void setSelectedOrganizerId(String organizerId) {
        if (organizerId != null && !organizerId.isBlank()) {
            selectedOrganizerId = organizerId.trim();
        }
    }

    public static synchronized String getSelectedOrganizerId() {
        return selectedOrganizerId;
    }

    public static synchronized void setSelectedVendorId(String vendorId) {
        if (vendorId != null && !vendorId.isBlank()) {
            selectedVendorId = vendorId.trim();
        }
    }

    public static synchronized String getSelectedVendorId() {
        return selectedVendorId;
    }
}