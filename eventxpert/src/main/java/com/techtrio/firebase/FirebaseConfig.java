package com.techtrio.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FirebaseConfig {

    private static Firestore firestore;

    public static synchronized void initializeFirebase() {
        // 1. PREVENT SSL HANDSHAKE TIMEOUT ON WINDOWS (FORCE IPv4)
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("io.grpc.internal.DnsNameResolverProvider.enable_grpclb", "true");

        if (!FirebaseApp.getApps().isEmpty()) {
            if (firestore == null) {
                firestore = FirestoreClient.getFirestore();
            }
            return;
        }

        try {
            InputStream serviceAccount = null;
            File keyFile = new File("serviceAccountKey.json");

            if (keyFile.exists()) {
                serviceAccount = new FileInputStream(keyFile);
            } else {
                serviceAccount = FirebaseConfig.class.getClassLoader()
                        .getResourceAsStream("serviceAccountKey.json");
            }

            if (serviceAccount == null) {
                System.err.println("⚠️ WARNING: serviceAccountKey.json not found. Attempting Default Credentials.");
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
                FirebaseApp.initializeApp(options);
            } else {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                serviceAccount.close();
            }

            firestore = FirestoreClient.getFirestore();
            System.out.println("✅ Firebase & Firestore initialized successfully with IPv4 support!");

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized Firestore getFirestore() {
        if (firestore == null) {
            initializeFirebase();
        }
        return firestore;
    }
}