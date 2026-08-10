package com.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Homepage extends Application {

    @Override
    public void start(Stage stage) throws Exception{


        Text tx = new Text("Git Demo");
        tx.setStyle("-fx-fill:White;-fx-font-size:18");

        VBox root = new VBox(10,tx);
        root.setStyle("-fx-alignment:center");
        
        Scene sc = new Scene(root,500,400);
        sc.setFill(Color.BLACK);
        stage.setScene(sc);
        stage.setTitle("Git Demo");
        stage.show();
    }
    
}
