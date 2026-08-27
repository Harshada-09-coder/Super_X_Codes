package com.techtrio.services;

import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.VendorProfile;

public class VendorProfileService {

    public static final String COLLECTION_NAME = "Vendors";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    // =========================
    // CREATE / SAVE PROFILE
    // =========================
    public boolean saveVendorProfile(VendorProfile vendor) {
        if (vendor == null || vendor.getVendorId() == null || vendor.getVendorId().isBlank()) {
            System.err.println("Cannot save profile: Vendor or Vendor ID is null/empty.");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) {
                System.err.println("Firestore instance unavailable.");
                return false;
            }

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(vendor.getVendorId())
                    .set(vendor);

            future.get();
            System.out.println("Vendor profile saved successfully for UID: " + vendor.getVendorId());
            return true;

        } catch (Exception e) {
            System.err.println("Error saving vendor profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // READ / GET PROFILE
    // =========================
    public VendorProfile getVendorProfile(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            System.err.println("Cannot fetch profile: vendorId is null or blank.");
            return null;
        }

        try {
            Firestore db = getDb();
            if (db == null) {
                System.err.println("Firestore instance unavailable.");
                return null;
            }

            ApiFuture<DocumentSnapshot> future = db.collection(COLLECTION_NAME)
                    .document(vendorId.trim())
                    .get();

            DocumentSnapshot document = future.get();

            if (document.exists()) {
                System.out.println("Vendor profile found for UID: " + vendorId);
                VendorProfile profile = document.toObject(VendorProfile.class);
                if (profile != null && (profile.getVendorId() == null || profile.getVendorId().isBlank())) {
                    profile.setVendorId(vendorId);
                }
                return profile;
            } else {
                System.out.println("Vendor profile document does not exist for UID: " + vendorId);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error getting vendor profile for UID " + vendorId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // UPDATE PROFILE (PARTIAL MERGE)
    // =========================
    public boolean updateVendorProfileFields(String vendorId, Map<String, Object> updates) {
        if (vendorId == null || vendorId.isBlank() || updates == null || updates.isEmpty()) {
            System.err.println("Cannot update profile: vendorId or updates map is null/empty.");
            return false;
        }

        try {
            Firestore db = getDb();
            if (db == null) {
                System.err.println("Firestore instance unavailable.");
                return false;
            }

            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(vendorId.trim())
                    .update(updates);

            future.get();
            System.out.println("Vendor profile updated successfully in Firestore for UID: " + vendorId);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating vendor profile in Firestore for UID " + vendorId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}