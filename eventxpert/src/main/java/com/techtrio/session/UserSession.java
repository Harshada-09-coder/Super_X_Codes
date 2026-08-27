package com.techtrio.session;

public class UserSession {

    private static String uid;
    private static String idToken;
    private static String userEmail;
    private static String userRole;

    public static synchronized void setSession(String u, String token, String email, String role) {
        if (u != null && !u.isBlank()) {
            uid = u.trim();
        }
        if (token != null && !token.isBlank()) {
            idToken = token.trim();
        }
        if (email != null && !email.isBlank()) {
            userEmail = email.trim();
        }
        if (role != null && !role.isBlank()) {
            userRole = role.trim();
        }
    }

    public static synchronized String getUid() {
        return uid;
    }

    public static synchronized String getIdToken() {
        return idToken;
    }

    public static synchronized String getUserEmail() {
        return userEmail;
    }

    public static synchronized String getUserRole() {
        return userRole;
    }

    public static synchronized boolean isAuthenticated() {
        return uid != null && !uid.isBlank();
    }

    public static synchronized void clearSession() {
        uid = null;
        idToken = null;
        userEmail = null;
        userRole = null;
    }
}