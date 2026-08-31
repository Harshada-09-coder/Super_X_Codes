package com.techtrio.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Payment;

public class PaymentService {

    public static final String COLLECTION_NAME = "payments";
    private final EventService eventService = new EventService();

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    /**
     * Process Payment & Save Distribution to Firestore
     */
    public boolean processPayment(Payment payment) {
        if (payment == null || payment.getEventId() == null || payment.getEventId().isBlank()) {
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            // Generate IDs if not present
            if (payment.getPaymentId() == null || payment.getPaymentId().isBlank()) {
                payment.setPaymentId("PAY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            if (payment.getTransactionId() == null || payment.getTransactionId().isBlank()) {
                payment.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
            }

            // Set Timestamp and Status
            String now = Instant.now().toString();
            payment.setPaymentDate(now);
            payment.setPaymentStatus("SUCCESS");

            // 1. Save Payment Record to Firestore
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(payment.getPaymentId())
                    .set(payment);
            future.get();

            // 2. Update Event Status to PAID
            eventService.updateEventStatus(payment.getEventId(), "PAID");

            System.out.println("Payment successful! ID: " + payment.getPaymentId() + ", Txn: " + payment.getTransactionId());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper to Calculate 3-Way Split (Platform: 5%, Vendor: vendorCost, Organizer: Remaining)
     */
    public Payment calculateSplit(String eventId, String customerId, String customerName, 
                                  String organizerId, double totalAmount, double vendorCost) {
        
        double platformFee = totalAmount * 0.05; // 5% Platform Fee
        double organizerAmount = totalAmount - (platformFee + vendorCost);
        if (organizerAmount < 0) organizerAmount = 0;

        Payment payment = new Payment();
        payment.setEventId(eventId);
        payment.setCustomerId(customerId);
        payment.setCustomerName(customerName);
        payment.setOrganizerId(organizerId);
        payment.setTotalAmount(totalAmount);
        payment.setPlatformFee(platformFee);
        payment.setVendorAmount(vendorCost);
        payment.setOrganizerAmount(organizerAmount);

        return payment;
    }

    /**
     * Get Payment Details by Event ID
     */
    public Payment getPaymentByEventId(String eventId) {
        if (eventId == null || eventId.isBlank()) return null;

        try {
            Firestore db = getDb();
            if (db == null) return null;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("eventId", eventId.trim())
                    .limit(1)
                    .get();

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
     * Get All Payments (Admin Revenue/Analytics साठी)
     */
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                Payment p = doc.toObject(Payment.class);
                if (p != null) list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get Payments for a Specific Organizer (Organizer Revenue साठी)
     */
    public List<Payment> getPaymentsByOrganizerId(String organizerId) {
        List<Payment> list = new ArrayList<>();
        if (organizerId == null || organizerId.isBlank()) return list;

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("organizerId", organizerId.trim())
                    .get();

            for (DocumentSnapshot doc : future.get().getDocuments()) {
                Payment p = doc.toObject(Payment.class);
                if (p != null) list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
