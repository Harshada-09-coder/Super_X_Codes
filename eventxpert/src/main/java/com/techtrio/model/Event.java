package com.techtrio.model;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {

    private String eventId;
    private String customerId;
    private String eventName;
    private String eventType;
    private String category;
    private int guests; // Guest Count
    private String eventDate;
    private String time;
    private String requirements;
    private String notes;
    private String city;
    private String locality;
    private String venueType;
    private String budget;
    private boolean aiEnabled;
    private String status; // PENDING, ASSIGNED, CONFIRMED, REJECTED
    private String createdAt;
    private String updatedAt;

    // --- AI PLANNING EXTENSIONS ---
    private String aiPlanJson;
    private String theme;

    // --- ORGANIZER ASSIGNMENT EXTENSIONS ---
    private String organizerId;
    private String organizerName;
    private String organizerStatus; // NOT_ASSIGNED, PENDING, ACCEPTED, REJECTED
    private String assignedAt;
    private String assignedBy;
    private String acceptedAt;
    private String rejectedAt;
    private String adminReviewNotes;

    // --- GUEST MANAGEMENT & QR EXTENSIONS ---
    private String guestMode; // "MANUAL" or "QR_ONLY"
    private int guestLimit;
    private int checkedInCount = 0;
    private String qrToken;
    private List<Guest> guestList = new ArrayList<>(); // Renamed from guests to avoid duplicate name

    public Event() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAiPlanJson() {
        return aiPlanJson;
    }

    public void setAiPlanJson(String aiPlanJson) {
        this.aiPlanJson = aiPlanJson;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    // --- ORGANIZER GETTERS AND SETTERS ---
    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerStatus() {
        return organizerStatus;
    }

    public void setOrganizerStatus(String organizerStatus) {
        this.organizerStatus = organizerStatus;
    }

    public String getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(String assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(String acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public String getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(String rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public String getAdminReviewNotes() {
        return adminReviewNotes;
    }

    public void setAdminReviewNotes(String adminReviewNotes) {
        this.adminReviewNotes = adminReviewNotes;
    }

    // --- GUEST MANAGEMENT GETTERS & SETTERS ---
    public String getGuestMode() {
        return guestMode;
    }

    public void setGuestMode(String guestMode) {
        this.guestMode = guestMode;
    }

    public int getGuestLimit() {
        return guestLimit;
    }

    public void setGuestLimit(int guestLimit) {
        this.guestLimit = guestLimit;
    }

    public int getCheckedInCount() {
        return checkedInCount;
    }

    public void setCheckedInCount(int checkedInCount) {
        this.checkedInCount = checkedInCount;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public List<Guest> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<Guest> guestList) {
        this.guestList = guestList;
    }
}