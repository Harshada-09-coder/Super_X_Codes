package com.techtrio.model.budget;

public class BudgetAllocation {
    private String category;
    private double recommendedAmount;
    private double percentage;
    private String icon;

    public BudgetAllocation(String category, double recommendedAmount, double percentage, String icon) {
        this.category = category;
        this.recommendedAmount = recommendedAmount;
        this.percentage = percentage;
        this.icon = icon;
    }

    public String getCategory() { return category; }
    public double getRecommendedAmount() { return recommendedAmount; }
    public double getPercentage() { return percentage; }
    public String getIcon() { return icon; }
}