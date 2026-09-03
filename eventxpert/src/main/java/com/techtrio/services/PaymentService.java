package com.techtrio.services;

import com.google.cloud.firestore.*;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Payment;

import java.util.HashMap;
import java.util.Map;

public class PaymentService {

    private final Firestore db = FirebaseConfig.getFirestore();

    public void processPaymentAndDistribute(Payment payment, String eventId, double paidAmount) throws Exception {
        if (db == null) return;

        // १. Save Payment record
        DocumentReference paymentRef = db.collection("payments").document();
        if (payment != null) {
            payment.setPaymentId(paymentRef.getId());
            paymentRef.set(payment).get();
        }

        // २. Update Event Paid Amount
        DocumentReference eventRef = db.collection("events").document(eventId);
        DocumentSnapshot eventSnap = eventRef.get().get();

        double currentPaid = 0.0;
        double totalBudget = 0.0;
        String organizerId = "";

        if (eventSnap.exists()) {
            Double p = eventSnap.getDouble("paidAmount");
            Double b = eventSnap.getDouble("totalBudget");
            if (p != null) currentPaid = p;
            if (b != null) totalBudget = b;
            organizerId = eventSnap.getString("organizerId");
        }

        double newPaid = currentPaid + paidAmount;
        Map<String, Object> eventUpdate = new HashMap<>();
        eventUpdate.put("paidAmount", newPaid);
        if (newPaid >= totalBudget && totalBudget > 0) {
            eventUpdate.put("paymentStatus", "PAID");
        } else {
            eventUpdate.put("paymentStatus", "PARTIAL");
        }
        eventRef.update(eventUpdate).get();

        // ३. Vendor Real-Time Distribution (final variable fix)
        QuerySnapshot bookingsSnap = db.collection("vendor_bookings")
                .whereEqualTo("eventId", eventId)
                .get().get();

        double totalVendorShare = 0.0;
        for (QueryDocumentSnapshot bookingDoc : bookingsSnap) {
            String vendorId = bookingDoc.getString("vendorId");
            Double rawCost = bookingDoc.getDouble("cost");
            
            // ⭐ FIX: final variable for lambda closure
            final double finalVendorCost = (rawCost != null) ? rawCost : 0.0;
            totalVendorShare += finalVendorCost;

            if (vendorId != null && !vendorId.isBlank()) {
                DocumentReference vendorRef = db.collection("vendors").document(vendorId);
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot vSnap = transaction.get(vendorRef).get();
                    double currentEarning = 0.0;
                    if (vSnap.exists() && vSnap.contains("totalEarnings")) {
                        Double d = vSnap.getDouble("totalEarnings");
                        if (d != null) currentEarning = d;
                    }
                    transaction.update(vendorRef, "totalEarnings", currentEarning + finalVendorCost);
                    return null;
                }).get();
            }

            bookingDoc.getReference().update("paymentStatus", "PAID");
        }

        // ४. Organizer Real-Time Distribution
        double organizerShare = paidAmount - totalVendorShare;
        if (organizerShare < 0) organizerShare = paidAmount * 0.15;
        final double finalOrgShare = organizerShare;

        if (organizerId != null && !organizerId.isBlank()) {
            DocumentReference orgRef = db.collection("organizers").document(organizerId);
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot oSnap = transaction.get(orgRef).get();
                double currentEarning = 0.0;
                if (oSnap.exists() && oSnap.contains("totalEarnings")) {
                    Double d = oSnap.getDouble("totalEarnings");
                    if (d != null) currentEarning = d;
                }
                transaction.update(orgRef, "totalEarnings", currentEarning + finalOrgShare);
                return null;
            }).get();
        }
    }
}