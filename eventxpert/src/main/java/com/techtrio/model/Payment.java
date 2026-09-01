package com.techtrio.model;

public class Payment {

    private String paymentId;
    private String eventId;
    private String customerId;
    private String customerName;
    private double totalAmount;
    
    // Revenue Distribution (Split) Fields
    private String organizerId;
    private double organizerAmount;
    private double vendorAmount;
    private double platformFee;
    
    // Transaction Details
    private String paymentMethod; // UPI, CARD, NET_BANKING
    private String paymentStatus; // SUCCESS, FAILED, PENDING
    private String transactionId;
    private String paymentDate;

    // Default Constructor (Firestore साठी अनिवार्य)
    public Payment() {
    }

    // Parameterized Constructor
    public Payment(String paymentId, String eventId, String customerId, String customerName,
                   double totalAmount, String organizerId, double organizerAmount,
                   double vendorAmount, double platformFee, String paymentMethod,
                   String paymentStatus, String transactionId, String paymentDate) {
        this.paymentId = paymentId;
        this.eventId = eventId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.organizerId = organizerId;
        this.organizerAmount = organizerAmount;
        this.vendorAmount = vendorAmount;
        this.platformFee = platformFee;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public double getOrganizerAmount() {
        return organizerAmount;
    }

    public void setOrganizerAmount(double organizerAmount) {
        this.organizerAmount = organizerAmount;
    }

    public double getVendorAmount() {
        return vendorAmount;
    }

    public void setVendorAmount(double vendorAmount) {
        this.vendorAmount = vendorAmount;
    }

    public double getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(double platformFee) {
        this.platformFee = platformFee;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}
    

