package com.techtrio.controller;

public class SignInResult {
    private String uid;
    private String idToken;

    public SignInResult(String uid, String idToken) {
        this.uid = uid;
        this.idToken = idToken;
    }

    public String getUid() {
        return uid;
    }

    public String getIdToken() {
        return idToken;
    }
}
