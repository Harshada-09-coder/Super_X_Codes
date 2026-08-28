package com.techtrio.context;

public class AdminContext {

    private static String selectedCustomerId;
    private static String selectedEventId;

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
}