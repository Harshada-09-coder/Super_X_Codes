package com.techtrio.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class AuthController {
    private String API_KEY = "AIzaSyCZ1GJ87_JvVahhYvWU45A0L1V087QRALM";
    
     public SignUpResult signUp(String name, String email, String mobile, String password, String role){

        JSONObject payload = new JSONObject()
         //.put("name",name)
         .put("email", email)
         //.put("mobileNo" , mobile)
         .put("password", password)
         .put("returnSecureToken", true);


        try {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(
                "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                        + API_KEY
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(payload.toString())
                )
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(response.statusCode());
        System.out.println(response.body());

        if (response.statusCode() == 200) {

            JSONObject result =
                    new JSONObject(response.body());
            String uid =
                    result.getString("localId");

            String idToken =
                    result.getString("idToken");

            // Ata Firestore madhye data save karu
            boolean firestoreSaved =
                    saveUserToFirestore(
                            uid,
                            idToken,
                            name,
                            email,
                            mobile,
                            role
                    );

            if (firestoreSaved) {
                return new SignUpResult(idToken, idToken);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
    }
  
    private boolean saveUserToFirestore(
        String uid,
        String idToken,
        String name,
        String email,
        String mobile, 
        String role) {

    try {

        JSONObject fields = new JSONObject();

        fields.put(
                "name",
                new JSONObject()
                        .put("stringValue", name)
        );

        fields.put(
                "email",
                new JSONObject()
                        .put("stringValue", email)
        );

        fields.put(
                "mobileNo",
                new JSONObject()
                        .put("stringValue", mobile)
        );

        fields.put(
                "role",
                new JSONObject()
                        .put("stringValue", role)
        );

        JSONObject document =
                new JSONObject()
                        .put("fields", fields);

        String projectId = "eventxpert-6f453";
        role = role.trim().toLowerCase();
         String collection;

if (role.equalsIgnoreCase("customer")) {
    collection = "Customers";
} else if (role.equalsIgnoreCase("organizer")) {
    collection = "Organizers";
} else if (role.equalsIgnoreCase("vendor")) {
    collection = "Vendors";
} else {
    System.out.println("Invalid role: " + role);
    return false;
}

        URI uri = URI.create(
                "https://firestore.googleapis.com/v1/projects/"
                        + projectId
                        + "/databases/(default)/documents/"
                        + collection + "/"
                        + uid
        );

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(uri)
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .header(
                                "Authorization",
                                "Bearer " + idToken
                        )
                        .method(
                                "PATCH",
                                HttpRequest.BodyPublishers
                                        .ofString(document.toString())
                        )
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(
                "Firestore status: "
                        + response.statusCode()
        );

        System.out.println(
                "Firestore response: "
                        + response.body()
        );

        return response.statusCode() == 200;

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}
    public SignInResult signIn(String email, String password){

       JSONObject payload = new JSONObject()
            .put("email", email)
            .put("password", password)
            .put("returnSecureToken", true);

    try {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                        + API_KEY
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(payload.toString())
                )
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(response.statusCode());
        System.out.println(response.body());

        if (response.statusCode() == 200) {

            JSONObject result =
                    new JSONObject(response.body());

            String uid =
                    result.getString("localId");

            String idToken =
                    result.getString("idToken");

            return new SignInResult(uid, idToken);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
 }

 public String getUserRole(String uid, String idToken) {

    try {

        String projectId = "eventxpert-6f453";

        String[] collections = {
                "Admin",
                "Customers",
                "Organizers",
                "Vendors"
        };

        HttpClient client = HttpClient.newHttpClient();

        for (String collection : collections) {

            URI uri = URI.create(
                    "https://firestore.googleapis.com/v1/projects/"
                            + projectId
                            + "/databases/(default)/documents/"
                            + collection
                            + "/"
                            + uid
            );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(uri)
                            .header(
                                    "Authorization",
                                    "Bearer " + idToken
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    collection + " status: "
                            + response.statusCode()
            );

            if (response.statusCode() == 200) {

                JSONObject document =
                        new JSONObject(response.body());

                JSONObject fields =
                        document.getJSONObject("fields");

                String role =
                        fields
                                .getJSONObject("role")
                                .getString("stringValue");

                System.out.println(
                        "ROLE FROM FIRESTORE: "
                                + role
                );

                return role;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}
}
