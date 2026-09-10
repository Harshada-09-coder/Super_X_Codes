package com.techtrio.ai.planning;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import com.techtrio.model.AiEventPlan;
import com.techtrio.view.ReviewPageC;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventPlanningService {

    private final SolverFactory<EventPlanningSolution> solverFactory;

    public EventPlanningService() {
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(EventPlanningSolution.class)
                .withEntityClasses(EventPlanningTask.class)
                .withConstraintProviderClass(EventPlanningConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(2)); // Fast, responsive solve for desktop UI

        this.solverFactory = SolverFactory.create(solverConfig);
    }

    public AiEventPlan generateOptimizedPlan(String searchQuery) {
        // Step 1: Read Event Parameters
        String eventName = (ReviewPageC.EventData.eventName != null && !ReviewPageC.EventData.eventName.isBlank())
                ? ReviewPageC.EventData.eventName : "Event";
        String eventType = (searchQuery != null && !searchQuery.isBlank())
                ? searchQuery
                : ((ReviewPageC.EventData.eventType != null && !ReviewPageC.EventData.eventType.isBlank())
                ? ReviewPageC.EventData.eventType : "General Event");

        String startDateStr = ReviewPageC.EventData.startDate;
        String endDateStr = ReviewPageC.EventData.endDate;
        String startTimeStr = ReviewPageC.EventData.startTime;
        String endTimeStr = ReviewPageC.EventData.endTime;
        String rawRequirements = ReviewPageC.EventData.requirements;

        // Step 2: Parse Customer-Selected Requirements (Strictly the 8 categories)
        Set<String> selectedRequirements = parseSelectedRequirements(rawRequirements);

        // Step 3: Generate Dynamic Planning Slots inside Event Duration
        List<EventPlanningSlot> slots = generatePlanningSlots(startDateStr, endDateStr, startTimeStr, endTimeStr);

        // Step 4: Generate Planning Tasks ONLY for Selected Requirements
        List<EventPlanningTask> tasks = generateTasksForSelectedRequirements(selectedRequirements, eventName, eventType);

        // Step 5: Build Problem Solution
        EventPlanningSolution problem = new EventPlanningSolution(eventName, eventType, selectedRequirements, slots, tasks);

        // Step 6: Solve with Timefold
        Solver<EventPlanningSolution> solver = solverFactory.buildSolver();
        EventPlanningSolution solution = solver.solve(problem);

        // Step 7: Map Solution to View Model
        return convertSolutionToAiEventPlan(solution, startDateStr, endDateStr, selectedRequirements);
    }

    /**
     * Parses the customer's selected requirements string from RequirementsC.
     */
    public Set<String> parseSelectedRequirements(String rawRequirements) {
        Set<String> selected = new LinkedHashSet<>();
        if (rawRequirements == null || rawRequirements.isBlank()) {
            return selected;
        }

        String[] parts = rawRequirements.split(",\\s*");
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                selected.add(trimmed);
            }
        }
        return selected;
    }

    /**
     * Generates slots strictly within the event's start/end dates and times.
     */
    private List<EventPlanningSlot> generatePlanningSlots(String startDateStr, String endDateStr, String startTimeStr, String endTimeStr) {
        List<EventPlanningSlot> slots = new ArrayList<>();

        LocalDate startDate = parseDate(startDateStr, LocalDate.now().plusDays(14));
        LocalDate endDate = parseDate(endDateStr, startDate);
        if (endDate.isBefore(startDate)) endDate = startDate;

        LocalTime startTime = parseTime(startTimeStr, LocalTime.of(9, 0));
        LocalTime endTime = parseTime(endTimeStr, LocalTime.of(18, 0));
        if (endTime.isBefore(startTime.plusMinutes(60))) endTime = startTime.plusHours(8);

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
        int dayIndex = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalTime current = startTime;
            int slotCounter = 1;

            while (current.plusMinutes(30).isBefore(endTime) || current.plusMinutes(30).equals(endTime)) {
                LocalTime next = current.plusMinutes(30);
                String slotId = "SLOT_" + date + "_" + current.format(DateTimeFormatter.ofPattern("HHmm"));
                String slotName = date + " " + current.format(timeFmt) + " - " + next.format(timeFmt);

                slots.add(new EventPlanningSlot(slotId, slotName, date, current, next, dayIndex));
                current = next;
                slotCounter++;
            }
            dayIndex++;
        }

        return slots;
    }

    /**
     * Centralized mapping: Produces tasks ONLY for the customer-selected requirements.
     */
    private List<EventPlanningTask> generateTasksForSelectedRequirements(Set<String> selected, String eventName, String eventType) {
        List<EventPlanningTask> tasks = new ArrayList<>();
        int idCounter = 1;

        // 1. Decoration
        if (selected.contains("Decoration")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Theme & Decor Material Staging", "Decoration", 30, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Stage & Backdrop Construction", "Decoration", 60, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Floral & Ambient Table Arrangements", "Decoration", 30, 2, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Final Decor & Illumination Quality Check", "Decoration", 30, 1, 3));
        }

        // 2. Catering
        if (selected.contains("Catering")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Catering Live Station & Food Warmer Setup", "Catering", 45, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Beverage & Welcome Refreshments Service", "Catering", 30, 2, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Main Course Buffet & Banquet Service", "Catering", 60, 1, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Dining Clearance & Food Waste Logistics", "Catering", 30, 3, 3));
        }

        // 3. Photography
        if (selected.contains("Photography")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Camera Rigging & Lighting Calibration", "Photography", 30, 2, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Pre-Event Portrait & Stage Photo Session", "Photography", 45, 2, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Ceremony Highlights & Candid Photography", "Photography", 90, 1, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Group Photos & Memory Backup Verification", "Photography", 30, 3, 3));
        }

        // 4. Sound System
        if (selected.contains("Sound System")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Audio Console & Speaker Rigging", "Sound System", 45, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Wireless Microphone & Acoustic Sound Check", "Sound System", 30, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Live Audio Engineering & BGM Mixing", "Sound System", 90, 2, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Audio System Teardown & Cable Packing", "Sound System", 30, 3, 3));
        }

        // 5. Anchor
        if (selected.contains("Anchor")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Anchor Run-of-Show & Script Review", "Anchor", 30, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Stage Hosting & Dignitary Announcements", "Anchor", 60, 1, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Audience Engagement & Closing Remarks", "Anchor", 30, 2, 3));
        }

        // 6. Rental Equipment
        if (selected.contains("Rental Equipment")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Rental Furniture & Stanchion Unloading", "Rental Equipment", 45, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Seating Plan & Layout Placement", "Rental Equipment", 45, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Equipment Inventory Reconciliation & Return", "Rental Equipment", 30, 3, 3));
        }

        // 7. Invitation & Printing
        if (selected.contains("Invitation & Printing")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Welcome Signage & Photo Backdrop Installation", "Invitation & Printing", 30, 2, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Reception Desk, Badge & Program Kit Distribution", "Invitation & Printing", 30, 2, 2));
        }

        // 8. Cake & Dessert Services
        if (selected.contains("Cake & Dessert Services")) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Custom Dessert & Cake Display Assembly", "Cake & Dessert Services", 30, 2, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Ceremonial Cake Cutting Coordination", "Cake & Dessert Services", 30, 1, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Dessert Station Service & Refill", "Cake & Dessert Services", 30, 2, 2));
        }

        // If customer selected NO requirements, provide core event milestones
        if (tasks.isEmpty()) {
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Venue Readiness & Door Opening", "General", 30, 1, 1));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Guest Registration & Assembly", "General", 60, 1, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Core Event Execution", "General", 90, 1, 2));
            tasks.add(new EventPlanningTask("T" + (idCounter++), "Event Wrap-Up & Closing", "General", 30, 2, 3));
        }

        return tasks;
    }

    /**
     * Converts Timefold's solved schedule into the UI-ready AiEventPlan model.
     */
    private AiEventPlan convertSolutionToAiEventPlan(EventPlanningSolution solution, String startDateStr, String endDateStr, Set<String> selected) {
        AiEventPlan plan = new AiEventPlan();

        // 1. Sort assigned tasks chronologically by assigned slot
        List<EventPlanningTask> sortedTasks = new ArrayList<>(solution.getTaskList());
        sortedTasks.sort(Comparator.comparing(
                task -> task.getPlanningSlot() != null
                        ? task.getPlanningSlot().getDayIndex() * 1440 + task.getPlanningSlot().getStartMinuteOfDay()
                        : Integer.MAX_VALUE
        ));

        // 2. Timeline schedule items
        for (EventPlanningTask task : sortedTasks) {
            if (task.getPlanningSlot() != null) {
                String timeLabel = task.getPlanningSlot().getFormattedTimeRange();
                String activity = task.getTaskName() + " [" + task.getCategory() + "]";
                plan.getTimeline().add(new AiEventPlan.ScheduleItem(timeLabel, activity));
            }
        }

        // 3. Task checklist
        for (EventPlanningTask task : sortedTasks) {
            plan.getTaskChecklist().add(task.getTaskName() + " (Category: " + task.getCategory() + ")");
        }

        // 4. Vendors (Strictly for selected requirements)
        addVendorRecommendationsForSelected(plan, selected);

        // 5. Budget Breakdown (Strictly for selected requirements)
        addBudgetBreakdownForSelected(plan, selected);

        // 6. Summary & Theme
        int guests = ReviewPageC.EventData.guests > 0 ? ReviewPageC.EventData.guests : 150;
        String reqSummary = selected.isEmpty() ? "general coordination" : String.join(", ", selected);
        plan.setSummary("Timefold-optimized plan for " + solution.getEventName() + " (" + solution.getEventType() +
                ") covering: " + reqSummary + " for " + guests + " guests across the event duration.");
        plan.setTheme(determineTheme(solution.getEventType()));

        long dayCount = calculateDays(startDateStr, endDateStr);
        plan.setDuration(dayCount > 1 ? dayCount + " Days" : "1 Day");
        plan.setEstimatedTasks(solution.getTaskList().size());
        plan.setRecommendedVendorsCount(plan.getVendorRecommendations().size());

        return plan;
    }

    private void addVendorRecommendationsForSelected(AiEventPlan plan, Set<String> selected) {
        if (selected.contains("Catering")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Catering", "TasteBuds Gourmet Caterers", 4.9));
        }
        if (selected.contains("Photography")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Photography", "Moments Visual Studio", 4.8));
        }
        if (selected.contains("Decoration")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Decoration", "Blossom Elite Decorators", 4.7));
        }
        if (selected.contains("Sound System")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Sound System", "AcousticPulse AV & Audio", 4.8));
        }
        if (selected.contains("Anchor")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Anchor", "StageCraft Professional Emcees", 4.9));
        }
        if (selected.contains("Rental Equipment")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Rental Equipment", "Prime Event Rentals & Staging", 4.6));
        }
        if (selected.contains("Invitation & Printing")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Invitation & Printing", "Ink & Elegance Printing House", 4.7));
        }
        if (selected.contains("Cake & Dessert Services")) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Cake & Dessert Services", "Velvet Crust Artisanal Bakery", 4.8));
        }

        if (plan.getVendorRecommendations().isEmpty()) {
            plan.getVendorRecommendations().add(new AiEventPlan.VendorRecommendation("Venue Coordination", "Premier Venue Alliance", 4.8));
        }
    }

    private void addBudgetBreakdownForSelected(AiEventPlan plan, Set<String> selected) {
        double totalBudget = 500000;
        try {
            String clean = ReviewPageC.EventData.budget.replaceAll("[^0-9]", "");
            if (!clean.isEmpty()) totalBudget = Double.parseDouble(clean);
        } catch (Exception ignored) {}

        if (selected.isEmpty()) {
            plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Venue & Logistics", String.format("₹ %,.0f", totalBudget * 0.70), 70.0));
            plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Contingency", String.format("₹ %,.0f", totalBudget * 0.30), 30.0));
            return;
        }

        double perCategoryWeight = 80.0 / selected.size();
        for (String req : selected) {
            double catAmount = totalBudget * (perCategoryWeight / 100.0);
            plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem(req, String.format("₹ %,.0f", catAmount), perCategoryWeight));
        }
        // 20% safety contingency
        plan.getBudgetBreakdown().add(new AiEventPlan.BudgetItem("Safety Contingency", String.format("₹ %,.0f", totalBudget * 0.20), 20.0));
    }

    private String determineTheme(String eventType) {
        if (eventType == null) return "Classic Elegance";
        String lower = eventType.toLowerCase();
        if (lower.contains("conference") || lower.contains("corporate")) return "Modern Executive & Innovation";
        if (lower.contains("birthday") || lower.contains("party")) return "Vibrant Celebration & Neon Glow";
        if (lower.contains("wedding")) return "Royal Heritage & Floral Romance";
        return "Classic Elegance";
    }

    private LocalDate parseDate(String dateStr, LocalDate fallback) {
        if (dateStr == null || dateStr.isBlank()) return fallback;
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private LocalTime parseTime(String timeStr, LocalTime fallback) {
        if (timeStr == null || timeStr.isBlank()) return fallback;
        String clean = timeStr.trim().toUpperCase();
        String[] patterns = {"h:mm a", "hh:mm a", "h:mma", "hh:mma", "H:mm", "HH:mm"};
        for (String pattern : patterns) {
            try {
                return LocalTime.parse(clean, DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            } catch (Exception ignored) {}
        }
        return fallback;
    }

    private long calculateDays(String startStr, String endStr) {
        try {
            LocalDate s = LocalDate.parse(startStr.trim());
            LocalDate e = LocalDate.parse(endStr.trim());
            return java.time.temporal.ChronoUnit.DAYS.between(s, e) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
}