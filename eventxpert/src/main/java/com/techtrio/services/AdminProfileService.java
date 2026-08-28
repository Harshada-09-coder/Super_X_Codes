package com.techtrio.services;

import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.model.AdminProfile;

public class AdminProfileService {

    private static final String COLLECTION = "Admin";

    private Firestore getDb() {
        return FirebaseConfig.getFirestore();
    }

    // =========================
    // CREATE
    // =========================

    public boolean createAdminProfile(AdminProfile admin) {

        try {

            if (admin == null ||
                admin.getAdminId() == null ||
                admin.getAdminId().isBlank()) {

                return false;
            }

            Firestore db = getDb();

            Map<String, Object> data = new HashMap<>();

            data.put("adminId", admin.getAdminId());
            data.put("name", admin.getName());
            data.put("email", admin.getEmail());
            data.put("mobileNo", admin.getmobileNo());
            data.put("address", admin.getAddress());

            if (admin.getCreatedAt() != null) {
                data.put("createdAt", admin.getCreatedAt());
            } else {
                data.put("createdAt", Timestamp.now());
            }

            ApiFuture<WriteResult> future =
                    db.collection(COLLECTION)
                      .document(admin.getAdminId())
                      .set(data);

            future.get();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // READ
    // =========================

    public AdminProfile getAdminProfile(String adminId) {

        try {

            if (adminId == null || adminId.isBlank()) {
                return null;
            }

            Firestore db = getDb();

            ApiFuture<DocumentSnapshot> future =
                    db.collection(COLLECTION)
                      .document(adminId)
                      .get();

            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return null;
            }

            AdminProfile admin =
                    document.toObject(AdminProfile.class);

            if (admin != null &&
                (admin.getAdminId() == null ||
                 admin.getAdminId().isBlank())) {

                admin.setAdminId(adminId);
            }

            return admin;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // UPDATE
    // =========================

    public boolean updateAdminProfile(
            String adminId,
            String name,
            String email,
            String mobileNo,
            String address) {

        try {

            if (adminId == null || adminId.isBlank()) {
                return false;
            }

            Firestore db = getDb();

            Map<String, Object> updates = new HashMap<>();

            updates.put("name", name);
            updates.put("email", email);
            updates.put("mobileNo", mobileNo);
            updates.put("address", address);

            ApiFuture<WriteResult> future =
                    db.collection(COLLECTION)
                      .document(adminId)
                      .update(updates);

            future.get();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // DELETE
    // =========================

    public boolean deleteAdminProfile(String adminId) {

        try {

            if (adminId == null || adminId.isBlank()) {
                return false;
            }

            Firestore db = getDb();

            ApiFuture<WriteResult> future =
                    db.collection(COLLECTION)
                      .document(adminId)
                      .delete();

            future.get();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTwoFactorStatus(
        String adminId,
        boolean enabled) {

    try {

        if (adminId == null || adminId.isBlank()) {
            return false;
        }

        Firestore db = getDb();

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "twoFactorEnabled",
                enabled
        );

        ApiFuture<WriteResult> future =
                db.collection(COLLECTION)
                  .document(adminId)
                  .update(updates);

        future.get();

        return true;

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}


}
