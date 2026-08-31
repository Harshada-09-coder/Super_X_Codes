package com.techtrio.model;

import java.util.ArrayList;
import java.util.List;

public class AiEventPlan {

    private String summary = "";
    private String theme = "Classic Elegance";
    private String duration = "1 Day";
    private int estimatedTasks = 25;
    private int recommendedVendorsCount = 12;

    private List<ScheduleItem> timeline = new ArrayList<>();
    private List<String> taskChecklist = new ArrayList<>();
    private List<VendorRecommendation> vendorRecommendations = new ArrayList<>();
    private List<BudgetItem> budgetBreakdown = new ArrayList<>();
    private List<String> importantNotes = new ArrayList<>();

    public static class ScheduleItem {
        private String time;
        private String activity;

        public ScheduleItem(String time, String activity) {
            this.time = time;
            this.activity = activity;
        }

        public String getTime() { return time; }
        public String getActivity() { return activity; }
    }

    public static class VendorRecommendation {
        private String category;
        private String name;
        private double rating;

        public VendorRecommendation(String category, String name, double rating) {
            this.category = category;
            this.name = name;
            this.rating = rating;
        }

        public String getCategory() { return category; }
        public String getName() { return name; }
        public double getRating() { return rating; }
    }

    public static class BudgetItem {
        private String category;
        private String amount;
        private double percentage;

        public BudgetItem(String category, String amount, double percentage) {
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
        }

        public String getCategory() { return category; }
        public String getAmount() { return amount; }
        public double getPercentage() { return percentage; }
    }

    // Getters and Setters
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getEstimatedTasks() { return estimatedTasks; }
    public void setEstimatedTasks(int estimatedTasks) { this.estimatedTasks = estimatedTasks; }

    public int getRecommendedVendorsCount() { return recommendedVendorsCount; }
    public void setRecommendedVendorsCount(int recommendedVendorsCount) { this.recommendedVendorsCount = recommendedVendorsCount; }

    public List<ScheduleItem> getTimeline() { return timeline; }
    public void setTimeline(List<ScheduleItem> timeline) { this.timeline = timeline; }

    public List<String> getTaskChecklist() { return taskChecklist; }
    public void setTaskChecklist(List<String> taskChecklist) { this.taskChecklist = taskChecklist; }

    public List<VendorRecommendation> getVendorRecommendations() { return vendorRecommendations; }
    public void setVendorRecommendations(List<VendorRecommendation> vendorRecommendations) { this.vendorRecommendations = vendorRecommendations; }

    public List<BudgetItem> getBudgetBreakdown() { return budgetBreakdown; }
    public void setBudgetBreakdown(List<BudgetItem> budgetBreakdown) { this.budgetBreakdown = budgetBreakdown; }

    public List<String> getImportantNotes() { return importantNotes; }
    public void setImportantNotes(List<String> importantNotes) { this.importantNotes = importantNotes; }
}