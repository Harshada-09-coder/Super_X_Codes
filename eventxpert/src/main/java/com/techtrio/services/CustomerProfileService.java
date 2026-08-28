package com.techtrio.services;

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
import com.techtrio.model.CustomerProfile;

public class CustomerProfileService {

    public static final String COLLECTION_NAME = "Customers";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    public CustomerProfile getCustomerProfile(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return null;
        }

        try {
            Firestore db = getDb();
            if (db == null) return null;

            ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME)
                    .document(customerId.trim())
                    .get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                CustomerProfile profile = document.toObject(CustomerProfile.class);
                if (profile != null && (profile.getCustomerId() == null || profile.getCustomerId().isBlank())) {
                    profile.setCustomerId(customerId);
                }
                return profile;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error fetching Customer Profile: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<CustomerProfile> getAllCustomerProfiles() {
        List<CustomerProfile> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                CustomerProfile profile = doc.toObject(CustomerProfile.class);
                if (profile != null) {
                    if (profile.getCustomerId() == null || profile.getCustomerId().isBlank()) {
                        profile.setCustomerId(doc.getId());
                    }
                    list.add(profile);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching all customer profiles: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean markCustomerAsExisting(String customerId) {
        if (customerId == null || customerId.isBlank()) return false;

        try {
            Firestore db = getDb();
            if (db == null) return false;

            Map<String, Object> updates = new HashMap<>();
            updates.put("hasCreatedEvent", true);
            updates.put("onboardingStatus", "EXISTING");

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(customerId.trim())
                    .update(updates);

            future.get();
            System.out.println("Customer state updated to EXISTING for UID: " + customerId);
            return true;

        } catch (Exception e) {
            System.err.println("Error marking Customer as EXISTING: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerProfileFields(String customerId, Map<String, Object> updates) {
        if (customerId == null || customerId.isBlank() || updates == null || updates.isEmpty()) {
            System.err.println("Cannot update profile: customerId or updates map is null/empty.");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(customerId.trim())
                    .update(updates);

            future.get();
            System.out.println("Customer profile updated successfully in Firestore for UID: " + customerId);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating Customer Profile in Firestore for UID " + customerId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}