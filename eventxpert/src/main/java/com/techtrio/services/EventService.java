package com.techtrio.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
}