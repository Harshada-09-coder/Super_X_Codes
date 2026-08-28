package com.techtrio;

import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.view.LandingPage;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) throws Exception {

        FirebaseConfig.initializeFirebase();

        Application.launch(LandingPage.class, args);
    }
} 
