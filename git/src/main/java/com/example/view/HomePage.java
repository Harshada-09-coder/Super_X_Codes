package com.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomePage extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Button btn = new Button("Next");


        Scene scene = new Scene(btn,400,500);


        stage.setScene(scene);
        stage.setTitle("Git Page");
        stage.show();

    }
    
}
