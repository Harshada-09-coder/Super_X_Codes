package com.techtrio.model;

public class MyEvent {

    private String eventId;
    private String customerId;     // किंवा uid
    private String eventName;
    private String eventType;      // उदा. Academic Event, Family Event, Birthday Event
    private String eventDate;      // YYYY-MM-DD फॉरमॅट (उदा. 2026-08-15)
    private String eventTime;      // उदा. 10:00 AM - 04:00 PM
    private String city;           // उदा. Pune, Mumbai
    private String venueType;      // उदा. Banquet Hall, Auditorium
    private int guestCount;        // पाहुण्यांची संख्या
    private double budget;         // इव्हेंटचे बजेट
    private String status;         // PLANNING, UPCOMING, COMPLETED, PENDING
    private String notes;          // अतिरिक्त सूचना / केटरिंग नोट्स

    // Firebase Firestore साठी No-Argument Default Constructor आवश्यक आहे
    public MyEvent() {
    }

    // सर्व फील्ड्ससह Parameterized Constructor
    public MyEvent(String eventId, String customerId, String eventName, String eventType,
                 String eventDate, String eventTime, String city, String venueType,
                 int guestCount, double budget, String status, String notes) {
        this.eventId = eventId;
        this.customerId = customerId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.city = city;
        this.venueType = venueType;
        this.guestCount = guestCount;
        this.budget = budget;
        this.status = status;
        this.notes = notes;
    }

    // ================= GETTERS & SETTERS =================

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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
