package com.techtrio.model.budget;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class BudgetOptimizationResult {
    private List<BudgetAllocation> allocations;
    private double totalAllocated;
    private String status;

    public BudgetOptimizationResult(List<BudgetAllocation> allocations, double totalAllocated) {
        this.allocations = allocations;
        this.totalAllocated = totalAllocated;
        this.status = "RECOMMENDED";
    }

    public List<BudgetAllocation> getAllocations() { return allocations; }
    public double getTotalAllocated() { return totalAllocated; }
    public String getStatus() { return status; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (BudgetAllocation a : allocations) {
            map.put(a.getCategory(), a.getRecommendedAmount());
        }
        return map;
    }
}