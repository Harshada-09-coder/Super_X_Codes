package com.techtrio.services;

import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
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
}