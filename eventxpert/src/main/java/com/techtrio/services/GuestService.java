package com.techtrio.services;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuestService {

    public static class CheckInResult {
        private final boolean success;
        private final String message;
        private final int currentCheckedIn;
        private final int remainingEntries;

        public CheckInResult(boolean success, String message, int currentCheckedIn, int remainingEntries) {
            this.success = success;
            this.message = message;
            this.currentCheckedIn = currentCheckedIn;
            this.remainingEntries = remainingEntries;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getCurrentCheckedIn() { return currentCheckedIn; }
        public int getRemainingEntries() { return remainingEntries; }
    }

    /**
     * 1. QR MODE ATOMIC CHECK-IN (Enforces guestLimit in Firestore)
     */
    public CheckInResult processQREntryCheckIn(String eventId, String scannedToken) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference eventRef = db.collection("events").document(eventId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef).get();
            if (!snapshot.exists()) {
                return new CheckInResult(false, "Event not found.", 0, 0);
            }

            String storedToken = snapshot.getString("qrToken");
            if (storedToken == null || !storedToken.equals(scannedToken)) {
                return new CheckInResult(false, "Invalid QR Token for this event.", 0, 0);
            }

            long limit = snapshot.contains("guestLimit") && snapshot.get("guestLimit") != null 
                    ? ((Number) snapshot.get("guestLimit")).longValue() : 0;
            long checkedIn = snapshot.contains("checkedInCount") && snapshot.get("checkedInCount") != null 
                    ? ((Number) snapshot.get("checkedInCount")).longValue() : 0;

            if (checkedIn >= limit) {
                return new CheckInResult(false, "Guest entry limit reached for this event.", (int) checkedIn, 0);
            }

            long newCheckedIn = checkedIn + 1;
            long remaining = Math.max(0, limit - newCheckedIn);

            transaction.update(eventRef, "checkedInCount", (int) newCheckedIn);

            // Log
            DocumentReference logRef = eventRef.collection("checkInLogs").document();
            Map<String, Object> log = new HashMap<>();
            log.put("timestamp", System.currentTimeMillis());
            log.put("mode", "QR_ONLY");
            transaction.set(logRef, log);

            return new CheckInResult(true, "Entry Allowed ✓", (int) newCheckedIn, (int) remaining);
        }).get();
    }

    /**
     * 2. MANUAL GUEST CHECK-IN (Safe Long/Map Casting)
     */
    public CheckInResult checkInManualGuest(String eventId, String guestId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference eventRef = db.collection("events").document(eventId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef).get();
            if (!snapshot.exists()) {
                return new CheckInResult(false, "Event not found.", 0, 0);
            }

            List<Map<String, Object>> guests = (List<Map<String, Object>>) snapshot.get("guestList");
            if (guests == null) guests = (List<Map<String, Object>>) snapshot.get("guests");
            if (guests == null) guests = new ArrayList<>();

            long limit = snapshot.contains("guestLimit") && snapshot.get("guestLimit") != null 
                    ? ((Number) snapshot.get("guestLimit")).longValue() : guests.size();
            long checkedIn = snapshot.contains("checkedInCount") && snapshot.get("checkedInCount") != null 
                    ? ((Number) snapshot.get("checkedInCount")).longValue() : 0;

            boolean found = false;
            for (Map<String, Object> g : guests) {
                if (guestId.equals(g.get("id"))) {
                    found = true;
                    if (Boolean.TRUE.equals(g.get("checkedIn"))) {
                        return new CheckInResult(false, "Guest is already checked in.", (int) checkedIn, (int) (limit - checkedIn));
                    }
                    g.put("checkedIn", true);
                    g.put("checkInTime", System.currentTimeMillis());
                    checkedIn++;
                    break;
                }
            }

            if (!found) {
                return new CheckInResult(false, "Guest not found on list.", (int) checkedIn, (int) (limit - checkedIn));
            }

            transaction.update(eventRef, "guestList", guests);
            transaction.update(eventRef, "checkedInCount", (int) checkedIn);

            return new CheckInResult(true, "Guest Checked In Successfully ✓", (int) checkedIn, (int) Math.max(0, limit - checkedIn));
        }).get();
    }
}