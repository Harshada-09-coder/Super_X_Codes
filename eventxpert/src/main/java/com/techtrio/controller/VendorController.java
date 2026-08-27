package com.techtrio.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.json.JSONObject;

import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;

public class VendorController {

    private final String PROJECT_ID = "eventxpert-6f453";
    public static final String COLLECTION_NAME = "Vendors";
    private final HttpClient client = HttpClient.newHttpClient();
    private final VendorProfileService vendorProfileService = new VendorProfileService();

    // =========================================================
    // GET VENDOR STATE FROM FIRESTORE
    // =========================================================
    public VendorState getVendorState(String uid, String idToken) {
        if (uid == null || uid.isBlank()) {
            return null;
        }

        try {
            URI uri = URI.create(
                    "https://firestore.googleapis.com/v1/projects/"
                            + PROJECT_ID
                            + "/databases/(default)/documents/" + COLLECTION_NAME + "/"
                            + uid.trim()
            );

            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri).GET();
            if (idToken != null && !idToken.isBlank()) {
                builder.header("Authorization", "Bearer " + idToken);
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject document = new JSONObject(response.body());
                if (!document.has("fields")) {
                    return new VendorState(false, false, "NEW", "NONE");
                }
                JSONObject fields = document.getJSONObject("fields");

                boolean profileCompleted = fields.has("profileCompleted")
                        && fields.getJSONObject("profileCompleted").optBoolean("booleanValue", false);

                boolean hasProvidedService = fields.has("hasProvidedService")
                        && fields.getJSONObject("hasProvidedService").optBoolean("booleanValue", false);

                String onboardingStatus = fields.has("onboardingStatus")
                        && fields.getJSONObject("onboardingStatus").has("stringValue")
                        ? fields.getJSONObject("onboardingStatus").getString("stringValue") : "NEW";

                String currentProcess = fields.has("currentProcess")
                        && fields.getJSONObject("currentProcess").has("stringValue")
                        ? fields.getJSONObject("currentProcess").getString("stringValue") : "NONE";

                return new VendorState(
                        profileCompleted,
                        hasProvidedService,
                        onboardingStatus,
                        currentProcess
                );
            } else {
                System.out.println("Vendor document status: " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean markServiceProvided(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return false;

        try {
            String documentPath =
                    "https://firestore.googleapis.com/v1/projects/"
                            + PROJECT_ID
                            + "/databases/(default)/documents/"
                            + COLLECTION_NAME
                            + "/"
                            + uid.trim()
                            + "?updateMask.fieldPaths=hasProvidedService&updateMask.fieldPaths=onboardingStatus";

            JSONObject fields = new JSONObject();
            fields.put("hasProvidedService", new JSONObject().put("booleanValue", true));
            fields.put("onboardingStatus", new JSONObject().put("stringValue", "ACTIVE"));

            JSONObject document = new JSONObject().put("fields", fields);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(documentPath))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()));

            if (idToken != null && !idToken.isBlank()) {
                builder.header("Authorization", "Bearer " + idToken);
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completeVendorProfile(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return false;

        try {
            String documentPath =
                    "https://firestore.googleapis.com/v1/projects/"
                            + PROJECT_ID
                            + "/databases/(default)/documents/" + COLLECTION_NAME + "/"
                            + uid.trim()
                            + "?updateMask.fieldPaths=profileCompleted&updateMask.fieldPaths=onboardingStatus&updateMask.fieldPaths=currentProcess";

            JSONObject fields = new JSONObject();
            fields.put("profileCompleted", new JSONObject().put("booleanValue", true));
            fields.put("onboardingStatus", new JSONObject().put("stringValue", "ACTIVE"));
            fields.put("currentProcess", new JSONObject().put("stringValue", "NONE"));

            JSONObject document = new JSONObject().put("fields", fields);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(documentPath))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(document.toString()));

            if (idToken != null && !idToken.isBlank()) {
                builder.header("Authorization", "Bearer " + idToken);
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createVendorProfile(VendorProfile profile, String idToken) {
        if (profile == null) return false;
        return vendorProfileService.saveVendorProfile(profile);
    }

    public VendorProfile getVendorProfile(String uid, String idToken) {
        if (uid == null || uid.isBlank()) return null;
        return vendorProfileService.getVendorProfile(uid);
    }

    public boolean updateVendorProfileFields(String uid, Map<String, Object> updates) {
        if (uid == null || uid.isBlank()) return false;
        return vendorProfileService.updateVendorProfileFields(uid, updates);
    }
}