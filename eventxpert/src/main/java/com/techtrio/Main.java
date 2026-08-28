package com.techtrio;

import com.techtrio.firebase.FirebaseConfig;
import com.techtrio.view.LandingPage;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) throws Exception {

        FirebaseConfig.initializeFirebase();

        Application.launch(LandingPage.class, args);
    }
<<<<<<< HEAD
} 
=======
}
>>>>>>> dcc37cf3b610c7e18062f9cf1a58de699f2b39c7
