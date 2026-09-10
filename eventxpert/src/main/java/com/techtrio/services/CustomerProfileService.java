package com.techtrio.services;

import java.time.Instant;
import java.util.ArrayList;
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

    // ==========================================
    // CREATE / SAVE CUSTOMER PROFILE
    // ==========================================
    public boolean saveCustomerProfile(CustomerProfile profile) {
        if (profile == null || profile.getCustomerId() == null || profile.getCustomerId().isBlank()) {
            System.err.println("Cannot save profile: Customer ID is null/empty.");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            String now = Instant.now().toString();
            if (profile.getCreatedAt() == null || profile.getCreatedAt().isBlank()) {
                profile.setCreatedAt(now);
            }

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(profile.getCustomerId().trim())
                    .set(profile);

            future.get();
            System.out.println("Customer profile saved in Firestore: " + profile.getCustomerId());
            return true;

        } catch (Exception e) {
            System.err.println("Error saving Customer Profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // READ SINGLE CUSTOMER PROFILE
    // ==========================================
    public CustomerProfile getCustomerProfile(String customerId) {
        if (customerId == null || customerId.isBlank()) return null;

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

    // ==========================================
    // READ ALL CUSTOMER PROFILES (FOR ADMIN)
    // ==========================================
    public List<CustomerProfile> getAllCustomerProfiles() {
        List<CustomerProfile> list = new ArrayList<>();
        try {
            Firestore db = getDb();
            if (db == null) return list;

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (DocumentSnapshot doc : documents) {
                CustomerProfile profile = doc.toObject(CustomerProfile.class);
                if (profile != null) {
                    if (profile.getCustomerId() == null || profile.getCustomerId().isBlank()) {
                        profile.setCustomerId(doc.getId());
                    }
                    list.add(profile);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching all Customer Profiles: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================
    // UPDATE CUSTOMER PROFILE FIELDS
    // ==========================================
    public boolean updateCustomerProfileFields(String customerId, Map<String, Object> updates) {
        if (customerId == null || customerId.isBlank() || updates == null || updates.isEmpty()) {
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) return false;

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(customerId.trim())
                    .update(updates);

            future.get();
            System.out.println("Customer profile fields updated in Firestore for UID: " + customerId);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating Customer Profile fields: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // DELETE CUSTOMER PROFILE
    // ==========================================
    public boolean deleteCustomerProfile(String customerId) {
        if (customerId == null || customerId.isBlank()) return false;

        try {
            Firestore db = getDb();
            if (db == null) return false;

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(customerId.trim())
                    .delete();

            future.get();
            return true;

        } catch (Exception e) {
            System.err.println("Error deleting Customer Profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}