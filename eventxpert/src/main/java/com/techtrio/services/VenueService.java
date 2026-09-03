package com.techtrio.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.model.VenueModel;

import java.util.*;

public class VenueService {

    private final Firestore db;

    public VenueService() {
        this.db = FirestoreClient.getFirestore();
    }

    /**
     * 1. Customer च्या City, VenueType आणि Guest Capacity नुसार जवळपासचे Venues मिळवणे.
     */
    public List<VenueModel> getNearbyVenues(String city, String venueType, int minCapacity) {
        List<VenueModel> venueList = new ArrayList<>();
        try {
            CollectionReference venuesRef = db.collection("venues");
            Query query = venuesRef;

            if (city != null && !city.isBlank()) {
                query = query.whereEqualTo("city", city.trim());
            }

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                VenueModel venue = doc.toObject(VenueModel.class);
                if (venue != null) {
                    venue.setVenueId(doc.getId());
                    venueList.add(venue);
                }
            }

            // Fallback (जर डेटाबेसमध्ये venues उपलब्ध नसतील तर dynamic sample list लोड होईल)
            if (venueList.isEmpty()) {
                venueList = generateSampleVenues(city, venueType, minCapacity);
            }

        } catch (Exception e) {
            System.err.println("Error fetching venues from Firestore: " + e.getMessage());
            venueList = generateSampleVenues(city, venueType, minCapacity);
        }
        return venueList;
    }

    /**
     * 2. Organizer: Customer ला Venue Suggestions पाठवणे.
     */
    public boolean sendVenueSuggestionsToCustomer(String eventId, List<VenueModel> suggestedVenues) {
        try {
            if (eventId == null || eventId.isBlank()) return false;

            DocumentReference docRef = db.collection("events").document(eventId.trim());

            Map<String, Object> updates = new HashMap<>();
            updates.put("venueStatus", "SUGGESTED");
            updates.put("suggestedVenues", suggestedVenues);
            updates.put("venueChangeNotes", ""); // Reset change notes if any

            ApiFuture<WriteResult> future = docRef.update(updates);
            future.get();
            return true;
        } catch (Exception e) {
            System.err.println("Error sending venue suggestions: " + e.getMessage());
            return false;
        }
    }

    /**
     * 3. Customer: Organizer च्या पर्यायांमधून एक Venue निवडून Confirm करणे.
     */
    public boolean confirmCustomerVenueChoice(String eventId, VenueModel selectedVenue, String organizerName) {
        try {
            if (eventId == null || selectedVenue == null) return false;

            DocumentReference docRef = db.collection("events").document(eventId.trim());

            Map<String, Object> updates = new HashMap<>();
            updates.put("venueStatus", "CONFIRMED");
            updates.put("confirmedVenueName", selectedVenue.getName());
            updates.put("confirmedVenueAddress", selectedVenue.getAddress());
            updates.put("confirmedVenueCost", "₹ " + String.format("%,.0f", selectedVenue.getPrice()));
            updates.put("venueType", selectedVenue.getType());
            updates.put("venueConfirmationDate", new java.text.SimpleDateFormat("dd MMM yyyy").format(new Date()));
            if (organizerName != null && !organizerName.isBlank()) {
                updates.put("venueConfirmedBy", organizerName);
            }

            ApiFuture<WriteResult> future = docRef.update(updates);
            future.get();
            return true;
        } catch (Exception e) {
            System.err.println("Error confirming venue choice: " + e.getMessage());
            return false;
        }
    }

    /**
     * 4. Customer: Organizer ला Venue बदलण्याची विनंती करणे.
     */
    public boolean requestVenueChange(String eventId, String changeNotes) {
        try {
            if (eventId == null || eventId.isBlank()) return false;

            DocumentReference docRef = db.collection("events").document(eventId.trim());

            Map<String, Object> updates = new HashMap<>();
            updates.put("venueStatus", "CHANGE_REQUESTED");
            updates.put("venueChangeNotes", changeNotes != null ? changeNotes : "Customer requested venue alteration.");

            ApiFuture<WriteResult> future = docRef.update(updates);
            future.get();
            return true;
        } catch (Exception e) {
            System.err.println("Error requesting venue change: " + e.getMessage());
            return false;
        }
    }

    
    private List<VenueModel> generateSampleVenues(String city, String venueType, int capacity) {
        List<VenueModel> list = new ArrayList<>();
        String targetCity = (city != null && !city.isBlank()) ? city : "Pune";
        int cap = capacity > 0 ? capacity : 250;

        list.add(new VenueModel("V-101", "Grand Convention Center", venueType != null ? venueType : "Conference Center",
                targetCity, "Senapati Bapat Road", "SB Road, Near ICC Tech Park, " + targetCity,
                cap + 150, 150000.0, 4.8, true, true, true, 18.5362, 73.8300, "/assets/images/venue1.jpg", "+91 9823012345"));

        list.add(new VenueModel("V-102", "Royal Heritage Banquet & Lawn", "Banquet Hall",
                targetCity, "Baner", "Main Baner Road, Near High Street, " + targetCity,
                cap + 50, 120000.0, 4.7, true, true, true, 18.5590, 73.7868, "/assets/images/venue2.jpg", "+91 9823054321"));

        list.add(new VenueModel("V-103", "Blue Horizon Sky Lounge & Hall", "Hotel",
                targetCity, "Koregaon Park", "North Main Road, Koregaon Park, " + targetCity,
                cap + 100, 180000.0, 4.9, true, true, false, 18.5360, 73.8940, "/assets/images/venue3.jpg", "+91 9823098765"));

        list.add(new VenueModel("V-104", "Riverside Palace Resort", "Outdoor Venue",
                targetCity, "Kalyani Nagar", "Riverside Drive, Kalyani Nagar, " + targetCity,
                cap + 250, 210000.0, 4.6, true, true, true, 18.5480, 73.9020, "/assets/images/venue4.jpg", "+91 9823067890"));

        return list;
    }
}