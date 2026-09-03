package com.techtrio.model;

public class Payment {
    private String paymentId;
       private String transactionId;
    private String orderId;
    private String eventId;
    private String customerId; 
    private String customerName;
    private String paymentMethod; 
    private double totalAmount;
    private double platformFee;       // Admin Share
    private double organizerAmount;   // Organizer Share
    private double vendorAmount;      // Vendor Share
    private String status;            // SUCCESS, PENDING, FAILED
    private String timestamp;
    private String organizerId;

    public Payment() {}

    public Payment(String paymentId, String transactionId, String orderId, String eventId, String customerId, String organizerId, String paymentMethod,
                   double totalAmount, double platformFee, double organizerAmount, 
                   double vendorAmount, String status, String timestamp) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.eventId = eventId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.platformFee = platformFee;
        this.organizerAmount = organizerAmount;
        this.vendorAmount = vendorAmount;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

     public String getTransactionId() { return transactionId != null ? transactionId : paymentId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

      public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }


    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPlatformFee() { return platformFee; }
    public void setPlatformFee(double platformFee) { this.platformFee = platformFee; }

    
    public double getOrganizerAmount() { return organizerAmount; }
    public void setOrganizerAmount(double organizerAmount) { this.organizerAmount = organizerAmount; }

    public double getVendorAmount() { return vendorAmount; }
    public void setVendorAmount(double vendorAmount) { this.vendorAmount = vendorAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}