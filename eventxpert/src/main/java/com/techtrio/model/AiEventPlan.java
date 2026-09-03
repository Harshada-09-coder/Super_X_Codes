package com.techtrio.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AiEventPlan {

    private String summary = "";
    private String theme = "Classic Elegance";
    private String duration = "1 Day";
    private int estimatedTasks = 0;
    private int recommendedVendorsCount = 0;

    private List<ScheduleItem> timeline = new ArrayList<>();
    private List<String> taskChecklist = new ArrayList<>();
    private List<VendorRecommendation> vendorRecommendations = new ArrayList<>();
    private List<BudgetItem> budgetBreakdown = new ArrayList<>();
    private List<String> importantNotes = new ArrayList<>();

    public AiEventPlan() {
    }

    // --- JSON SERIALIZER ---
    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("summary", summary);
        obj.put("theme", theme);
        obj.put("duration", duration);
        obj.put("estimatedTasks", estimatedTasks);
        obj.put("recommendedVendorsCount", recommendedVendorsCount);

        JSONArray tlArr = new JSONArray();
        for (ScheduleItem s : timeline) {
            JSONObject sObj = new JSONObject();
            sObj.put("time", s.getTime());
            sObj.put("activity", s.getActivity());
            tlArr.put(sObj);
        }
        obj.put("timeline", tlArr);

        JSONArray tasksArr = new JSONArray(taskChecklist);
        obj.put("taskChecklist", tasksArr);

        JSONArray vArr = new JSONArray();
        for (VendorRecommendation v : vendorRecommendations) {
            JSONObject vObj = new JSONObject();
            vObj.put("category", v.getCategory());
            vObj.put("name", v.getName());
            vObj.put("rating", v.getRating());
            vArr.put(vObj);
        }
        obj.put("vendorRecommendations", vArr);

        JSONArray bArr = new JSONArray();
        for (BudgetItem b : budgetBreakdown) {
            JSONObject bObj = new JSONObject();
            bObj.put("category", b.getCategory());
            bObj.put("amount", b.getAmount());
            bObj.put("percentage", b.getPercentage());
            bArr.put(bObj);
        }
        obj.put("budgetBreakdown", bArr);

        JSONArray nArr = new JSONArray(importantNotes);
        obj.put("importantNotes", nArr);

        return obj.toString();
    }

    // --- JSON DESERIALIZER ---
    public static AiEventPlan fromJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return new AiEventPlan();
        }
        try {
            JSONObject obj = new JSONObject(jsonStr);
            AiEventPlan plan = new AiEventPlan();
            plan.setSummary(obj.optString("summary", ""));
            plan.setTheme(obj.optString("theme", "Classic Elegance"));
            plan.setDuration(obj.optString("duration", "1 Day"));
            plan.setEstimatedTasks(obj.optInt("estimatedTasks", 0));
            plan.setRecommendedVendorsCount(obj.optInt("recommendedVendorsCount", 0));

            if (obj.has("timeline")) {
                JSONArray tlArr = obj.getJSONArray("timeline");
                for (int i = 0; i < tlArr.length(); i++) {
                    JSONObject sObj = tlArr.getJSONObject(i);
                    plan.getTimeline().add(new ScheduleItem(sObj.optString("time", ""), sObj.optString("activity", "")));
                }
            }

            if (obj.has("taskChecklist")) {
                JSONArray tArr = obj.getJSONArray("taskChecklist");
                for (int i = 0; i < tArr.length(); i++) {
                    plan.getTaskChecklist().add(tArr.getString(i));
                }
            }

            if (obj.has("vendorRecommendations")) {
                JSONArray vArr = obj.getJSONArray("vendorRecommendations");
                for (int i = 0; i < vArr.length(); i++) {
                    JSONObject vObj = vArr.getJSONObject(i);
                    plan.getVendorRecommendations().add(new VendorRecommendation(
                            vObj.optString("category", ""),
                            vObj.optString("name", ""),
                            vObj.optDouble("rating", 4.8)
                    ));
                }
            }

            if (obj.has("budgetBreakdown")) {
                JSONArray bArr = obj.getJSONArray("budgetBreakdown");
                for (int i = 0; i < bArr.length(); i++) {
                    JSONObject bObj = bArr.getJSONObject(i);
                    plan.getBudgetBreakdown().add(new BudgetItem(
                            bObj.optString("category", ""),
                            bObj.optString("amount", "₹ 0"),
                            bObj.optDouble("percentage", 0.0)
                    ));
                }
            }

            if (obj.has("importantNotes")) {
                JSONArray nArr = obj.getJSONArray("importantNotes");
                for (int i = 0; i < nArr.length(); i++) {
                    plan.getImportantNotes().add(nArr.getString(i));
                }
            }

            return plan;
        } catch (Exception e) {
            System.err.println("Error parsing AiEventPlan JSON: " + e.getMessage());
            return new AiEventPlan();
        }
    }

    // --- INNER CLASSES ---
    public static class ScheduleItem {
        private String time;
        private String activity;

        public ScheduleItem() {}
        public ScheduleItem(String time, String activity) {
            this.time = time;
            this.activity = activity;
        }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }
    }

    public static class VendorRecommendation {
        private String category;
        private String name;
        private double rating;

        public VendorRecommendation() {}
        public VendorRecommendation(String category, String name, double rating) {
            this.category = category;
            this.name = name;
            this.rating = rating;
        }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
    }

    public static class BudgetItem {
        private String category;
        private String amount;
        private double percentage;

        public BudgetItem() {}
        public BudgetItem(String category, String amount, double percentage) {
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
        }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    // --- GETTERS & SETTERS ---
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public int getEstimatedTasks() { return estimatedTasks; }
    public void setEstimatedTasks(int estimatedTasks) { this.estimatedTasks = estimatedTasks; }
    public int getRecommendedVendorsCount() { return recommendedVendorsCount; }
    public void setRecommendedVendorsCount(int count) { this.recommendedVendorsCount = count; }
    public List<ScheduleItem> getTimeline() { return timeline; }
    public void setTimeline(List<ScheduleItem> timeline) { this.timeline = timeline; }
    public List<String> getTaskChecklist() { return taskChecklist; }
    public void setTaskChecklist(List<String> taskChecklist) { this.taskChecklist = taskChecklist; }
    public List<VendorRecommendation> getVendorRecommendations() { return vendorRecommendations; }
    public void setVendorRecommendations(List<VendorRecommendation> recs) { this.vendorRecommendations = recs; }
    public List<BudgetItem> getBudgetBreakdown() { return budgetBreakdown; }
    public void setBudgetBreakdown(List<BudgetItem> budgetBreakdown) { this.budgetBreakdown = budgetBreakdown; }
    public List<String> getImportantNotes() { return importantNotes; }
    public void setImportantNotes(List<String> importantNotes) { this.importantNotes = importantNotes; }
}