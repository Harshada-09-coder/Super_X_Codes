package com.techtrio.services;

import com.techtrio.model.budget.BudgetAllocation;
import com.techtrio.model.budget.BudgetOptimizationRequest;
import com.techtrio.model.budget.BudgetOptimizationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BudgetOptimizerService {

    public BudgetOptimizationResult optimizeBudget(BudgetOptimizationRequest request) {
        double total = request.getTotalBudget();
        if (total <= 0) {
            total = 500000.0;
        }

        List<String> reqs = request.getSelectedRequirements();
        int guests = request.getGuestCount();
        String eventType = request.getEventType();

        // 1. Base Weights Mapping
        Map<String, Double> weights = new LinkedHashMap<>();
        Map<String, String> iconMap = new HashMap<>();

        // Icon Definitions
        iconMap.put("Venue", "fas-hotel");
        iconMap.put("Catering", "fas-concierge-bell");
        iconMap.put("Decoration", "fas-archway");
        iconMap.put("Photography", "fas-camera");
        iconMap.put("Sound System", "fas-volume-up");
        iconMap.put("Anchor", "fas-microphone");
        iconMap.put("Rental Equipment", "fas-toolbox");
        iconMap.put("Invitation & Printing", "fas-envelope-open-text");
        iconMap.put("Cake & Dessert Services", "fas-birthday-cake");
        iconMap.put("Contingency & Other", "fas-shield-alt");

        // Base Preference Weights
        Map<String, Double> baseWeights = new LinkedHashMap<>();
        baseWeights.put("Venue", 28.0);
        baseWeights.put("Catering", 25.0);
        baseWeights.put("Decoration", 18.0);
        baseWeights.put("Photography", 10.0);
        baseWeights.put("Sound System", 6.0);
        baseWeights.put("Anchor", 4.0);
        baseWeights.put("Rental Equipment", 5.0);
        baseWeights.put("Invitation & Printing", 3.0);
        baseWeights.put("Cake & Dessert Services", 4.0);

        // Filter only Selected Requirements
        if (reqs != null) {
            for (String req : reqs) {
                String trimmed = req.trim();
                if (baseWeights.containsKey(trimmed)) {
                    weights.put(trimmed, baseWeights.get(trimmed));
                }
            }
        }

        // Always ensure Venue
        if (!weights.containsKey("Venue")) {
            weights.put("Venue", 25.0);
        }

        // 2. Dynamic Adjustments
        if (weights.containsKey("Catering") && guests > 300) {
            weights.put("Catering", weights.get("Catering") + 6.0);
        }

        if ("Wedding".equalsIgnoreCase(eventType) && weights.containsKey("Decoration")) {
            weights.put("Decoration", weights.get("Decoration") + 5.0);
        } else if ("Conference".equalsIgnoreCase(eventType) && weights.containsKey("Sound System")) {
            weights.put("Sound System", weights.get("Sound System") + 4.0);
        }

        weights.put("Contingency & Other", 8.0);

        // 3. Normalization (Total sum = 100%)
        double sumWeights = 0.0;
        for (Double val : weights.values()) {
            sumWeights += val;
        }

        List<BudgetAllocation> allocations = new ArrayList<>();
        double runningSum = 0.0;

        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            double normalizedPercentage = (entry.getValue() / sumWeights) * 100.0;
            double amount = Math.round((total * (normalizedPercentage / 100.0)) / 100.0) * 100.0;
            runningSum += amount;

            allocations.add(new BudgetAllocation(
                    entry.getKey(),
                    amount,
                    Math.round(normalizedPercentage * 10.0) / 10.0,
                    iconMap.getOrDefault(entry.getKey(), "fas-check-circle")
            ));
        }

        return new BudgetOptimizationResult(allocations, runningSum);
    }
}