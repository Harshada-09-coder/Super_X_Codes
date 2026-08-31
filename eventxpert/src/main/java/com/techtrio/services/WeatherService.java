package com.techtrio.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.techtrio.model.WeatherData;

public class WeatherService {

    // तुमची OpenWeatherMap API Key इथे टाका
    private static final String API_KEY = "fec8ea5411f7ff16b007fde20f9ab42b";

    /**
     * विशिष्ट इव्हेंटच्या तारखेसाठी हवामानाचा अंदाज मिळवणे
     */
    public WeatherData getWeatherForEventDate(String city, String eventDate) {
        if (city == null || city.isBlank()) {
            city = "Pune";
        }

        // जर तारीख दिली नसेल तर थेट आजचे हवामान कॉल करणे
        if (eventDate == null || eventDate.isBlank()) {
            return getWeatherAlert(city);
        }

        try {
            String encodedCity = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
            // OpenWeatherMap 5-Day Forecast Endpoint
            String urlString = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&units=metric&appid=" + API_KEY;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray forecastList = jsonResponse.getJSONArray("list");

                JSONObject matchedForecast = null;

                // Event Date शी (उदा. 2026-09-05 किंवा 05-09-2026) मॅच शोधणे
                String cleanTargetDate = eventDate.trim().replace("/", "-");

                for (int i = 0; i < forecastList.length(); i++) {
                    JSONObject item = forecastList.getJSONObject(i);
                    String dtTxt = item.getString("dt_txt"); // Format: "2026-09-05 12:00:00"

                    if (dtTxt.contains(cleanTargetDate) || isMatchingDate(dtTxt, cleanTargetDate)) {
                        matchedForecast = item;
                        // दुपारच्या वेळेचा डेटा प्राधान्याने घेणे
                        if (dtTxt.contains("12:00:00") || dtTxt.contains("15:00:00")) {
                            break;
                        }
                    }
                }

                WeatherData weatherData = new WeatherData();

                if (matchedForecast != null) {
                    double temp = matchedForecast.getJSONObject("main").getDouble("temp");
                    String desc = matchedForecast.getJSONArray("weather").getJSONObject(0).getString("description");

                    weatherData.setTemperature((int) Math.round(temp));
                    weatherData.setAlertMessage("Forecast: " + capitalize(desc));

                    if (desc.toLowerCase().contains("rain") || desc.toLowerCase().contains("thunderstorm") || desc.toLowerCase().contains("storm")) {
                        weatherData.setAlertType("WARNING");
                    } else {
                        weatherData.setAlertType("NORMAL");
                    }
                } else {
                    // जर तारीख ५ दिवसांपेक्षा लांब असेल (Forecast लिमिट बाहेर)
                    weatherData.setTemperature(0);
                    weatherData.setAlertMessage("Forecast available closer to event date.");
                    weatherData.setAlertType("NORMAL");
                }

                return weatherData;
            }
        } catch (Exception e) {
            // एरर आल्यास सुरक्षितपणे फॉलबॅक
            System.err.println("Weather Fetch Error: " + e.getMessage());
        }

        return getWeatherAlert(city);
    }

    /**
     * डीफॉल्ट / करंट हवामान
     */
    public WeatherData getWeatherAlert(String city) {
        if (city == null || city.isBlank()) {
            city = "Pune";
        }
        try {
            String encodedCity = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&units=metric&appid=" + API_KEY;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                double temp = json.getJSONObject("main").getDouble("temp");
                String desc = json.getJSONArray("weather").getJSONObject(0).getString("description");

                WeatherData weatherData = new WeatherData();
                weatherData.setTemperature((int) Math.round(temp));
                weatherData.setAlertMessage(capitalize(desc));

                if (desc.toLowerCase().contains("rain") || desc.toLowerCase().contains("thunderstorm")) {
                    weatherData.setAlertType("WARNING");
                } else {
                    weatherData.setAlertType("NORMAL");
                }
                return weatherData;
            }
        } catch (Exception e) {
            // Fallback mock
        }

        // Mock Fallback डेटा जेणेकरून UI कधीही क्रॅश होणार नाही
        WeatherData fallback = new WeatherData();
        fallback.setTemperature(27);
        fallback.setAlertMessage("Clear Sky (Estimated)");
        fallback.setAlertType("NORMAL");
        return fallback;
    }

    private boolean isMatchingDate(String dtTxt, String targetDate) {
        try {
            return dtTxt.startsWith(targetDate);
        } catch (Exception e) {
            return false;
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}