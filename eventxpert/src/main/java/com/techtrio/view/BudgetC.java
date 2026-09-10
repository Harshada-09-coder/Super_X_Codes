package com.techtrio.view;

import com.techtrio.model.budget.BudgetAllocation;
import com.techtrio.model.budget.BudgetOptimizationRequest;
import com.techtrio.model.budget.BudgetOptimizationResult;
import com.techtrio.services.BudgetOptimizerService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BudgetC {

    private Scene scene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#CBD5E1";

    private Label totalAmount;
    private VBox allocationRowsContainer;
    private ToggleButton toggleOptimizer;
    private VBox smartRecommendationBox;
    private final BudgetOptimizerService optimizerService = new BudgetOptimizerService();

    public BudgetC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public BudgetC() {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
    }

    public Scene getBudgetScene() {
        return getBudgetScene(null, null);
    }

    public Scene getBudgetScene(Runnable backAction, Runnable nextAction) {
        ReviewPageC.EventData.currentStep = 4;

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
                sidebarItem("[4]", "Budget", "fas-wallet", true),
                sidebarItem("[5]", "Guest", "fas-users", false),
                sidebarItem("[6]", "AI Planning", "fas-magic", false),
                sidebarItem("[7]", "Review", "fas-check-circle", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandBox, titleBox, menu, sideSpace);

        // 2. CONTENT (TopBar Removed)
        VBox content = new VBox(14);
        content.setPadding(new Insets(28, 45, 20, 45));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label stepText = label("STEP 4 OF 7", 10, FontWeight.BOLD, BLUE);
        Label heading = label("Set Your Event Budget", 26, FontWeight.BOLD, NAVY);
        Label description = label("Define your total spend and let our engine optimize the recommended distribution.", 12, FontWeight.NORMAL, "#667085");

        // LEFT COLUMN - Input & Toggle
        VBox estimated = card();
        estimated.getChildren().add(label("Estimated Total Budget", 14, FontWeight.BOLD, NAVY));

        String initialBudget = (ReviewPageC.EventData.budget != null && !ReviewPageC.EventData.budget.isBlank())
                ? ReviewPageC.EventData.budget
                : "";

        TextField amountField = new TextField(initialBudget);
        amountField.setPromptText("Enter Total Budget (e.g. 100000)");
        amountField.setPrefHeight(44);
        amountField.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        amountField.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:7;-fx-background-radius:7;-fx-padding:8 12;-fx-text-fill:" + NAVY + " !important;");

        estimated.getChildren().addAll(amountField, label("ⓘ Enter your estimated budget for the event.", 11, FontWeight.BOLD, NAVY));

        // Smart Budget Optimizer Card
        VBox smartCard = card();
        HBox smartHeader = new HBox();
        smartHeader.setAlignment(Pos.CENTER_LEFT);

        Label smartTitle = label("✦  Smart Budget Optimizer", 15, FontWeight.BOLD, NAVY);
        Region smartSpace = new Region();
        HBox.setHgrow(smartSpace, Priority.ALWAYS);

        toggleOptimizer = new ToggleButton("ON");
        toggleOptimizer.setSelected(true);
        toggleOptimizer.setPrefSize(58, 28);
        toggleOptimizer.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        toggleOptimizer.setTextFill(Color.WHITE);
        toggleOptimizer.setStyle("-fx-background-color:" + BLUE + ";-fx-background-radius:20;-fx-cursor:hand;");

        smartHeader.getChildren().addAll(smartTitle, smartSpace, toggleOptimizer);

        smartRecommendationBox = new VBox(6);
        smartRecommendationBox.setPadding(new Insets(10));
        smartRecommendationBox.setStyle("-fx-background-color:#F4F7F9;-fx-border-color:" + NAVY + ";-fx-border-width:0 0 0 3;-fx-border-radius:4;");

        smartCard.getChildren().addAll(smartHeader, label("Auto-allocate funds based on selected requirements.", 11, FontWeight.BOLD, NAVY), smartRecommendationBox);

        VBox leftColumn = new VBox(14, estimated, smartCard);
        leftColumn.setPrefWidth(380);

        // RIGHT COLUMN - Recommendation Display
        VBox allocationCard = card();
        HBox allocationHeader = new HBox();
        allocationHeader.setAlignment(Pos.CENTER_LEFT);

        VBox allocationText = new VBox(2,
                label("AI/Smart Recommended Budget", 16, FontWeight.BOLD, NAVY),
                label("Optimized for requirements chosen in Step 2", 11, FontWeight.BOLD, BLUE)
        );
        Region allocationSpace = new Region();
        HBox.setHgrow(allocationSpace, Priority.ALWAYS);

        totalAmount = label("₹ 0", 20, FontWeight.BOLD, NAVY);
        VBox totalBox = new VBox(1, totalAmount, label("Total Allocated", 10, FontWeight.BOLD, NAVY));
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        allocationHeader.getChildren().addAll(allocationText, allocationSpace, totalBox);

        allocationRowsContainer = new VBox(10);
        allocationRowsContainer.setPadding(new Insets(10, 0, 10, 0));

        Label disclaimer = label("ⓘ This is a recommended allocation. Final budget will be managed and finalized by your organizer.", 11, FontWeight.BOLD, "#92400E");
        disclaimer.setStyle("-fx-background-color:#FEF3C7;-fx-padding:8 12;-fx-background-radius:6;-fx-border-color:#FDE68A;-fx-border-radius:6;");
        disclaimer.setWrapText(true);

        allocationCard.getChildren().addAll(allocationHeader, new Separator(), allocationRowsContainer, disclaimer);
        HBox.setHgrow(allocationCard, Priority.ALWAYS);

        // Recalculation Engine
        Runnable recalculate = () -> {
            double parsedVal = 0.0;
            try {
                String text = amountField.getText();
                if (text != null) {
                    String clean = text.replaceAll("[^0-9.]", "").trim();
                    if (!clean.isEmpty()) parsedVal = Double.parseDouble(clean);
                }
            } catch (Exception ignored) {}

            totalAmount.setText(String.format("₹ %,.0f", parsedVal));

            if (parsedVal <= 0) {
                allocationRowsContainer.getChildren().clear();
                allocationRowsContainer.getChildren().add(label("Please enter your estimated budget to see optimization.", 12, FontWeight.NORMAL, NAVY));
                smartRecommendationBox.getChildren().clear();
                smartRecommendationBox.getChildren().add(label("Awaiting Budget Input...", 11, FontWeight.BOLD, NAVY));
                return;
            }

            if (!toggleOptimizer.isSelected()) {
                allocationRowsContainer.getChildren().clear();
                Label offLabel = label("Smart Optimization is turned OFF.\nYour total budget (" + String.format("₹ %,.0f", parsedVal) + ") will be submitted directly to your organizer for manual planning.", 13, FontWeight.BOLD, NAVY);
                offLabel.setPadding(new Insets(20));
                allocationRowsContainer.getChildren().add(offLabel);
                smartRecommendationBox.getChildren().clear();
                smartRecommendationBox.getChildren().add(label("Optimizer Inactive (Manual Mode)", 11, FontWeight.BOLD, NAVY));
                return;
            }

            String reqString = ReviewPageC.EventData.requirements;
            List<String> selectedReqs = new ArrayList<>();
            if (reqString != null && !reqString.isBlank()) {
                selectedReqs.addAll(Arrays.asList(reqString.split(",\\s*")));
            }

            BudgetOptimizationRequest req = new BudgetOptimizationRequest(
                    parsedVal,
                    ReviewPageC.EventData.eventType != null ? ReviewPageC.EventData.eventType : "General",
                    ReviewPageC.EventData.guests > 0 ? ReviewPageC.EventData.guests : 100,
                    selectedReqs,
                    ReviewPageC.EventData.city != null ? ReviewPageC.EventData.city : "Pune"
            );

            BudgetOptimizationResult result = optimizerService.optimizeBudget(req);

            allocationRowsContainer.getChildren().clear();
            smartRecommendationBox.getChildren().clear();
            smartRecommendationBox.getChildren().add(label("Constraint Optimization Applied:", 11, FontWeight.BOLD, NAVY));

            HBox chips = new HBox(6);
            if (result != null && result.getAllocations() != null) {
                for (BudgetAllocation alloc : result.getAllocations()) {
                    allocationRowsContainer.getChildren().add(createBudgetRow(alloc));
                    if (chips.getChildren().size() < 3) {
                        chips.getChildren().add(tag(alloc.getCategory() + " " + alloc.getPercentage() + "%"));
                    }
                }
            }
            smartRecommendationBox.getChildren().add(chips);
        };

        amountField.textProperty().addListener((obs, oldV, newV) -> recalculate.run());
        toggleOptimizer.selectedProperty().addListener((obs, oldV, isSelected) -> {
            toggleOptimizer.setText(isSelected ? "ON" : "OFF");
            toggleOptimizer.setStyle("-fx-background-color:" + (isSelected ? BLUE : "#8A96A3") + ";-fx-background-radius:20;-fx-cursor:hand;");
            recalculate.run();
        });

        recalculate.run();

        HBox mainRow = new HBox(20, leftColumn, allocationCard);
        content.getChildren().addAll(stepText, heading, description, mainRow);

        // FOOTER
        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, "#667085");
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            ReviewPageC.EventData.saveDraft(3);
            if (backAction != null) {
                backAction.run();
            } else {
                LocationC locationPage = new LocationC(uid, idToken);
                LandingPage.landingPageStage.setScene(locationPage.getLocationScene());
            }
        });

        next.setOnAction(e -> {
            String val = amountField.getText() != null ? amountField.getText().trim() : "";
            ReviewPageC.EventData.budget = !val.isBlank() ? (val.startsWith("₹") ? val : "₹ " + val) : "₹ 0";
            ReviewPageC.EventData.saveDraft(5);

            if (nextAction != null) {
                nextAction.run();
            } else {
                GuestManagementC guestPage = new GuestManagementC(uid, idToken);
                LandingPage.landingPageStage.setScene(guestPage.getGuestManagementScene());
            }
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox();
        rightSide.getChildren().addAll(content, footer);
        VBox.setVgrow(content, Priority.ALWAYS);

        HBox fullPage = new HBox();
        fullPage.getChildren().addAll(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        root.setCenter(fullPage);
        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private VBox createBudgetRow(BudgetAllocation allocation) {
        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane iconHolder = new StackPane();
        iconHolder.setPrefSize(30, 30);
        iconHolder.setStyle("-fx-background-color:#E0EDFF;-fx-background-radius:6;");

        try {
            String iconCode = (allocation.getIcon() != null && !allocation.getIcon().isBlank()) ? allocation.getIcon() : "fas-check-circle";
            FontIcon fontIcon = new FontIcon(iconCode);
            fontIcon.setIconSize(14);
            fontIcon.setIconColor(Color.web(BLUE));
            iconHolder.getChildren().add(fontIcon);
        } catch (Exception ex) {
            Label defaultIco = new Label("•");
            defaultIco.setStyle("-fx-text-fill:" + BLUE + ";-fx-font-weight:bold;");
            iconHolder.getChildren().add(defaultIco);
        }

        Label nameLabel = label(allocation.getCategory(), 13, FontWeight.BOLD, NAVY);
        Label percentBadge = label(allocation.getPercentage() + "%", 11, FontWeight.BOLD, BLUE);
        percentBadge.setStyle("-fx-background-color:#E0F2FE;-fx-padding:2 6;-fx-background-radius:8;");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Label amountLabel = label(String.format("₹ %,.0f", allocation.getRecommendedAmount()), 13, FontWeight.BOLD, NAVY);

        top.getChildren().addAll(iconHolder, nameLabel, percentBadge, space, amountLabel);

        ProgressBar bar = new ProgressBar(allocation.getPercentage() / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(6);
        bar.setStyle("-fx-accent:" + BLUE + ";");

        return new VBox(4, top, bar);
    }

    private Label tag(String text) {
        Label t = label(text, 10, FontWeight.BOLD, NAVY);
        t.setPadding(new Insets(3, 7, 3, 7));
        t.setStyle("-fx-background-color:#E0F2FE;-fx-background-radius:10;");
        return t;
    }

    private VBox card() {
        VBox v = new VBox(12);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        return v;
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
        Label l = new Label(text != null ? text : "");
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + " !important;");
        return l;
    }
}