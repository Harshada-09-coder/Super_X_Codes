package com.techtrio.session;

public class UserSession {

    private static String uid;
    private static String idToken;
    private static String userEmail;
    private static String userRole;
    private static String userName; // Added for User Profile Name

    public static synchronized void setSession(String u, String token, String email, String role) {
        setSession(u, token, email, role, null);
    }

    public static synchronized void setSession(String u, String token, String email, String role, String name) {
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
        if (name != null && !name.isBlank()) {
            userName = name.trim();
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

    // 🎯 Razorpay आणि OTP साठी जोडलेली सुरक्षित मेथड:
    public static synchronized String getEmail() {
        return (userEmail != null && !userEmail.isBlank()) ? userEmail : "customer@eventxpert.com";
    }

    // 🎯 स्वतंत्र ईमेल सेट करण्यासाठी:
    public static synchronized void setEmail(String email) {
        if (email != null && !email.isBlank()) {
            userEmail = email.trim();
        }
    }

    public static synchronized String getUserRole() {
        return userRole;
    }

    // Guest.java मधील एरर फिक्स करणारी मेथड
    public static synchronized String getUserName() {
        return (userName != null && !userName.isBlank()) ? userName : "Customer";
    }

    public static synchronized void setUserName(String name) {
        if (name != null && !name.isBlank()) {
            userName = name.trim();
        }
    }

    public static synchronized boolean isAuthenticated() {
        return uid != null && !uid.isBlank();
    }

    public static synchronized void clearSession() {
        uid = null;
        idToken = null;
        userEmail = null;
        userRole = null;
        userName = null;
    }
}