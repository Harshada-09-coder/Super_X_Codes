package com.techtrio.controller;

import java.util.List;
import com.techtrio.model.Payment;
import com.techtrio.services.RazorpayService;

public class PaymentController {

    private final RazorpayService razorService;

    public PaymentController() {
        this.razorService = new RazorpayService();
    }

    /**
     * Prepare Payment Breakdown (Split Calculation)
     */
    public Payment preparePaymentSummary(String eventId, String customerId, String customerName,
                                          String organizerId, double totalAmount, double vendorCost) {
        return razorService.calculateSplit(eventId, customerId, customerName, organizerId, totalAmount, vendorCost);
    }

    /**
     * Process & Save Payment
     */
    public boolean processPayment(Payment payment, String paymentMethod) {
        if (payment == null) return false;
        payment.setPaymentMethod(paymentMethod);
        return razorService.processPayment(payment); // 👈 दुरुस्त केले (आधी razorpayService typo होता)
    }

    /**
     * Validate UPI ID Format
     */
    public boolean validateUpiId(String upiId) {
        if (upiId == null || upiId.isBlank()) return false;
        return upiId.matches("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$");
    }

    /**
     * Validate Card Details (16-digit card, MM/YY expiry, 3-digit CVV)
     */
    public boolean validateCardDetails(String cardNumber, String expiry, String cvv) {
        if (cardNumber == null || expiry == null || cvv == null) return false;

        String cleanCard = cardNumber.replaceAll("\\s+", "");
        boolean cardValid = cleanCard.matches("^\\d{16}$");
        boolean expiryValid = expiry.matches("^(0[1-9]|1[0-2])/[0-9]{2}$");
        boolean cvvValid = cvv.matches("^\\d{3}$");

        return cardValid && expiryValid && cvvValid;
    }

    /**
     * Verify Demo OTP (Default: 123456 किंवा कोणताही 6-अंकी नंबर)
     */
    public boolean verifyOtp(String enteredOtp) {
        if (enteredOtp == null || enteredOtp.isBlank()) return false;
        return enteredOtp.trim().equals("123456") || enteredOtp.trim().matches("^\\d{6}$");
    }

    /**
     * Fetch Payment by Event ID
     */
    public Payment getPaymentByEventId(String eventId) {
        return razorService.getPaymentByEventId(eventId); // 👈 दुरुस्त केले (आधी paymentService लिहिले होते)
    }

    /**
     * Fetch All Payments (Admin Revenue साठी)
     */
    public List<Payment> getAllPayments() {
        return razorService.getAllPayments();
    }

    public double getTotalPlatformRevenue() {
        return razorService.getTotalPlatformRevenue();
    }
}