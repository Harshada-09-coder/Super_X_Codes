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
import com.techtrio.model.OrganizerProfile;

public class EventService {

    public static final String COLLECTION_NAME = "events";
    public static final String ORGANIZERS_COLLECTION = "Organizers";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    // --- CREATE EVENT (CUSTOMER SIDE WITH FULL AI DATA) ---
    public boolean createEvent(Event event) {
        if (event == null || event.getEventId() == null || event.getEventId().isBlank()) {
            System.err.println("❌ CreateEvent Error: Event or EventId is null/blank!");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) {
                System.err.println("❌ Firestore Database instance is NULL. Check Firebase initialization!");
                return false;
            }

            if (event.getStatus() == null || event.getStatus().isBlank()) {
                event.setStatus("PENDING");
            }
            if (event.getOrganizerStatus() == null || event.getOrganizerStatus().isBlank()) {
                event.setOrganizerStatus("NOT_ASSIGNED");
            }

            String now = Instant.now().toString();
            if (event.getCreatedAt() == null) event.setCreatedAt(now);
            event.setUpdatedAt(now);

            System.out.println("⏳ Saving Event to Firestore: " + event.getEventId() + " (" + event.getEventName() + ")");

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(event.getEventId().trim())
                    .set(event);

            WriteResult result = future.get();
            System.out.println("✅ Event document [" + event.getEventId() + "] successfully saved in Firestore at " + result.getUpdateTime());
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error saving event to Firestore: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- ORGANIZER SIDE: FETCH ALL PENDING / NEW REQUESTS ---
    public List<Event> getPendingEventRequests() {
        List<Event> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            // Fetch events waiting for an organizer (PENDING or NOT_ASSIGNED)
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("status", "PENDING")
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                Event ev = doc.toObject(Event.class);
                if (ev != null) {
                    if (ev.getEventId() == null || ev.getEventId().isBlank()) {
                        ev.setEventId(doc.getId());
                    }
                    list.add(ev);
                }
            }
            System.out.println(" Fetched " + list.size() + " pending event requests for organizers.");
        } catch (Exception e) {
            System.err.println("❌ Error fetching pending events: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Event> getAvailableEventsForCity(String city) {
        List<Event> list = new ArrayList<>();
        if (city == null || city.isBlank()) return getPendingEventRequests();

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("status", "PENDING")
                    .whereEqualTo("city", city.trim())
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                Event ev = doc.toObject(Event.class);
                if (ev != null) {
                    if (ev.getEventId() == null || ev.getEventId().isBlank()) {
                        ev.setEventId(doc.getId());
                    }
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (DocumentSnapshot doc : documents) {
                Event ev = doc.toObject(Event.class);
                if (ev != null) {
                    if (ev.getEventId() == null || ev.getEventId().isBlank()) {
                        ev.setEventId(doc.getId());
                    }
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Event getEventById(String eventId) {
        if (eventId == null || eventId.isBlank()) return null;
        try {
            Firestore db = getDb();
            if (db == null) return null;

            ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME).document(eventId.trim()).get();
            DocumentSnapshot doc = future.get();
            if (doc.exists()) {
                Event ev = doc.toObject(Event.class);
                if (ev != null && (ev.getEventId() == null || ev.getEventId().isBlank())) {
                    ev.setEventId(doc.getId());
                }
                return ev;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Event> getEventsByCustomerId(String customerId) {
        List<Event> list = new ArrayList<>();
        if (customerId == null || customerId.isBlank()) return list;

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                Event ev = doc.toObject(Event.class);
                if (ev != null) {
                    if (ev.getEventId() == null || ev.getEventId().isBlank()) {
                        ev.setEventId(doc.getId());
                    }
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Event> getEventsByOrganizerId(String organizerId) {
        List<Event> list = new ArrayList<>();
        if (organizerId == null || organizerId.isBlank()) return list;

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("organizerId", organizerId.trim())
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                Event ev = doc.toObject(Event.class);
                if (ev != null) {
                    if (ev.getEventId() == null || ev.getEventId().isBlank()) {
                        ev.setEventId(doc.getId());
                    }
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrganizerProfile> getAllActiveOrganizers() {
        List<OrganizerProfile> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(ORGANIZERS_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (DocumentSnapshot doc : documents) {
                OrganizerProfile org = doc.toObject(OrganizerProfile.class);
                if (org != null) {
                    if (org.getOrganizerId() == null || org.getOrganizerId().isBlank()) {
                        org.setOrganizerId(doc.getId());
                    }
                    list.add(org);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName, String adminUid, String reviewNotes) {
        if (eventId == null || eventId.isBlank() || organizerId == null || organizerId.isBlank()) {
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizerId", organizerId.trim());
            updates.put("organizerName", organizerName != null ? organizerName.trim() : "Organizer");
            updates.put("organizerStatus", "PENDING");
            updates.put("status", "ASSIGNED");
            updates.put("assignedAt", now);
            updates.put("updatedAt", now);

            if (adminUid != null && !adminUid.isBlank()) {
                updates.put("assignedBy", adminUid.trim());
            }
            if (reviewNotes != null && !reviewNotes.isBlank()) {
                updates.put("adminReviewNotes", reviewNotes.trim());
            }

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(eventId.trim())
                    .update(updates);

            future.get();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName) {
        return assignOrganizerToEvent(eventId, organizerId, organizerName, null, null);
    }

    public boolean acceptEventAssignment(String eventId, String organizerId, String organizerName) {
        if (eventId == null || eventId.isBlank()) return false;

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizerId", organizerId);
            updates.put("organizerName", organizerName != null ? organizerName : "Organizer");
            updates.put("organizerStatus", "ACCEPTED");
            updates.put("status", "CONFIRMED");
            updates.put("acceptedAt", now);
            updates.put("updatedAt", now);

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(eventId.trim())
                    .update(updates);

            future.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean acceptEventAssignment(String eventId) {
        return acceptEventAssignment(eventId, "", "");
    }

    public boolean rejectEventAssignment(String eventId) {
        if (eventId == null || eventId.isBlank()) return false;

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizerStatus", "REJECTED");
            updates.put("status", "REJECTED");
            updates.put("rejectedAt", now);
            updates.put("updatedAt", now);

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(eventId.trim())
                    .update(updates);

            future.get();
            return true;
        } catch (Exception e) {
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

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(eventId.trim())
                    .update(updates);

            future.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}