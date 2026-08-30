package com.techtrio.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.VendorBooking;

public class VendorBookingService {

    public static final String COLLECTION_NAME = "vendorBookings";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    public boolean createVendorBooking(VendorBooking booking) {
        if (booking == null || booking.getBookingId() == null || booking.getBookingId().isBlank()) {
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            if (booking.getCreatedAt() == null) booking.setCreatedAt(now);
            booking.setUpdatedAt(now);

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(booking.getBookingId().trim())
                    .set(booking);

            future.get();
            System.out.println("Vendor booking created in Firestore: " + booking.getBookingId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<VendorBooking> getBookingsByVendorId(String vendorId) {
        List<VendorBooking> list = new ArrayList<>();
        if (vendorId == null || vendorId.isBlank()) return list;

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("vendorId", vendorId.trim())
                    .get();

            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                VendorBooking b = doc.toObject(VendorBooking.class);
                if (b != null) {
                    if (b.getBookingId() == null || b.getBookingId().isBlank()) {
                        b.setBookingId(doc.getId());
                    }
                    list.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<VendorBooking> getBookingsByEventId(String eventId) {
        List<VendorBooking> list = new ArrayList<>();
        if (eventId == null || eventId.isBlank()) return list;

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("eventId", eventId.trim())
                    .get();

            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                VendorBooking b = doc.toObject(VendorBooking.class);
                if (b != null) {
                    if (b.getBookingId() == null || b.getBookingId().isBlank()) {
                        b.setBookingId(doc.getId());
                    }
                    list.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateBookingStatus(String bookingId, String status) {
        if (bookingId == null || bookingId.isBlank()) return false;

        try {
            Firestore db = getDb();
            if (db == null) return false;

            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status);
            updates.put("updatedAt", Instant.now().toString());

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(bookingId.trim())
                    .update(updates);

            future.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}