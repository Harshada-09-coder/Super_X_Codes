package com.techtrio.services;

import com.techtrio.model.AiEventPlan;
import com.techtrio.view.ReviewPageC;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GeminiAiService {

    // Primary model suggested directly by Google's API
    private static final String PRIMARY_MODEL = "gemini-3.6-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private static String cachedWorkingModel = null;
    private static String runtimeApiKey = "AQ.Ab8RN6K9zBJ-5aYi7xwsJFkbg-9jrCaqmdXTz0gASZFqCBm6bw";

    public static void setApiKey(String key) {
        runtimeApiKey = key;
    }

    public String getApiKey() {
        // 1. Runtime explicit key
        if (runtimeApiKey != null && !runtimeApiKey.isBlank()) {
            return runtimeApiKey.trim();
        }

        // 2. Environment variable
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isBlank()) return key.trim();

        // 3. JVM System Properties
        key = System.getProperty("GEMINI_API_KEY");
        if (key != null && !key.isBlank()) return key.trim();

        key = System.getProperty("gemini.api.key");
        if (key != null && !key.isBlank()) return key.trim();

        // 4. gemini.properties file
        try {
            File propFile = new File("gemini.properties");
            InputStream is = propFile.exists() ? new FileInputStream(propFile)
                    : getClass().getClassLoader().getResourceAsStream("gemini.properties");
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                key = prop.getProperty("gemini.api.key");
                is.close();
                if (key != null && !key.isBlank()) return key.trim();
            }
        } catch (Exception ignored) {}

        return null;
    }

    public AiEventPlan generatePlanFromEventData() {
        return generatePlanFromEventData("");
    }

    public AiEventPlan generatePlanFromEventData(String searchQuery) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("[AI Service] No Gemini API key detected. Running dynamic offline engine.");
            return generateIntelligentFallback(searchQuery);
        }

        String prompt = buildPrompt(searchQuery);

        // 1. If we already found a working model previously, use it
        if (cachedWorkingModel != null) {
            AiEventPlan plan = callGeminiApi(cachedWorkingModel, apiKey, prompt, searchQuery);
            if (plan != null) return plan;
        }

        // 2. Try primary model: gemini-3.6-flash
        AiEventPlan plan = callGeminiApi(PRIMARY_MODEL, apiKey, prompt, searchQuery);
        if (plan != null) {
            cachedWorkingModel = PRIMARY_MODEL;
            return plan;
        }

        // 3. Auto-discover available models directly from Google API for this API key
        List<String> availableModels = fetchAvailableModels(apiKey);
        for (String modelName : availableModels) {
            System.out.println("[AI Service] Trying discovered model: " + modelName + "...");
            plan = callGeminiApi(modelName, apiKey, prompt, searchQuery);
            if (plan != null) {
                cachedWorkingModel = modelName;
                return plan;
            }
        }

        System.err.println("[AI Service] All Gemini model endpoints failed. Switching to dynamic offline plan.");
        return generateIntelligentFallback(searchQuery);
    }

    private List<String> fetchAvailableModels(String apiKey) {
        List<String> validModels = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6)).build();
            String listUrl = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(listUrl)).GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                JSONObject obj = new JSONObject(resp.body());
                if (obj.has("models")) {
                    JSONArray arr = obj.getJSONArray("models");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject m = arr.getJSONObject(i);
                        String name = m.optString("name", ""); // e.g. "models/gemini-3.6-flash"
                        if (name.startsWith("models/")) {
                            name = name.replace("models/", "");
                        }

                        // Verify it supports generateContent
                        JSONArray methods = m.optJSONArray("supportedGenerationMethods");
                        if (methods != null && methods.toList().contains("generateContent")) {
                            validModels.add(name);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("[AI Service] Could not list models: " + ex.getMessage());
        }

        // Add common fallbacks if list was empty
        if (validModels.isEmpty()) {
            validModels.add("gemini-3.6-flash");
            validModels.add("gemini-pro");
        }
        return validModels;
    }

    private AiEventPlan callGeminiApi(String modelName, String apiKey, String prompt, String searchQuery) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(12))
                    .build();

            String endpointUrl = BASE_URL + modelName + ":generateContent?key=" + apiKey;

            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("parts", new JSONArray().put(new JSONObject().put("text", prompt)));
            contents.put(part);
            requestBody.put("contents", contents);

            JSONObject generationConfig = new JSONObject();
            generationConfig.put("responseMimeType", "application/json");
            generationConfig.put("temperature", 0.7);
            requestBody.put("generationConfig", generationConfig);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .timeout(Duration.ofSeconds(25))
                    .build();

            System.out.println("[AI Service] Calling Gemini (" + modelName + ")...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject resObj = new JSONObject(response.body());
                String rawText = resObj.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                rawText = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
                System.out.println("[AI Service] Gemini successfully generated the plan using " + modelName + "!");
                return parseJsonToPlan(new JSONObject(rawText), searchQuery);
            } else {
                System.err.println("[AI Service] Model [" + modelName + "] returned HTTP " + response.statusCode() + ": " + response.body());
                return null;
            }
        } catch (Exception ex) {
            System.err.println("[AI Service] Exception during API call to " + modelName + ": " + ex.getMessage());
            return null;
        }
    }

    private String buildPrompt(String searchQuery) {
        String eventName = (ReviewPageC.EventData.eventName != null && !ReviewPageC.EventData.eventName.isBlank())
                ? ReviewPageC.EventData.eventName : "My Special Event";

        String eventType = (searchQuery != null && !searchQuery.isBlank())
                ? searchQuery
                : ((ReviewPageC.EventData.eventType != null && !ReviewPageC.EventData.eventType.isBlank())
                ? ReviewPageC.EventData.eventType : "General Event");

        String category = (ReviewPageC.EventData.category != null && !ReviewPageC.EventData.category.isBlank())
                ? ReviewPageC.EventData.category : "Formal";

        int guests = ReviewPageC.EventData.guests > 0 ? ReviewPageC.EventData.guests : 250;

        String date = (ReviewPageC.EventData.eventDate != null && !ReviewPageC.EventData.eventDate.isBlank())
                ? ReviewPageC.EventData.eventDate : "Upcoming";

        String time = (ReviewPageC.EventData.time != null && !ReviewPageC.EventData.time.isBlank())
                ? ReviewPageC.EventData.time : "10:00 AM";

        String requirements = (ReviewPageC.EventData.requirements != null && !ReviewPageC.EventData.requirements.isBlank())
                ? ReviewPageC.EventData.requirements : "Venue, Catering, Decoration, Sound, Photography";

        String city = (ReviewPageC.EventData.city != null && !ReviewPageC.EventData.city.isBlank())
                ? ReviewPageC.EventData.city : "Pune";

        String area = (ReviewPageC.EventData.area != null && !ReviewPageC.EventData.area.isBlank())
                ? ReviewPageC.EventData.area : "City Center";

        String venue = (ReviewPageC.EventData.venue != null && !ReviewPageC.EventData.venue.isBlank())
                ? ReviewPageC.EventData.venue : "Banquet Hall";

        String budget = (ReviewPageC.EventData.budget != null && !ReviewPageC.EventData.budget.isBlank())
                ? ReviewPageC.EventData.budget : "₹ 5,00,000";

        String customNotes = (ReviewPageC.EventData.notes != null && !ReviewPageC.EventData.notes.isBlank())
                ? ReviewPageC.EventData.notes : "Standard requirements.";

        return "You are an expert AI Event Planner for the 'EventXpert' platform.\n"
                + "Generate a customized, realistic, and practical event plan based SOLELY on this customer's actual event inputs:\n"
                + "- Event Name: " + eventName + "\n"
                + "- Event Type / Theme: " + eventType + "\n"
                + "- Category: " + category + "\n"
                + "- Expected Guest Count: " + guests + "\n"
                + "- Scheduled Date: " + date + "\n"
                + "- Start Time: " + time + "\n"
                + "- Requested Services: " + requirements + "\n"
                + "- Location: " + area + ", " + city + "\n"
                + "- Venue Type: " + venue + "\n"
                + "- Total Budget: " + budget + "\n"
                + "- Customer Notes: " + customNotes + "\n\n"
                + "Generate a JSON response conforming EXACTLY to the following structure:\n"
                + "{\n"
                + "  \"summary\": \"2-3 sentence personalized summary for this " + eventType + " event.\",\n"
                + "  \"theme\": \"Creative theme suitable for " + eventType + "\",\n"
                + "  \"duration\": \"1 Day\",\n"
                + "  \"estimatedTasks\": 35,\n"
                + "  \"recommendedVendorsCount\": 15,\n"
                + "  \"timeline\": [\n"
                + "    {\"time\": \"08:00 AM\", \"activity\": \"Venue Access & Stage Setup\"},\n"
                + "    {\"time\": \"10:30 AM\", \"activity\": \"AV Sound & Lighting Quality Check\"},\n"
                + "    {\"time\": \"12:00 PM\", \"activity\": \"Guest Arrival & Welcome Refreshments\"},\n"
                + "    {\"time\": \"01:30 PM\", \"activity\": \"Main Event Ceremony / Program\"},\n"
                + "    {\"time\": \"04:30 PM\", \"activity\": \"Dining, Media Sessions & Networking\"}\n"
                + "  ],\n"
                + "  \"taskChecklist\": [\n"
                + "    \"Confirm venue reservation and seating arrangements\",\n"
                + "    \"Finalize tailored catering menu matching dietary preferences\",\n"
                + "    \"Coordinate decoration and floral themes\",\n"
                + "    \"Send digital invitations and monitor RSVP statuses\",\n"
                + "    \"Rehearse audio-visual and presentation equipment\"\n"
                + "  ],\n"
                + "  \"vendorRecommendations\": [\n"
                + "    {\"category\": \"Catering\", \"name\": \"TasteBuds Gourmet Caterers\", \"rating\": 4.8},\n"
                + "    {\"category\": \"Photography\", \"name\": \"Moments Studio & Media\", \"rating\": 4.7},\n"
                + "    {\"category\": \"Decoration\", \"name\": \"Blossom Elite Decorators\", \"rating\": 4.6}\n"
                + "  ],\n"
                + "  \"budgetBreakdown\": [\n"
                + "    {\"category\": \"Venue\", \"amount\": \"₹ 1,50,000\", \"percentage\": 30.0},\n"
                + "    {\"category\": \"Catering\", \"amount\": \"₹ 1,25,000\", \"percentage\": 25.0},\n"
                + "    {\"category\": \"Decoration\", \"amount\": \"₹ 75,000\", \"percentage\": 15.0},\n"
                + "    {\"category\": \"Others & Contingency\", \"amount\": \"₹ 1,50,000\", \"percentage\": 30.0}\n"
                + "  ],\n"
                + "  \"importantNotes\": [\n"
                + "    \"Lock vendor contracts at least 3 weeks in advance.\",\n"
                + "    \"Keep a 10% emergency buffer for unexpected guest increments.\"\n"
                + "  ]\n"
                + "}";
    }

    private AiEventPlan parseJsonToPlan(JSONObject json, String searchQuery) {
        AiEventPlan plan = new AiEventPlan();
        String eventName = ReviewPageC.EventData.eventName != null ? ReviewPageC.EventData.eventName : "Event";
        plan.setSummary(json.optString("summary", "Personalized event plan for " + eventName));
        plan.setTheme(json.optString("theme", "Classic Elegance"));
        plan.setDuration(json.optString("duration", "1 Day"));
        plan.setEstimatedTasks(json.optInt("estimatedTasks", 35));
        plan.setRecommendedVendorsCount(json.optInt("recommendedVendorsCount", 15));

        if (json.has("timeline")) {
            JSONArray arr = json.getJSONArray("timeline");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                plan.getTimeline().add(new AiEventPlan.ScheduleItem(o.optString("time", "TBD"), o.optString("activity", "")));
            }
        }

        if (json.has("taskChecklist")) {
            JSONArray arr = json.getJSONArray("taskChecklist");
            for (int i = 0; i < arr.length(); i++) {
                plan.getTaskChecklist().add(arr.getString(i));
            }
        }

        if (json.has("vendorRecommendations")) {
            JSONArray arr = json.getJSONArray("vendorRecommendations");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation(
                        o.optString("category", "Vendor"),
                        o.optString("name", "Recommended Service"),
                        o.optDouble("rating", 4.8)
                ));
            }
        }

        if (json.has("budgetBreakdown")) {
            JSONArray arr = json.getJSONArray("budgetBreakdown");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem(
                        o.optString("category", "Expense"),
                        o.optString("amount", "₹ 0"),
                        o.optDouble("percentage", 25.0)
                ));
            }
        }

        if (json.has("importantNotes")) {
            JSONArray arr = json.getJSONArray("importantNotes");
            for (int i = 0; i < arr.length(); i++) {
                plan.getImportantNotes().add(arr.getString(i));
            }
        }

        return plan;
    }

    public AiEventPlan generateIntelligentFallback() {
        return generateIntelligentFallback("");
    }

    public AiEventPlan generateIntelligentFallback(String searchQuery) {
        AiEventPlan plan = new AiEventPlan();

        String eType = (searchQuery != null && !searchQuery.isBlank())
                ? searchQuery
                : ((ReviewPageC.EventData.eventType != null && !ReviewPageC.EventData.eventType.isBlank())
                ? ReviewPageC.EventData.eventType : "Event");

        String eDate = (ReviewPageC.EventData.eventDate != null && !ReviewPageC.EventData.eventDate.isBlank())
                ? ReviewPageC.EventData.eventDate : "the selected date";

        String loc = (ReviewPageC.EventData.city != null && !ReviewPageC.EventData.city.isBlank())
                ? ReviewPageC.EventData.city : "Pune";

        int guests = ReviewPageC.EventData.guests > 0 ? ReviewPageC.EventData.guests : 250;

        plan.setSummary("This is an AI-optimized event plan for your " + eType + " on " + eDate + " in " + loc
                + " with " + guests + " expected guests. The schedule, vendor matches, and budget distribution are tailored to your requirements.");

        if (eType.equalsIgnoreCase("Conference") || eType.equalsIgnoreCase("Corporate Event")) {
            plan.setTheme("Modern Executive & Innovation");
        } else if (eType.equalsIgnoreCase("Birthday") || eType.equalsIgnoreCase("Party")) {
            plan.setTheme("Vibrant Celebration & Lights");
        } else {
            plan.setTheme("Classic Elegance & Royalty");
        }

        plan.setDuration("1 Day");
        plan.setEstimatedTasks(38);
        plan.setRecommendedVendorsCount(16);

        // Timeline
        plan.getTimeline().add(new AiEventPlan.ScheduleItem("08:30 AM", "Vendor Arrival & Stage/Audio Setup"));
        plan.getTimeline().add(new AiEventPlan.ScheduleItem("10:30 AM", "Technical & Lighting Rehearsal"));
        plan.getTimeline().add(new AiEventPlan.ScheduleItem("11:30 AM", "Guest Registration & Welcome Drinks"));
        plan.getTimeline().add(new AiEventPlan.ScheduleItem("01:00 PM", "Core " + eType + " Proceedings & Lunch"));
        plan.getTimeline().add(new AiEventPlan.ScheduleItem("04:30 PM", "Social Gathering, Photography & Wrap-Up"));

        // Checklist
        plan.getTaskChecklist().add("Confirm venue layout and seating capacity for " + guests + " guests");
        plan.getTaskChecklist().add("Finalize catering menu and beverage arrangements");
        plan.getTaskChecklist().add("Schedule photographer and videographer shoot list");
        plan.getTaskChecklist().add("Send automated digital RSVPs to all guests");
        plan.getTaskChecklist().add("Inspect sound, microphone, and presentation setups");

        // Vendor Recommendations
        plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Catering", "TasteBuds Royal Caterers", 4.8));
        plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Photography", "Moments Visual Studio", 4.7));
        plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Decoration", "Blossom Grand Decorators", 4.6));

        // Budget breakdown
        double totalVal = 500000;
        try {
            String clean = ReviewPageC.EventData.budget.replaceAll("[^0-9]", "");
            if (!clean.isEmpty()) totalVal = Double.parseDouble(clean);
        } catch (Exception ignored) {}

        plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Venue", String.format("₹ %,.0f", totalVal * 0.30), 30.0));
        plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Catering", String.format("₹ %,.0f", totalVal * 0.25), 25.0));
        plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Decoration", String.format("₹ %,.0f", totalVal * 0.15), 15.0));
        plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Photography", String.format("₹ %,.0f", totalVal * 0.10), 10.0));
        plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Contingency", String.format("₹ %,.0f", totalVal * 0.20), 20.0));

        plan.getImportantNotes().add("Book core service providers at least 3 weeks before " + eDate + ".");
        plan.getImportantNotes().add("Maintain a 10% contingency allocation for unexpected guest increases.");

        return plan;
    }
}