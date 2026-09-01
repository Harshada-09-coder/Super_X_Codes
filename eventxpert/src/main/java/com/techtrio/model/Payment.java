package com.techtrio.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Payment {
    private String paymentId;
    private String razorpayPaymentLinkId;
    private String razorpayPaymentId;
    private String eventId;
    private String customerId;
    
    // Amount Distribution Breakdown
    private double totalAmount;
    private double platformFee;       // Admin चा नफा (Platform Cut)
    private double organizerAmount;   // Organizer ला मिळणारी रक्कम
    private Map<String, Double> vendorAmounts; // Vendor ID -> Vendor Amount
    
    private String status; // "PENDING", "SUCCESS", "FAILED"
    private Date timestamp;

    public Payment() {
        this.vendorAmounts = new HashMap<>();
    }

    public Payment(String eventId, String customerId, double totalAmount, 
                   double platformFeePercent, double organizerPercent, Map<String, Double> vendorAmounts) {
        this.eventId = eventId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.platformFee = (totalAmount * platformFeePercent) / 100.0;
        this.organizerAmount = (totalAmount * organizerPercent) / 100.0;
        this.vendorAmounts = (vendorAmounts != null) ? vendorAmounts : new HashMap<>();
        this.status = "PENDING";
        this.timestamp = new Date();
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getRazorpayPaymentLinkId() { return razorpayPaymentLinkId; }
    public void setRazorpayPaymentLinkId(String razorpayPaymentLinkId) { this.razorpayPaymentLinkId = razorpayPaymentLinkId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPlatformFee() { return platformFee; }
    public void setPlatformFee(double platformFee) { this.platformFee = platformFee; }

    public double getOrganizerAmount() { return organizerAmount; }
    public void setOrganizerAmount(double organizerAmount) { this.organizerAmount = organizerAmount; }

    public Map<String, Double> getVendorAmounts() { return vendorAmounts; }
    public void setVendorAmounts(Map<String, Double> vendorAmounts) { this.vendorAmounts = vendorAmounts; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}