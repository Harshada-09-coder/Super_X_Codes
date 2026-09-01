package com.techtrio.services;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;

public class RazorpayService {

    // 🔴 तुमच्या Razorpay Dashboard वरील Test/Live API Keys येथे टाका
    private static final String RAZORPAY_KEY_ID = "rzp_test_YOUR_KEY_ID_HERE";
    private static final String RAZORPAY_KEY_SECRET = "YOUR_KEY_SECRET_HERE";

    private RazorpayClient client;

    public RazorpayService() {
        try {
            this.client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
        } catch (RazorpayException e) {
            System.err.println("Razorpay Client Init Error: " + e.getMessage());
        }
    }

    /**
     * नवीन पेमेंट लिंक आणि UPI QR साठी URL तयार करणे
     */
    public PaymentLink createPaymentLink(String paymentId, double amountInRupees, 
                                         String customerName, String customerEmail, String customerPhone) throws RazorpayException {
        if (client == null) {
            throw new RazorpayException("Razorpay client is not initialized.");
        }

        JSONObject request = new JSONObject();
        // रक्कम पैशांमध्ये हवी असते (Amount in Paise)
        request.put("amount", (int) Math.round(amountInRupees * 100));
        request.put("currency", "INR");
        request.put("accept_partial", false);
        request.put("reference_id", paymentId);
        request.put("description", "EventXpert Booking Payment ID: " + paymentId);

        JSONObject customer = new JSONObject();
        customer.put("name", (customerName != null && !customerName.isEmpty()) ? customerName : "Customer");
        customer.put("email", (customerEmail != null && !customerEmail.isEmpty()) ? customerEmail : "customer@eventxpert.com");
        customer.put("contact", (customerPhone != null && !customerPhone.isEmpty()) ? customerPhone : "9999999999");
        request.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("sms", true);
        notify.put("email", true);
        request.put("notify", notify);
        request.put("reminder_enable", true);

        return client.paymentLink.create(request);
    }

    /**
     * Real-time Payment Status तपासणे
     */
    public boolean checkPaymentStatus(String paymentLinkId) {
        try {
            if (client == null) return false;
            PaymentLink paymentLink = client.paymentLink.fetch(paymentLinkId);
            String status = paymentLink.get("status");
            return "paid".equalsIgnoreCase(status);
        } catch (Exception e) {
            return false;
        }
    }
}