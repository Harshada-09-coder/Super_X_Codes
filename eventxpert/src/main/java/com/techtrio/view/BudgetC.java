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
    private BudgetOptimizerService optimizerService = new BudgetOptimizerService();

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
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "#FFFFFF");
        Label logoSub = label("EVENT MANAGEMENT", 10, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label createTitle = label("Create Event", 20, FontWeight.BOLD, "#FFFFFF");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", false),
                sidebarItem("[2]", "Requirements", false),
                sidebarItem("[3]", "Venue", false),
                sidebarItem("[4]", "Budget", true),
                sidebarItem("[5]", "Guest", false),
                sidebarItem("[6]", "AI Planning", false),
                sidebarItem("[7]", "Review", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brand, titleBox, menu, sideSpace);

        // 2. TOP BAR
        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:0 0 1 0;");

        Label topLogo = label("EventXpert", 14, FontWeight.BOLD, NAVY);
        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        Label bell = label("Notifications", 11, FontWeight.BOLD, NAVY);
        Label help = label("?", 13, FontWeight.BOLD, NAVY);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + NAVY + ";-fx-border-radius:50;-fx-background-radius:50;");
        topBar.getChildren().addAll(topLogo, topSpace, bell, help);

        // 3. CONTENT
        VBox content = new VBox(14);
        content.setPadding(new Insets(20, 35, 18, 35));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label stepText = label("STEP 4 OF 7", 11, FontWeight.BOLD, BLUE);
        Label heading = label("Set Your Event Budget", 26, FontWeight.BOLD, NAVY);
        Label description = label("Define your total spend and let our engine optimize the recommended distribution.", 13, FontWeight.BOLD, NAVY);

        // LEFT COLUMN - Input & Toggle
        VBox estimated = card();
        estimated.getChildren().add(label("Estimated Total Budget", 14, FontWeight.BOLD, NAVY));

        String initialBudget = (ReviewPageC.EventData.budget != null && !ReviewPageC.EventData.budget.isBlank())
                ? ReviewPageC.EventData.budget
                : "₹ 8,45,000";

        TextField amountField = new TextField(initialBudget);
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

        totalAmount = label(initialBudget, 20, FontWeight.BOLD, NAVY);
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

        // Safe Recalculation Engine
        Runnable recalculate = () -> {
            double parsedVal = 845000.0;
            try {
                String text = amountField.getText();
                if (text != null) {
                    String clean = text.replaceAll("[^0-9.]", "").trim();
                    if (!clean.isEmpty()) {
                        parsedVal = Double.parseDouble(clean);
                    }
                }
            } catch (Exception ignored) {}

            totalAmount.setText(String.format("₹ %,.0f", parsedVal));

            if (!toggleOptimizer.isSelected()) {
                allocationRowsContainer.getChildren().clear();
                Label offLabel = label("Smart Optimization is turned OFF.\nYour total budget (" + String.format("₹ %,.0f", parsedVal) + ") will be submitted directly to your organizer for manual planning.", 13, FontWeight.BOLD, NAVY);
                offLabel.setPadding(new Insets(20));
                allocationRowsContainer.getChildren().add(offLabel);
                smartRecommendationBox.getChildren().clear();
                smartRecommendationBox.getChildren().add(label("Optimizer Inactive (Manual Mode)", 11, FontWeight.BOLD, NAVY));
                return;
            }

            // Extract selected requirements safely
            String reqString = ReviewPageC.EventData.requirements;
            List<String> selectedReqs = new ArrayList<>();
            if (reqString != null && !reqString.isBlank()) {
                selectedReqs.addAll(Arrays.asList(reqString.split(",\\s*")));
            }

            BudgetOptimizationRequest req = new BudgetOptimizationRequest(
                    parsedVal,
                    ReviewPageC.EventData.eventType != null ? ReviewPageC.EventData.eventType : "Wedding",
                    ReviewPageC.EventData.guests > 0 ? ReviewPageC.EventData.guests : 150,
                    selectedReqs,
                    ReviewPageC.EventData.city != null ? ReviewPageC.EventData.city : "Pune"
            );

            BudgetOptimizationResult result = optimizerService.optimizeBudget(req);

            // Populate Recommendation Rows
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

        // Event Listeners
        amountField.textProperty().addListener((obs, oldV, newV) -> recalculate.run());
        toggleOptimizer.selectedProperty().addListener((obs, oldV, isSelected) -> {
            toggleOptimizer.setText(isSelected ? "ON" : "OFF");
            toggleOptimizer.setStyle("-fx-background-color:" + (isSelected ? BLUE : "#8A96A3") + ";-fx-background-radius:20;-fx-cursor:hand;");
            recalculate.run();
        });

        // Initial Run
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

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, NAVY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            } else {
                LocationC locationPage = new LocationC(uid, idToken);
                LandingPage.landingPageStage.setScene(locationPage.getLocationScene());
            }
        });

        next.setOnAction(e -> {
            ReviewPageC.EventData.budget = amountField.getText() != null ? amountField.getText().trim() : "₹ 8,45,000";

            if (nextAction != null) {
                nextAction.run();
            } else {
                GuestManagementC guestPage = new GuestManagementC(uid, idToken);
                LandingPage.landingPageStage.setScene(guestPage.getGuestManagementScene());
            }
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox();
        rightSide.getChildren().addAll(topBar, content, footer);
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
        v.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");
        return v;
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setMinHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 13, FontWeight.NORMAL, "white"), label(text, 13, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text != null ? text : "");
        l.setFont(Font.font("Arial", weight, size));
        l.setTextFill(Color.web(color));
        l.setStyle("-fx-text-fill: " + color + " !important; -fx-opacity: 1.0;");
        return l;
    }
}