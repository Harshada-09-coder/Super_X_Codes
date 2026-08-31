package com.techtrio.model;

public class WeatherData {

    private String condition;      // उदा. Sunny, Rain, Cloudy
    private double temperature;    // उदा. 28.5 °C
    private int precipitationChance; // पावसाची शक्यता उदा. 75%
    private String alertMessage;   // इव्हेंटसाठीचा सल्ला / वॉर्निंग
    private String alertType;      // SUCCESS, WARNING, DANGER

    public WeatherData() {}

    public WeatherData(String condition, double temperature, int precipitationChance, String alertMessage, String alertType) {
        this.condition = condition;
        this.temperature = temperature;
        this.precipitationChance = precipitationChance;
        this.alertMessage = alertMessage;
        this.alertType = alertType;
    }

    // Getters and Setters
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getPrecipitationChance() { return precipitationChance; }
    public void setPrecipitationChance(int precipitationChance) { this.precipitationChance = precipitationChance; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
}
