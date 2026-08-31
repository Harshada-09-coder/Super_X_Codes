package com.techtrio.services;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.techtrio.model.LocationData;

public class MapService {

    // ==========================================
    // 1. GET CURRENT LOCATION (IP BASED - FAST & RELIABLE)
    // ==========================================
    public LocationData getCurrentLocation() {
        try {
            // Free & accurate IP Geolocation (No WebView needed)
            String url = "http://ip-api.com/json/?fields=status,message,country,regionName,city,district,lat,lon,query";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());

                if ("success".equalsIgnoreCase(json.optString("status"))) {
                    String city = json.optString("city", "");
                    String district = json.optString("district", "");
                    String region = json.optString("regionName", "");
                    double lat = json.optDouble("lat", 0.0);
                    double lon = json.optDouble("lon", 0.0);

                    // If district is empty, use region/state
                    String area = !district.isBlank() ? district : region;

                    LocationData loc = new LocationData();
                    loc.setCity(city);
                    loc.setArea(area);
                    loc.setLatitude(lat);
                    loc.setLongitude(lon);

                    System.out.println("Auto-detected City: " + city + ", Area: " + area);
                    return loc;
                }
            }
        } catch (Exception e) {
            System.err.println("Error detecting current location: " + e.getMessage());
        }

        return null;
    }

    // =========================
    // 2. GET LOCATION COORDINATES BY SEARCH
    // =========================
    public LocationData getLocationCoordinates(String city, String area, String venue) {
        try {
            String location = "";
            if (area != null && !area.isBlank()) {
                location += area.trim() + ", ";
            }
            if (city != null && !city.isBlank()) {
                location += city.trim();
            }
            if (location.endsWith(", ")) {
                location = location.substring(0, location.length() - 2);
            }

            if (location.isBlank()) return null;

            String apiKey = System.getenv("GEOAPIFY_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                System.out.println("Geoapify API key not found.");
                return null;
            }

            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = "https://api.geoapify.com/v1/geocode/search?text=" + encodedLocation + "&apiKey=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return null;

            JSONObject json = new JSONObject(response.body());
            JSONArray features = json.getJSONArray("features");
            if (features.isEmpty()) return null;

            JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            double longitude = coordinates.getDouble(0);
            double latitude = coordinates.getDouble(1);

            LocationData locationData = new LocationData();
            locationData.setCity(city);
            locationData.setArea(area);
            locationData.setVenue(venue);
            locationData.setLatitude(latitude);
            locationData.setLongitude(longitude);

            return locationData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}