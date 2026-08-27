package com.techtrio.controller;

public class SignUpResult {

    public String uid;
    public String idToken;

    public SignUpResult(String uid, String idToken) {
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
