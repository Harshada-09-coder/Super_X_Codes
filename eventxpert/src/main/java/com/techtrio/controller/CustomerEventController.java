package com.techtrio.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;

public class CustomerEventController {

    private Firestore db;

    public CustomerEventController() {
        try {
            this.db = FirestoreClient.getFirestore();
        } catch (Exception e) {
            System.err.println("Error initializing Firestore in CustomerEventController: " + e.getMessage());
        }
    }

    // ================= 1. GET EVENTS BY CUSTOMER ID =================
    public List<Event> getEventsByCustomerId(String customerId) {
        List<Event> eventList = new ArrayList<>();

        if (customerId == null || customerId.isBlank() || db == null) {
            return eventList;
        }

        try {
            ApiFuture<QuerySnapshot> future = db.collection("events")
                    .whereEqualTo("customerId", customerId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (DocumentSnapshot doc : documents) {
                Event event = doc.toObject(Event.class);
                if (event != null) {
                    if (event.getEventId() == null || event.getEventId().isBlank()) {
                        event.setEventId(doc.getId());
                    }
                    eventList.add(event);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching events from Firestore: " + e.getMessage());
        }

        return eventList;
    }

    // ================= 2. GET ORGANIZER DETAILS FROM DATABASE =================
    public Map<String, String> getOrganizerDetailsForEvent(String eventId) {
        Map<String, String> orgData = new HashMap<>();
        orgData.put("name", "Organizer Assigned");
        orgData.put("role", "Event Coordinator");
        orgData.put("email", "support@eventxpert.com");
        orgData.put("phone", "+91 98765 43210");

        if (db == null) return orgData;

        try {
            // १. आधी इव्हेंट डॉक्यूमेंटमधून Organizer माहिती शोधणे
            if (eventId != null && !eventId.isBlank()) {
                DocumentSnapshot evDoc = db.collection("events").document(eventId).get().get();
                if (evDoc.exists()) {
                    String orgId = evDoc.getString("organizerId");
                    String directOrgName = evDoc.getString("organizerName");

                    if (directOrgName != null && !directOrgName.isBlank()) {
                        orgData.put("name", directOrgName);
                        if (evDoc.getString("organizerRole") != null) orgData.put("role", evDoc.getString("organizerRole"));
                        if (evDoc.getString("organizerEmail") != null) orgData.put("email", evDoc.getString("organizerEmail"));
                        if (evDoc.getString("organizerPhone") != null) orgData.put("phone", evDoc.getString("organizerPhone"));
                        return orgData;
                    }

                    // २. जर organizerId असेल तर 'users' किंवा 'organizers' मधून प्रोफाइल आणणे
                    if (orgId != null && !orgId.isBlank()) {
                        DocumentSnapshot userDoc = db.collection("users").document(orgId).get().get();
                        if (userDoc.exists()) {
                            if (userDoc.getString("name") != null) orgData.put("name", userDoc.getString("name"));
                            if (userDoc.getString("role") != null) orgData.put("role", userDoc.getString("role"));
                            if (userDoc.getString("email") != null) orgData.put("email", userDoc.getString("email"));
                            if (userDoc.getString("phone") != null) orgData.put("phone", userDoc.getString("phone"));
                            return orgData;
                        }
                    }
                }
            }

            // ३. डेटाबेस मधील Active Organizer आणणे
            ApiFuture<QuerySnapshot> orgQuery = db.collection("users")
                    .whereEqualTo("role", "Organizer")
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> orgDocs = orgQuery.get().getDocuments();
            if (!orgDocs.isEmpty()) {
                DocumentSnapshot doc = orgDocs.get(0);
                if (doc.getString("name") != null) orgData.put("name", doc.getString("name"));
                if (doc.getString("role") != null) orgData.put("role", doc.getString("role") + " Dept.");
                if (doc.getString("email") != null) orgData.put("email", doc.getString("email"));
                if (doc.getString("phone") != null) orgData.put("phone", doc.getString("phone"));
            }

        } catch (Exception e) {
            System.err.println("Error fetching organizer details from DB: " + e.getMessage());
        }

        return orgData;
    }

    // ================= 3. GET CUSTOMER PROFILE =================
    public CustomerProfile getCustomerProfile(String uid) {
        if (uid == null || uid.isBlank() || db == null) return null;

        try {
            DocumentSnapshot document = db.collection("users").document(uid).get().get();
            if (document.exists()) {
                return document.toObject(CustomerProfile.class);
            }
        } catch (Exception e) {
            System.err.println("Error fetching customer profile: " + e.getMessage());
        }

        CustomerProfile defaultProfile = new CustomerProfile();
        defaultProfile.setName("Customer");
        return defaultProfile;
    }
}