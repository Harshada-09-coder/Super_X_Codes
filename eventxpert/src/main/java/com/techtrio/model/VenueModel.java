package com.techtrio.model;

import java.io.Serializable;

public class VenueModel implements Serializable {

    private String venueId;
    private String name;
    private String type;            // Hotel, Banquet Hall, Conference Center, Outdoor, Resort, etc.
    private String city;
    private String area;
    private String address;
    private int capacity;
    private double price;
    private double rating;
    private boolean parkingAvailable;
    private boolean cateringAllowed;
    private boolean decorationAllowed;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String contactNumber;

    // Default No-Arg Constructor for Firestore serialization
    public VenueModel() {
    }

    public VenueModel(String venueId, String name, String type, String city, String area, 
                      String address, int capacity, double price, double rating, 
                      boolean parkingAvailable, boolean cateringAllowed, boolean decorationAllowed, 
                      double latitude, double longitude, String imageUrl, String contactNumber) {
        this.venueId = venueId;
        this.name = name;
        this.type = type;
        this.city = city;
        this.area = area;
        this.address = address;
        this.capacity = capacity;
        this.price = price;
        this.rating = rating;
        this.parkingAvailable = parkingAvailable;
        this.cateringAllowed = cateringAllowed;
        this.decorationAllowed = decorationAllowed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.contactNumber = contactNumber;
    }

    // Getters and Setters
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isParkingAvailable() { return parkingAvailable; }
    public void setParkingAvailable(boolean parkingAvailable) { this.parkingAvailable = parkingAvailable; }

    public boolean isCateringAllowed() { return cateringAllowed; }
    public void setCateringAllowed(boolean cateringAllowed) { this.cateringAllowed = cateringAllowed; }

    public boolean isDecorationAllowed() { return decorationAllowed; }
    public void setDecorationAllowed(boolean decorationAllowed) { this.decorationAllowed = decorationAllowed; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}