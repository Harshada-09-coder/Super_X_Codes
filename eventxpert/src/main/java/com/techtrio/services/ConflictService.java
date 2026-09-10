package com.techtrio.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.model.Conflict;

import java.util.ArrayList;
import java.util.List;

public class ConflictService {

    private static final String COLLECTION = "conflicts";

    private Firestore getFirestore() {
        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            return null;
        }
    }

    // Do not save any new conflicts
    public String saveConflict(Conflict conflict) {
        return conflict != null ? conflict.getConflictId() : null;
    }

    // Returns an empty list so the Admin UI table loads smoothly with 0 conflicts
    public List<Conflict> getAllConflicts() {
        return new ArrayList<>();
    }

    public Conflict getConflictById(String conflictId) {
        return null;
    }

    public boolean updateConflict(Conflict conflict) {
        return true;
    }

    public boolean updateStatus(String conflictId, String status) {
        return true;
    }

    public boolean resolveConflict(String conflictId, String resolutionNotes) {
        return true;
    }

    public boolean deleteConflict(String conflictId) {
        return true;
    }
}