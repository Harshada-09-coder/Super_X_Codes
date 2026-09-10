package com.techtrio.services;

import com.google.cloud.firestore.*;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Payment;

import java.util.HashMap;
import java.util.Map;

public class PaymentService {

    private final Firestore db = FirebaseConfig.getFirestore();

    public void processPaymentAndDistribute(Payment payment, String eventId, double paidAmount) throws Exception {
        if (db == null || eventId == null || eventId.isBlank()) return;

        final String finalEventId = eventId.trim();

        // १. Payment रेकॉर्ड 'payments' कलेक्शनमध्ये सेव्ह करणे
        DocumentReference paymentRef = db.collection("payments").document();
        if (payment != null) {
            payment.setPaymentId(paymentRef.getId());
            paymentRef.set(payment).get();
        }

        // २. Event चे Paid Amount, Payment Status आणि Status अपडेट करणे
        DocumentReference eventRef = db.collection("events").document(finalEventId);
        DocumentSnapshot eventSnap = eventRef.get().get();

        double currentPaid = 0.0;
        double totalBudget = 0.0;
        String organizerId = "";

        if (eventSnap.exists()) {
            Double p = eventSnap.getDouble("paidAmount");
            Double b = eventSnap.getDouble("totalBudget");
            if (p != null) currentPaid = p;
            if (b != null) totalBudget = b;

            if (totalBudget <= 0) {
                String rawBudget = eventSnap.getString("budget");
                if (rawBudget != null) {
                    try {
                        String clean = rawBudget.replaceAll("[^0-9.]", "").trim();
                        if (!clean.isEmpty()) totalBudget = Double.parseDouble(clean);
                    } catch (Exception ignored) {}
                }
            }
            organizerId = eventSnap.getString("organizerId");
        }

        double newPaid = currentPaid + paidAmount;
        Map<String, Object> eventUpdate = new HashMap<>();
        eventUpdate.put("paidAmount", newPaid);

        // पूर्ण भरले असल्यास PAID & CONFIRMED
        if (newPaid >= totalBudget && totalBudget > 0) {
            eventUpdate.put("paymentStatus", "PAID");
            eventUpdate.put("status", "CONFIRMED");
        } else {
            eventUpdate.put("paymentStatus", "ADVANCE_PAID");
        }
        eventRef.update(eventUpdate).get();

        // ३. ⭐ Vendor Bookings अपडेट करणे (कलेक्शनचे नाव 'vendorBookings')
        QuerySnapshot bookingsSnap = db.collection("vendorBookings")
                .whereEqualTo("eventId", finalEventId)
                .get().get();

        for (QueryDocumentSnapshot bookingDoc : bookingsSnap) {
            String vendorId = bookingDoc.getString("vendorId");
            Double rawCost = bookingDoc.getDouble("cost");
            if (rawCost == null) {
                String amtStr = bookingDoc.getString("amount");
                if (amtStr != null) {
                    try {
                        String clean = amtStr.replaceAll("[^0-9.]", "").trim();
                        if (!clean.isEmpty()) rawCost = Double.parseDouble(clean);
                    } catch (Exception ignored) {}
                }
            }

            final double finalVendorCost = (rawCost != null) ? rawCost : 0.0;

            // ⭐ नियम: Venue असेल तर 0% Cut (100% रक्कम), इतर Vendor असेल तर 3% Cut (97% रक्कम)
            String category = bookingDoc.getString("serviceCategory");
            boolean isVenue = category != null && category.toLowerCase().contains("venue");

            final double netPayoutToVendor = isVenue ? finalVendorCost : (finalVendorCost * 0.97);

            // Vendor च्या खात्यात (totalEarnings) रक्कम जमा करणे
            if (vendorId != null && !vendorId.isBlank()) {
                DocumentReference vendorRef = db.collection("vendors").document(vendorId.trim());
                try {
                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot vSnap = transaction.get(vendorRef).get();
                        double currentEarning = 0.0;
                        if (vSnap.exists() && vSnap.contains("totalEarnings")) {
                            Double d = vSnap.getDouble("totalEarnings");
                            if (d != null) currentEarning = d;
                        }
                        transaction.update(vendorRef, "totalEarnings", currentEarning + netPayoutToVendor);
                        return null;
                    }).get();
                } catch (Exception ignored) {}
            }

            // बुकिंग स्टेटस 'PAID' करणे
            bookingDoc.getReference().update("paymentStatus", "PAID", "status", "PAID");
        }

        // ४. ⭐ Organizer Real-Time Distribution (कस्टमरने भरलेल्या रकमेचे थेट १५%)
        final double organizerShare15 = paidAmount * 0.15;

        if (organizerId != null && !organizerId.isBlank()) {
            DocumentReference orgRef = db.collection("organizers").document(organizerId.trim());
            try {
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot oSnap = transaction.get(orgRef).get();
                    double currentEarning = 0.0;
                    if (oSnap.exists() && oSnap.contains("totalEarnings")) {
                        Double d = oSnap.getDouble("totalEarnings");
                        if (d != null) currentEarning = d;
                    }
                    transaction.update(orgRef, "totalEarnings", currentEarning + organizerShare15);
                    return null;
                }).get();
            } catch (Exception ignored) {}
        }
    }
}