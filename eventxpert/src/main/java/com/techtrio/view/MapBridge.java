package com.techtrio.view;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public class MapBridge {

    private final TextField cityField;
    private final TextField localityField;

    public MapBridge(TextField cityField, TextField localityField) {
        this.cityField = cityField;
        this.localityField = localityField;
    }

    
    public void onLocationSelected(String city, String area, double lat, double lng) {
        Platform.runLater(() -> {
            if (city != null && !city.isBlank()) {
                cityField.setText(city);
                ReviewPageC.EventData.city = city;
            }
            if (area != null && !area.isBlank()) {
                localityField.setText(area);
                ReviewPageC.EventData.area = area;
            }
            System.out.println("Map Selected -> City: " + city + ", Area: " + area + " (" + lat + ", " + lng + ")");
        });
    }
}