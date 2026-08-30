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
import com.techtrio.model.OrganizerProfile;

public class OrganizerProfileService {

    public static final String COLLECTION_NAME = "Organizers";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    public boolean saveOrganizerProfile(OrganizerProfile profile) {
        if (profile == null || profile.getOrganizerId() == null || profile.getOrganizerId().isBlank()) {
            System.err.println("Cannot save profile: Organizer ID is null/empty.");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            if (profile.getCreatedAt() == null) profile.setCreatedAt(now);
            profile.setUpdatedAt(now);

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(profile.getOrganizerId().trim())
                    .set(profile);

            future.get();
            System.out.println("Organizer profile saved successfully in Firestore: " + profile.getOrganizerId());
            return true;

        } catch (Exception e) {
            System.err.println("Error saving Organizer Profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public OrganizerProfile getOrganizerProfile(String organizerId) {
        if (organizerId == null || organizerId.isBlank()) return null;

        try {
            Firestore db = getDb();
            if (db == null) return null;

            ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME)
                    .document(organizerId.trim())
                    .get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                OrganizerProfile profile = document.toObject(OrganizerProfile.class);
                if (profile != null && (profile.getOrganizerId() == null || profile.getOrganizerId().isBlank())) {
                    profile.setOrganizerId(organizerId);
                }
                return profile;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error fetching Organizer Profile: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ==========================================
    // READ ALL ORGANIZERS (FOR ADMIN)
    // ==========================================
    public List<OrganizerProfile> getAllOrganizers() {
        List<OrganizerProfile> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (DocumentSnapshot doc : documents) {
                OrganizerProfile profile = doc.toObject(OrganizerProfile.class);
                if (profile != null) {
                    if (profile.getOrganizerId() == null || profile.getOrganizerId().isBlank()) {
                        profile.setOrganizerId(doc.getId());
                    }
                    list.add(profile);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching all Organizers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateOrganizerProfileFields(String organizerId, Map<String, Object> updates) {
        if (organizerId == null || organizerId.isBlank() || updates == null || updates.isEmpty()) {
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            updates.put("updatedAt", Instant.now().toString());

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(organizerId.trim())
                    .update(updates);

            future.get();
            System.out.println("Organizer profile fields updated in Firestore for UID: " + organizerId);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating Organizer Profile fields: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean markOrganizerAsExisting(String organizerId) {
        if (organizerId == null || organizerId.isBlank()) return false;

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileCompleted", true);
        updates.put("onboardingStatus", "EXISTING");

        return updateOrganizerProfileFields(organizerId, updates);
    }
}