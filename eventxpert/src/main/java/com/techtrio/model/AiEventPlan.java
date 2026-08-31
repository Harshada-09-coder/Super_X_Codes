package com.techtrio.model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

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

    // --- JSON SERIALIZATION & DESERIALIZATION FOR FIRESTORE ---
    public String toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("summary", summary);
            obj.put("theme", theme);
            obj.put("duration", duration);
            obj.put("estimatedTasks", estimatedTasks);
            obj.put("recommendedVendorsCount", recommendedVendorsCount);

            JSONArray tArray = new JSONArray();
            for (ScheduleItem si : timeline) {
                JSONObject tObj = new JSONObject();
                tObj.put("time", si.getTime());
                tObj.put("activity", si.getActivity());
                tArray.put(tObj);
            }
            obj.put("timeline", tArray);

            JSONArray vArray = new JSONArray();
            for (VendorRecommendation vr : vendorRecommendations) {
                JSONObject vObj = new JSONObject();
                vObj.put("category", vr.getCategory());
                vObj.put("name", vr.getName());
                vObj.put("rating", vr.getRating());
                vArray.put(vObj);
            }
            obj.put("vendorRecommendations", vArray);

            JSONArray bArray = new JSONArray();
            for (BudgetItem bi : budgetBreakdown) {
                JSONObject bObj = new JSONObject();
                bObj.put("category", bi.getCategory());
                bObj.put("amount", bi.getAmount());
                bObj.put("percentage", bi.getPercentage());
                bArray.put(bObj);
            }
            obj.put("budgetBreakdown", bArray);

            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public static AiEventPlan fromJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) return null;
        try {
            JSONObject obj = new JSONObject(jsonStr);
            AiEventPlan plan = new AiEventPlan();
            plan.setSummary(obj.optString("summary", ""));
            plan.setTheme(obj.optString("theme", "Classic Elegance"));
            plan.setDuration(obj.optString("duration", "1 Day"));
            plan.setEstimatedTasks(obj.optInt("estimatedTasks", 25));
            plan.setRecommendedVendorsCount(obj.optInt("recommendedVendorsCount", 12));

            JSONArray tArray = obj.optJSONArray("timeline");
            if (tArray != null) {
                for (int i = 0; i < tArray.length(); i++) {
                    JSONObject t = tArray.getJSONObject(i);
                    plan.getTimeline().add(new ScheduleItem(t.optString("time"), t.optString("activity")));
                }
            }

            JSONArray vArray = obj.optJSONArray("vendorRecommendations");
            if (vArray != null) {
                for (int i = 0; i < vArray.length(); i++) {
                    JSONObject v = vArray.getJSONObject(i);
                    plan.getVendorRecommendations().add(new VendorRecommendation(
                            v.optString("category"), v.optString("name"), v.optDouble("rating", 4.5)));
                }
            }

            JSONArray bArray = obj.optJSONArray("budgetBreakdown");
            if (bArray != null) {
                for (int i = 0; i < bArray.length(); i++) {
                    JSONObject b = bArray.getJSONObject(i);
                    plan.getBudgetBreakdown().add(new BudgetItem(
                            b.optString("category"), b.optString("amount"), b.optDouble("percentage", 20.0)));
                }
            }

            return plan;
        } catch (Exception e) {
            return null;
        }
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