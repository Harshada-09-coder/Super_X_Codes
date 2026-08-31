package com.techtrio.model;

public class LocationData {

    private String city;
    private String area;
    private String venue;
    private double latitude;
    private double longitude;

    // Empty constructor
    public LocationData() {
    }

    // Constructor
    public LocationData(String city, String area, String venue,
                        double latitude, double longitude) {

        this.city = city;
        this.area = area;
        this.venue = venue;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
