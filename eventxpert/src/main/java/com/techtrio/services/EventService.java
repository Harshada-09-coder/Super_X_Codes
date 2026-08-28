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
import com.techtrio.model.Event;

public class EventService {

    public static final String COLLECTION_NAME = "events";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    public boolean createEvent(Event event) {
        if (event == null || event.getEventId() == null || event.getEventId().isBlank()) {
            System.err.println("Cannot save event: Event or Event ID is null.");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            if (event.getCreatedAt() == null) event.setCreatedAt(now);
            event.setUpdatedAt(now);
            if (event.getStatus() == null || event.getStatus().isBlank()) {
                event.setStatus("PENDING");
            }

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(event.getEventId())
                    .set(event);

            future.get();
            System.out.println("Event successfully created in Firestore! ID: " + event.getEventId());
            return true;

        } catch (Exception e) {
            System.err.println("Error saving event to Firestore: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getEventsByCustomerId(String customerId) {
        List<Event> events = new ArrayList<>();
        if (customerId == null || customerId.isBlank()) return events;

        try {
            Firestore db = getDb();
            if (db == null) return events;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                Event event = doc.toObject(Event.class);
                if (event != null) {
                    events.add(event);
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching events for customerId " + customerId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return events;
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return events;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                Event event = doc.toObject(Event.class);
                if (event != null) {
                    events.add(event);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching all events: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    public Event getEventById(String eventId) {
        if (eventId == null || eventId.isBlank()) return null;
        try {
            Firestore db = getDb();
            if (db == null) return null;

            ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME).document(eventId.trim()).get();
            DocumentSnapshot doc = future.get();
            if (doc.exists()) {
                return doc.toObject(Event.class);
            }
        } catch (Exception e) {
            System.err.println("Error fetching event by ID " + eventId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName) {
        if (eventId == null || eventId.isBlank()) return false;
        try {
            Firestore db = getDb();
            if (db == null) return false;

            Map<String, Object> updates = new HashMap<>();
            updates.put("organizerId", organizerId);
            updates.put("organizerName", organizerName);
            updates.put("status", "ASSIGNED");
            updates.put("updatedAt", Instant.now().toString());

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(eventId.trim()).update(updates);
            future.get();
            System.out.println("Organizer assigned to event " + eventId + " successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("Error assigning organizer to event " + eventId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEventStatus(String eventId, String status) {
        if (eventId == null || eventId.isBlank()) return false;
        try {
            Firestore db = getDb();
            if (db == null) return false;

            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status);
            updates.put("updatedAt", Instant.now().toString());

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(eventId.trim()).update(updates);
            future.get();
            return true;

        } catch (Exception e) {
            System.err.println("Error updating event status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}