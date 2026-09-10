package com.techtrio.view;

import com.techtrio.ai.planning.EventPlanningService;
import com.techtrio.model.AiEventPlan;
import com.techtrio.services.GeminiAiService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AiPlanningC {

    private Scene scene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#CBD5E1";

    private final EventPlanningService planningService = new EventPlanningService();
    private final GeminiAiService geminiService = new GeminiAiService();
    private AiEventPlan currentPlan;

    private StackPane dynamicContentArea;
    private VBox activeTabContent;
    private ProgressIndicator loadingIndicator;
    private TextField searchField;
    private VBox pill5;
    private VBox pill6;

    public AiPlanningC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getAiPlanningScene() {
        ReviewPageC.EventData.currentStep = 6;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(24, 20, 20, 22));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = label("EventXpert", 20, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 9, FontWeight.NORMAL, "#BDE3FF");
        brandTexts.getChildren().addAll(logo, logoSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        Label createTitle = label("Create Event", 18, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", "fas-info-circle", false),
                sidebarItem("[2]", "Requirements", "fas-list-ul", false),
                sidebarItem("[3]", "Venue & Location", "fas-map-marker-alt", false),
                sidebarItem("[4]", "Budget", "fas-wallet", false),
                sidebarItem("[5]", "Guest", "fas-users", false),
                sidebarItem("[6]", "AI Planning", "fas-magic", true),
                sidebarItem("[7]", "Review", "fas-check-circle", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandBox, titleBox, menu, sideSpace);

        // 2. MAIN CONTENT (TopBar Removed)
        VBox content = new VBox(14);
        content.setPadding(new Insets(28, 45, 20, 45));

        Label stepText = label("STEP 6 OF 7", 10, FontWeight.BOLD, BLUE);
        Label mainHeading = label("AI Event Planning", 26, FontWeight.BOLD, NAVY);
        Label mainSubtitle = label("Get a smart, personalized plan for your event in seconds.", 12, FontWeight.NORMAL, "#64748B");
        VBox headerBox = new VBox(3, stepText, mainHeading, mainSubtitle);

        // Search Bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(6, 8, 6, 14));
        searchBar.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");

        searchField = new TextField();
        searchField.setPromptText("Search by event type (e.g. Wedding, Conference, Birthday)...");
        searchField.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;-fx-font-size:12px;-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search →");
        searchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        searchBtn.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:6;-fx-padding:6 14;-fx-cursor:hand;");
        searchBtn.setOnAction(e -> triggerPlanGeneration(searchField.getText().trim()));
        searchField.setOnAction(e -> triggerPlanGeneration(searchField.getText().trim()));

        searchBar.getChildren().addAll(label("🔍", 13, FontWeight.NORMAL, NAVY), searchField, searchBtn);

        HBox featureBanner = createFeatureBanner();

        VBox planResultCard = new VBox(14);
        planResultCard.setPadding(new Insets(18));
        planResultCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");

        HBox planHeader = new HBox(12);
        planHeader.setAlignment(Pos.CENTER_LEFT);

        Label planTitle = label("Your AI Generated Plan", 16, FontWeight.BOLD, NAVY);
        Label draftBadge = label("Timefold Optimized", 10, FontWeight.BOLD, BLUE);
        draftBadge.setStyle("-fx-background-color:#E0EDFF;-fx-text-fill:" + BLUE + ";-fx-padding:3 8;-fx-background-radius:10;");

        Region phSpace = new Region();
        HBox.setHgrow(phSpace, Priority.ALWAYS);

        Button regenBtn = new Button("↺  Regenerate");
        regenBtn.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        regenBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + NAVY + ";-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:6 12;-fx-cursor:hand;");
        regenBtn.setOnAction(e -> triggerPlanGeneration(searchField.getText().trim()));

        planHeader.getChildren().addAll(planTitle, draftBadge, phSpace, regenBtn);

        // Stats Summary
        HBox statsRow = new HBox(10);
        statsRow.setFillHeight(true);

        String dynamicType = ReviewPageC.EventData.eventType.isBlank() ? "Wedding" : ReviewPageC.EventData.eventType;
        String dynamicDate = !ReviewPageC.EventData.startDate.isBlank()
                ? (!ReviewPageC.EventData.endDate.isBlank() && !ReviewPageC.EventData.startDate.equals(ReviewPageC.EventData.endDate)
                ? ReviewPageC.EventData.startDate + " to " + ReviewPageC.EventData.endDate
                : ReviewPageC.EventData.startDate)
                : "18 May 2024";
        String dynamicBudget = ReviewPageC.EventData.budget.isBlank() ? "₹ 8,45,000" : ReviewPageC.EventData.budget;

        String durationStr = "1 Day";
        if (!ReviewPageC.EventData.startDate.isBlank() && !ReviewPageC.EventData.endDate.isBlank()) {
            try {
                java.time.LocalDate s = java.time.LocalDate.parse(ReviewPageC.EventData.startDate.trim());
                java.time.LocalDate e = java.time.LocalDate.parse(ReviewPageC.EventData.endDate.trim());
                long days = java.time.temporal.ChronoUnit.DAYS.between(s, e) + 1;
                if (days > 1) durationStr = days + " Days";
            } catch (Exception ignored) {}
        }

        VBox pill1 = statPill("Event Type", dynamicType, "📅");
        VBox pill2 = statPill("Event Date", dynamicDate, "🕒");
        VBox pill3 = statPill("Duration", durationStr, "⏱");
        VBox pill4 = statPill("Estimated Budget", dynamicBudget, "💰");
        pill5 = statPill("Tasks", "0", "📋");
        pill6 = statPill("Vendors", "0", "🏢");

        for (VBox p : new VBox[]{pill1, pill2, pill3, pill4, pill5, pill6}) {
            HBox.setHgrow(p, Priority.ALWAYS);
            statsRow.getChildren().add(p);
        }

        // Tabs Header
        HBox tabHeader = new HBox(14);
        tabHeader.setStyle("-fx-border-color:#E2E8F0;-fx-border-width:0 0 1.5 0;");
        tabHeader.setPadding(new Insets(8, 0, 4, 0));

        String[] tabs = {"Overview", "Timeline", "Tasks", "Vendors", "Budget Breakdown"};
        Button[] tabButtons = new Button[tabs.length];

        activeTabContent = new VBox(12);
        activeTabContent.setPadding(new Insets(10, 0, 0, 0));

        for (int i = 0; i < tabs.length; i++) {
            final String tabName = tabs[i];
            Button tb = new Button(tabName);
            tb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            tb.setStyle(i == 0
                    ? "-fx-background-color:transparent;-fx-text-fill:" + NAVY + ";-fx-border-color:" + NAVY + ";-fx-border-width:0 0 2 0;-fx-padding:4 6;-fx-cursor:hand;"
                    : "-fx-background-color:transparent;-fx-text-fill:" + BLUE + ";-fx-padding:4 6;-fx-cursor:hand;");

            tb.setOnAction(e -> {
                for (Button b : tabButtons) {
                    b.setStyle("-fx-background-color:transparent;-fx-text-fill:" + BLUE + ";-fx-padding:4 6;-fx-cursor:hand;");
                }
                tb.setStyle("-fx-background-color:transparent;-fx-text-fill:" + NAVY + ";-fx-border-color:" + NAVY + ";-fx-border-width:0 0 2 0;-fx-padding:4 6;-fx-cursor:hand;");
                renderTabContent(tabName);
            });
            tabButtons[i] = tb;
            tabHeader.getChildren().add(tb);
        }

        dynamicContentArea = new StackPane();
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(40, 40);
        dynamicContentArea.getChildren().addAll(activeTabContent, loadingIndicator);

        planResultCard.getChildren().addAll(planHeader, statsRow, tabHeader, dynamicContentArea);
        content.getChildren().addAll(headerBox, searchBar, featureBanner, planResultCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        // FOOTER
        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, NAVY);
        Region fSpace = new Region();
        HBox.setHgrow(fSpace, Priority.ALWAYS);

        Button backBtn = new Button("Back");
        backBtn.setPrefSize(82, 38);
        backBtn.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        backBtn.setOnAction(e -> {
            ReviewPageC.EventData.saveDraft(5);
            GuestManagementC guestView = new GuestManagementC(uid, idToken);
            LandingPage.landingPageStage.setScene(guestView.getGuestManagementScene());
        });

        Button reviewBtn = new Button("Next");
        reviewBtn.setPrefSize(82, 38);
        reviewBtn.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        reviewBtn.setOnAction(e -> {
            savePlanToMemory();
            ReviewPageC.EventData.saveDraft(7);
            ReviewPageC reviewView = new ReviewPageC(uid, idToken);
            LandingPage.landingPageStage.setScene(reviewView.getReviewScene());
        });

        footer.getChildren().addAll(copyright, fSpace, backBtn, reviewBtn);

        VBox rightSide = new VBox(scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1550, 750);
        Platform.runLater(() -> triggerPlanGeneration(""));
        return scene;
    }

    private void triggerPlanGeneration(String query) {
        loadingIndicator.setVisible(true);
        activeTabContent.setOpacity(0.35);

        Task<AiEventPlan> planTask = new Task<>() {
            @Override
            protected AiEventPlan call() {
                return planningService.generateOptimizedPlan(query);
            }
        };

        planTask.setOnSucceeded(e -> {
            currentPlan = planTask.getValue();
            loadingIndicator.setVisible(false);
            activeTabContent.setOpacity(1.0);

            if (pill5 != null && pill6 != null && currentPlan != null) {
                Label taskCountLbl = (Label) pill5.getChildren().get(1);
                taskCountLbl.setText(String.valueOf(currentPlan.getEstimatedTasks()));
                Label vendorCountLbl = (Label) pill6.getChildren().get(1);
                vendorCountLbl.setText(String.valueOf(currentPlan.getRecommendedVendorsCount()));
            }

            renderTabContent("Overview");
        });

        planTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            activeTabContent.setOpacity(1.0);
            currentPlan = geminiService.generateIntelligentFallback(query);
            renderTabContent("Overview");
        });

        Thread th = new Thread(planTask);
        th.setDaemon(true);
        th.start();
    }

    private void renderTabContent(String tabName) {
        activeTabContent.getChildren().clear();
        if (currentPlan == null) return;

        switch (tabName) {
            case "Overview" -> renderOverviewTab();
            case "Timeline" -> renderTimelineTab();
            case "Tasks" -> renderTasksTab();
            case "Vendors" -> renderVendorsTab();
            case "Budget Breakdown" -> renderBudgetTab();
        }
    }

    private void renderOverviewTab() {
        VBox summaryBox = new VBox(8);
        summaryBox.setPadding(new Insets(12));
        summaryBox.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");

        Label sumTitle = label("Event Summary", 15, FontWeight.BOLD, NAVY);
        Label sumText = label(currentPlan.getSummary(), 13, FontWeight.BOLD, NAVY);
        sumText.setWrapText(true);

        summaryBox.getChildren().addAll(sumTitle, sumText);
        activeTabContent.getChildren().add(summaryBox);
    }

    private void renderTimelineTab() {
        VBox timelineBox = new VBox(8);
        if (currentPlan.getTimeline().isEmpty()) {
            timelineBox.getChildren().add(label("No timeline tasks scheduled for the selected requirements.", 12, FontWeight.NORMAL, NAVY));
        } else {
            for (AiEventPlan.ScheduleItem item : currentPlan.getTimeline()) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

                Label timeLbl = label(item.getTime(), 12, FontWeight.BOLD, BLUE);
                timeLbl.setPrefWidth(160);
                Label actLbl = label(item.getActivity(), 13, FontWeight.BOLD, NAVY);

                row.getChildren().addAll(timeLbl, actLbl);
                timelineBox.getChildren().add(row);
            }
        }
        activeTabContent.getChildren().add(timelineBox);
    }

    private void renderTasksTab() {
        VBox tasksBox = new VBox(8);
        if (currentPlan.getTaskChecklist().isEmpty()) {
            tasksBox.getChildren().add(label("No tasks configured.", 12, FontWeight.NORMAL, NAVY));
        } else {
            for (String task : currentPlan.getTaskChecklist()) {
                CheckBox cb = new CheckBox(task);
                cb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                cb.setStyle("-fx-text-fill:" + NAVY + ";-fx-padding:6;-fx-font-weight:bold;");
                tasksBox.getChildren().add(cb);
            }
        }
        activeTabContent.getChildren().add(tasksBox);
    }

    private void renderVendorsTab() {
        VBox vendorsList = new VBox(8);
        for (AiEventPlan.VendorRecommendation vr : currentPlan.getVendorRecommendations()) {
            HBox card = new HBox(12);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

            VBox vInfo = new VBox(2,
                    label(vr.getCategory(), 11, FontWeight.BOLD, BLUE),
                    label(vr.getName(), 14, FontWeight.BOLD, NAVY)
            );
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label rating = label("Rating: " + vr.getRating() + " ★", 12, FontWeight.BOLD, "#92400E");

            card.getChildren().addAll(vInfo, sp, rating);
            vendorsList.getChildren().add(card);
        }
        activeTabContent.getChildren().add(vendorsList);
    }

    private void renderBudgetTab() {
        VBox bBox = new VBox(10);
        for (AiEventPlan.BudgetItem bi : currentPlan.getBudgetBreakdown()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

            Label cat = label(bi.getCategory(), 13, FontWeight.BOLD, NAVY);
            cat.setPrefWidth(180);
            ProgressBar pb = new ProgressBar(bi.getPercentage() / 100.0);
            HBox.setHgrow(pb, Priority.ALWAYS);
            pb.setStyle("-fx-accent:" + BLUE + ";");
            Label amt = label(bi.getAmount(), 13, FontWeight.BOLD, NAVY);

            row.getChildren().addAll(cat, pb, amt);
            bBox.getChildren().add(row);
        }
        activeTabContent.getChildren().add(bBox);
    }

    private void savePlanToMemory() {
        if (currentPlan != null) {
            ReviewPageC.EventData.aiPlanJson = currentPlan.toJson();
            ReviewPageC.EventData.theme = currentPlan.getTheme();
            ReviewPageC.EventData.notes = currentPlan.getSummary();
        }
    }

    private HBox createFeatureBanner() {
        HBox banner = new HBox(16);
        banner.setPadding(new Insets(14));
        banner.setStyle("-fx-background-color:#F0F6FE;-fx-border-color:#CCE3FE;-fx-border-radius:9;-fx-background-radius:9;");

        VBox leftInfo = new VBox(4);
        leftInfo.setPrefWidth(280);
        Label bTitle = label("✨ Timefold AI Planning Active", 15, FontWeight.BOLD, NAVY);
        Label bDesc = label("Our constraint solver creates an optimized schedule strictly for your selected event services and duration.", 12, FontWeight.BOLD, NAVY);
        bDesc.setWrapText(true);
        leftInfo.getChildren().addAll(bTitle, bDesc);

        banner.getChildren().add(leftInfo);
        return banner;
    }

    private VBox statPill(String title, String val, String icon) {
        VBox pill = new VBox(2);
        pill.setPadding(new Insets(8));
        pill.setStyle("-fx-background-color:#F8FAFC;-fx-border-color:" + BORDER + ";-fx-border-radius:7;-fx-background-radius:7;");
        pill.getChildren().addAll(
                label(icon + " " + title, 10, FontWeight.BOLD, NAVY),
                label(val, 13, FontWeight.BOLD, NAVY)
        );
        return pill;
    }

    private HBox sidebarItem(String stepNum, String text, String iconLiteral, boolean active) {
        HBox item = new HBox(10);
        item.setPrefHeight(42);
        item.setMinHeight(42);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 16, 0, 22));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#B7C4D8"));

        Label textLbl = label(stepNum + "  " + text, 12, active ? FontWeight.BOLD : FontWeight.NORMAL, active ? NAVY : "#B7C4D8");

        if (active) {
            item.setStyle("-fx-background-color: #E0F2FE; -fx-background-radius: 7px;");
        } else {
            item.setStyle("-fx-background-color: transparent;");
        }

        item.getChildren().addAll(icon, textLbl);
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + " !important;");
        return l;
    }
}