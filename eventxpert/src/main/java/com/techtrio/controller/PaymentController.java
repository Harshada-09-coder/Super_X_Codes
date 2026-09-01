package com.techtrio.controller;

import com.google.cloud.firestore.Firestore;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Payment;
import com.techtrio.services.RazorpayService;

import java.util.HashMap;
import java.util.Map;

public class PaymentController {

    private final RazorpayService razorpayService;
    private final Firestore db;

    public PaymentController() {
        this.razorpayService = new RazorpayService();
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * पेमेंट ऑब्जेक्ट तयार करणे व वाटणीचे प्रमाण ठरवणे
     */
    public Payment initiatePayment(String eventId, String customerId, double totalAmount, Map<String, Double> vendorAmounts) {
        double platformFeePercent = 5.0;  // 5% Admin Platform Fee
        double organizerPercent = 15.0;   // 15% Organizer Commission

        return new Payment(eventId, customerId, totalAmount, platformFeePercent, organizerPercent, vendorAmounts);
    }

    /**
     * पेमेंट यशस्वी झाल्यानंतर Firebase मध्ये रेकॉर्ड आणि Payouts अपडेट करणे
     */
    public void recordSuccessfulPayment(Payment payment, String razorpayPaymentId) {
        try {
            payment.setStatus("SUCCESS");
            payment.setRazorpayPaymentId(razorpayPaymentId);

            // १. Payments Collection मध्ये सेव्ह करा
            if (db != null) {
                db.collection("payments").document(payment.getPaymentId()).set(payment);

                // २. Event Collection चा पेमेंट स्टेटस अपडेट करा
                Map<String, Object> eventUpdate = new HashMap<>();
                eventUpdate.put("paymentStatus", "PAID");
                eventUpdate.put("status", "CONFIRMED");
                db.collection("events").document(payment.getEventId()).update(eventUpdate);

                // ३. Organizer चे Pending Payout नोंदवा
                Map<String, Object> orgPayout = new HashMap<>();
                orgPayout.put("eventId", payment.getEventId());
                orgPayout.put("amount", payment.getOrganizerAmount());
                orgPayout.put("status", "PENDING_ADMIN_PAYOUT");
                orgPayout.put("timestamp", payment.getTimestamp());
                db.collection("organizer_payouts").document(payment.getEventId()).set(orgPayout);

                // ४. Vendors चे Bookings आणि Payouts नोंदवा
                if (payment.getVendorAmounts() != null) {
                    for (Map.Entry<String, Double> entry : payment.getVendorAmounts().entrySet()) {
                        Map<String, Object> vPayout = new HashMap<>();
                        vPayout.put("eventId", payment.getEventId());
                        vPayout.put("vendorId", entry.getKey());
                        vPayout.put("amount", entry.getValue());
                        vPayout.put("status", "CONFIRMED");
                        vPayout.put("timestamp", payment.getTimestamp());
                        db.collection("vendor_payouts").document(payment.getEventId() + "_" + entry.getKey()).set(vPayout);
                    }
                }
            }

            System.out.println("=== PAYMENT DISTRIBUTION SUCCESSFUL ===");
            System.out.println("Total Paid by User: ₹" + payment.getTotalAmount());
            System.out.println("Admin Platform Fee (Net Revenue): ₹" + payment.getPlatformFee());
            System.out.println("Organizer Share (To be Disbursed): ₹" + payment.getOrganizerAmount());
            System.out.println("Vendors Total Share: " + payment.getVendorAmounts());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RazorpayService getRazorpayService() {
        return razorpayService;
    }
}