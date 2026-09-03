package com.techtrio.model.budget;

import java.util.List;

public class BudgetOptimizationRequest {
    private double totalBudget;
    private String eventType;
    private int guestCount;
    private List<String> selectedRequirements;
    private String city;

    public BudgetOptimizationRequest(double totalBudget, String eventType, int guestCount, List<String> selectedRequirements, String city) {
        this.totalBudget = totalBudget;
        this.eventType = eventType != null ? eventType : "General";
        this.guestCount = guestCount > 0 ? guestCount : 100;
        this.selectedRequirements = selectedRequirements != null ? selectedRequirements : List.of();
        this.city = city != null ? city : "Pune";
    }

    public double getTotalBudget() { return totalBudget; }
    public String getEventType() { return eventType; }
    public int getGuestCount() { return guestCount; }
    public List<String> getSelectedRequirements() { return selectedRequirements; }
    public String getCity() { return city; }
}