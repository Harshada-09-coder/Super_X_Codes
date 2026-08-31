package com.techtrio.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.controller.AdminController;
import com.techtrio.model.Conflict;
import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConflictService {

    private static final String COLLECTION =
            "conflicts";

           




    // ==========================================
    // FIRESTORE
    // ==========================================

    private Firestore getFirestore() {

        return FirestoreClient.getFirestore();
    }


    // ==========================================
    // SAVE CONFLICT
    // ==========================================

    public String saveConflict(
            Conflict conflict) {

        try {

            if (conflict == null) {
                return null;
            }


            CollectionReference collection =
                    getFirestore()
                            .collection(COLLECTION);


            String conflictId =
                    conflict.getConflictId();


            if (conflictId == null
                    || conflictId.isBlank()) {

                conflictId =
                        collection.document().getId();

                conflict.setConflictId(
                        conflictId
                );
            }


            ApiFuture<WriteResult> future =
                    collection
                            .document(conflictId)
                            .set(conflict);


            future.get();


            System.out.println(
                    "Conflict saved successfully: "
                    + conflictId
            );


            return conflictId;

        } catch (Exception e) {

            System.err.println(
                    "Error saving conflict: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return null;
        }
    }


    // ==========================================
    // GET ALL CONFLICTS
    // ==========================================

    public List<Conflict> getAllConflicts() {

        List<Conflict> conflicts =
                new ArrayList<>();

        try {

            ApiFuture<QuerySnapshot> future =
                    getFirestore()
                            .collection(COLLECTION)
                            .get();


            QuerySnapshot snapshot =
                    future.get();


            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Conflict conflict =
                        document.toObject(
                                Conflict.class
                        );


                if (conflict != null) {

                    if (conflict.getConflictId()
                            == null) {

                        conflict.setConflictId(
                                document.getId()
                        );
                    }

                    conflicts.add(conflict);
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Error loading conflicts: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }


        return conflicts;
    }


    // ==========================================
    // GET CONFLICT BY ID
    // ==========================================

    public Conflict getConflictById(
            String conflictId) {

        try {

            if (conflictId == null
                    || conflictId.isBlank()) {

                return null;
            }


            DocumentSnapshot document =
                    getFirestore()
                            .collection(COLLECTION)
                            .document(conflictId)
                            .get()
                            .get();


            if (!document.exists()) {
                return null;
            }


            Conflict conflict =
                    document.toObject(
                            Conflict.class
                    );


            if (conflict != null
                    && conflict.getConflictId()
                        == null) {

                conflict.setConflictId(
                        document.getId()
                );
            }


            return conflict;

        } catch (Exception e) {

            System.err.println(
                    "Error loading conflict: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return null;
        }
    }


    // ==========================================
    // UPDATE CONFLICT
    // ==========================================

    public boolean updateConflict(
            Conflict conflict) {

        try {

            if (conflict == null
                    || conflict.getConflictId()
                        == null
                    || conflict.getConflictId()
                        .isBlank()) {

                return false;
            }


            getFirestore()
                    .collection(COLLECTION)
                    .document(
                            conflict.getConflictId()
                    )
                    .set(conflict)
                    .get();


            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error updating conflict: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // UPDATE STATUS
    // ==========================================

    public boolean updateStatus(
            String conflictId,
            String status) {

        try {

            if (conflictId == null
                    || conflictId.isBlank()) {

                return false;
            }


            getFirestore()
                    .collection(COLLECTION)
                    .document(conflictId)
                    .update(
                            "status",
                            status
                    )
                    .get();


            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error updating conflict status: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    public boolean resolveConflict(
        String conflictId,
        String resolutionNotes) {

    try {

        if (conflictId == null
                || conflictId.isBlank()) {

            return false;
        }

        if (resolutionNotes == null) {
            resolutionNotes = "";
        }

        getFirestore()
                .collection(COLLECTION)
                .document(conflictId)
                .update(
                        "status",
                        "Resolved",
                        "resolvedAt",
                        com.google.cloud.Timestamp.now(),
                        "resolutionNotes",
                        resolutionNotes
                )
                .get();

        return true;

    } catch (Exception e) {

        System.err.println(
                "Error resolving conflict: "
                + e.getMessage()
        );

        e.printStackTrace();

        return false;
    }
}


    // ==========================================
    // DELETE CONFLICT
    // ==========================================

    public boolean deleteConflict(
            String conflictId) {

        try {

            if (conflictId == null
                    || conflictId.isBlank()) {

                return false;
            }


            getFirestore()
                    .collection(COLLECTION)
                    .document(conflictId)
                    .delete()
                    .get();


            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error deleting conflict: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }
}
