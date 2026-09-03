package com.techtrio.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.techtrio.model.Payment;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RazorpayService {

    // Razorpay Test Keys
    private static final String KEY_ID = "rzp_test_TXR1wVKxKP2EpL";
    private static final String KEY_SECRET = "CgV1qME0TQzPSPBK1IG4cGdz";

    private RazorpayClient client;

    public RazorpayService() {
        try {
            this.client = new RazorpayClient(KEY_ID, KEY_SECRET);
        } catch (RazorpayException e) {
            e.printStackTrace();
        }
    }

    /**
     * Razorpay वर Order तयार करणे
     */
    public String createOrder(double amountInRupees, String receiptId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amountInRupees * 100)); // पैसे पैशांमध्ये (Paise)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);
        orderRequest.put("payment_capture", 1);

        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }

    /**
     * Real-Time Payment Distribution Logic:
     * 1. Admin (Platform Fee - 5%)
     * 2. Vendor Total
     * 3. Organizer Remaining
     */
    public boolean processRealtimeDistribution(String eventId, String customerId, String organizerId, 
                                               String vendorId, double totalAmount, double vendorShare, 
                                               String razorpayPaymentId, String orderId) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            double platformFeePercentage = 0.05; // ५% प्लॅटफॉर्म फी
            double platformFee = totalAmount * platformFeePercentage;
            double organizerShare = totalAmount - (platformFee + vendorShare);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Payment payment = new Payment();
            payment.setPaymentId(razorpayPaymentId);
            payment.setTransactionId(razorpayPaymentId);
            payment.setOrderId(orderId);
            payment.setEventId(eventId);
            payment.setCustomerId(customerId);
            payment.setOrganizerId(organizerId);
            payment.setTotalAmount(totalAmount);
            payment.setPlatformFee(platformFee);
            payment.setOrganizerAmount(organizerShare);
            payment.setVendorAmount(vendorShare);
            payment.setStatus("SUCCESS");
            payment.setTimestamp(timestamp);

            // Firebase: payments कलेक्शनमध्ये सेव्ह करा
            db.collection("payments").document(razorpayPaymentId).set(payment);

            // Event Status 'PAID' करा
            Map<String, Object> eventUpdate = new HashMap<>();
            eventUpdate.put("paymentStatus", "PAID");
            eventUpdate.put("status", "CONFIRMED");
            db.collection("events").document(eventId).set(eventUpdate, SetOptions.merge());

            // Admin Revenue अपडेट
            Map<String, Object> adminData = new HashMap<>();
            adminData.put("totalPlatformFeesCollected", platformFee);
            adminData.put("lastUpdated", timestamp);
            db.collection("admin_finances").document("revenue_summary")
              .set(adminData, SetOptions.merge());

            // Organizer Wallet/Earnings अपडेट
            Map<String, Object> orgEarning = new HashMap<>();
            orgEarning.put("eventId", eventId);
            orgEarning.put("earnedAmount", organizerShare);
            orgEarning.put("status", "CREDITED");
            orgEarning.put("timestamp", timestamp);
            db.collection("organizers").document(organizerId)
              .collection("earnings").document(eventId).set(orgEarning);

            // Vendor Earnings अपडेट
            if (vendorId != null && !vendorId.isEmpty()) {
                Map<String, Object> vendorEarning = new HashMap<>();
                vendorEarning.put("eventId", eventId);
                vendorEarning.put("payoutAmount", vendorShare);
                vendorEarning.put("status", "ESCROW_LOCKED");
                vendorEarning.put("timestamp", timestamp);
                db.collection("vendors").document(vendorId)
                  .collection("earnings").document(eventId).set(vendorEarning);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 1. Split Calculation (PaymentController साठी)
     */
    public Payment calculateSplit(String eventId, String customerId, String customerName, 
                                  String organizerId, double totalAmount, double vendorCost) {
        double platformFee = totalAmount * 0.05; // 5% Admin Platform Fee
        double organizerAmount = totalAmount - (platformFee + vendorCost);

        Payment payment = new Payment();
        payment.setEventId(eventId);
        payment.setCustomerId(customerId);
        payment.setCustomerName(customerName);
        payment.setOrganizerId(organizerId);
        payment.setTotalAmount(totalAmount);
        payment.setPlatformFee(platformFee);
        payment.setVendorAmount(vendorCost);
        payment.setOrganizerAmount(organizerAmount);
        payment.setTransactionId("TXN_" + System.currentTimeMillis());
        payment.setPaymentId("PAY_" + System.currentTimeMillis());
        payment.setStatus("CONFIRMED");
        payment.setTimestamp(LocalDateTime.now().toString());

        return payment;
    }

    /**
     * 2. Process Payment (PaymentController साठी)
     */
    public boolean processPayment(Payment payment) {
        if (payment == null) return false;
        try {
            Firestore db = FirestoreClient.getFirestore();

            String payId = payment.getPaymentId() != null ? payment.getPaymentId() : ("PAY_" + System.currentTimeMillis());
            db.collection("payments").document(payId).set(payment);

            if (payment.getEventId() != null) {
                Map<String, Object> update = new HashMap<>();
                update.put("status", "CONFIRMED");
                update.put("paymentStatus", "PAID");
                db.collection("events").document(payment.getEventId())
                  .set(update, SetOptions.merge());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 3. Get Payment by Event ID (PaymentController साठी)
     */
    public Payment getPaymentByEventId(String eventId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("payments").whereEqualTo("eventId", eventId).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            if (!docs.isEmpty()) {
                return docs.get(0).toObject(Payment.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 4. Get All Payments (PaymentController साठी)
     */
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("payments").get();
            for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                list.add(doc.toObject(Payment.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 5. Get Total Platform Revenue (PaymentController साठी)
     */
    public double getTotalPlatformRevenue() {
        double total = 0;
        try {
            List<Payment> all = getAllPayments();
            for (Payment p : all) {
                total += p.getPlatformFee();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}