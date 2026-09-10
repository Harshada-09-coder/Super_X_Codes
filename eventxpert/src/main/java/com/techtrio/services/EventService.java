package com.techtrio.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;

public class EventService {

    public static final String COLLECTION_NAME = "events";
    public static final String ORGANIZERS_COLLECTION = "Organizers";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    // --- 1. CUSTOMER CREATES EVENT (SAVED AS UNASSIGNED FOR ADMIN) ---
    public boolean createEvent(Event event) {
        if (event == null || event.getEventId() == null || event.getEventId().isBlank()) {
            System.err.println("❌ CreateEvent Error: Event or EventId is null/blank!");
            return false;
        }

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Firestore db = getDb();
                if (db == null) return false;

                // Event goes to ADMIN first
                if (event.getStatus() == null || event.getStatus().isBlank()) {
                    event.setStatus("PENDING");
                }
                if (event.getOrganizerStatus() == null || event.getOrganizerStatus().isBlank()) {
                    event.setOrganizerStatus("NOT_ASSIGNED");
                }
                event.setOrganizerId(null);

                String now = Instant.now().toString();
                if (event.getCreatedAt() == null) event.setCreatedAt(now);
                event.setUpdatedAt(now);

                System.out.println("⏳ Saving Event for Admin Review: " + event.getEventId() + " (" + event.getEventName() + ")");

                ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                        .document(event.getEventId().trim())
                        .set(event);

                WriteResult result = future.get(20, TimeUnit.SECONDS);
                System.out.println("✅ Event document [" + event.getEventId() + "] submitted to Admin at " + result.getUpdateTime());
                return true;

            } catch (Exception e) {
                System.err.println("⚠️ Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt < maxRetries) {
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                } else {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // --- 2. ADMIN SIDE: FETCH UNASSIGNED EVENTS WAITING FOR ORGANIZER ASSIGNMENT ---
    public List<Event> getUnassignedEventsForAdmin() {
        List<Event> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("organizerStatus", "NOT_ASSIGNED")
                    .get();

            List<QueryDocumentSnapshot> documents = future.get(15, TimeUnit.SECONDS).getDocuments();
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

    // --- 3. ADMIN ASSIGNS ORGANIZER TO EVENT ---
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
            updates.put("organizerStatus", "PENDING"); // Now pending for that specific organizer
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

            future.get(15, TimeUnit.SECONDS);
            System.out.println("✅ Admin assigned event [" + eventId + "] to Organizer [" + organizerName + "]");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignOrganizerToEvent(String eventId, String organizerId, String organizerName) {
        return assignOrganizerToEvent(eventId, organizerId, organizerName, null, null);
    }

    // --- 4. ORGANIZER SIDE: FETCH ONLY EVENTS ASSIGNED TO SPECIFIC ORGANIZER UID ---
    public List<Event> getEventsByOrganizerId(String organizerId) {
        List<Event> list = new ArrayList<>();
        if (organizerId == null || organizerId.isBlank()) return list;

        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("organizerId", organizerId.trim())
                    .get();

            List<QueryDocumentSnapshot> documents = future.get(15, TimeUnit.SECONDS).getDocuments();
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

    // --- 5. ORGANIZER ACCEPTS ASSIGNED EVENT ---
    public boolean acceptEventAssignment(String eventId, String organizerId, String organizerName) {
        if (eventId == null || eventId.isBlank()) return false;

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            Map<String, Object> updates = new HashMap<>();
            if (organizerId != null && !organizerId.isBlank()) updates.put("organizerId", organizerId);
            if (organizerName != null && !organizerName.isBlank()) updates.put("organizerName", organizerName);
            updates.put("organizerStatus", "ACCEPTED");
            updates.put("status", "CONFIRMED");
            updates.put("acceptedAt", now);
            updates.put("updatedAt", now);

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(eventId.trim())
                    .update(updates);

            future.get(15, TimeUnit.SECONDS);
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

            future.get(15, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 6. UPDATE EVENT STATUS (RESOLVES CUSTOMER CONTROLLER ERROR) ---
    public boolean updateEventStatus(String eventId, String status) {
        if (eventId == null || eventId.isBlank() || status == null || status.isBlank()) {
            return false;
        }
        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status.trim());
            updates.put("updatedAt", now);

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(eventId.trim())
                    .update(updates);

            future.get(15, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get(15, TimeUnit.SECONDS).getDocuments();

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
            DocumentSnapshot doc = future.get(15, TimeUnit.SECONDS);
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

            List<QueryDocumentSnapshot> documents = future.get(15, TimeUnit.SECONDS).getDocuments();
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
            List<QueryDocumentSnapshot> documents = future.get(15, TimeUnit.SECONDS).getDocuments();

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
    public boolean updateEventBudgetBreakdown(String eventId, double allocatedBudget, Map<String, Object> budgetMap) {
        if (eventId == null || eventId.isBlank()) return false;
        try {
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> updates = new HashMap<>();
            updates.put("allocatedBudget", allocatedBudget);
            updates.put("budgetRecommendation", budgetMap);
            updates.put("budgetOptimizationStatus", "FINALIZED");
            
            db.collection("events").document(eventId.trim()).update(updates).get();
            System.out.println("✅ Budget breakdown updated in Firestore for event: " + eventId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}