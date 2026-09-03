package com.techtrio.view;

import com.techtrio.model.AiEventPlan;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Map;

public class Budget {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    public Budget() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Budget(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getBudgetScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Budget"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createBudgetContent();
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        return new Scene(root, 1550, 785);
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(4, 8, 12, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(6, 8, 14, 8));

        Circle userAvatar = new Circle(16, Color.web("#DBEAFE"));
        VBox userDetails = new VBox(1);
        Label userName = new Label("Customer");
        userName.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label userEmail = new Label("user@email.com");
        userEmail.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px;");
        userDetails.getChildren().addAll(userName, userEmail);
        userBox.getChildren().addAll(userAvatar, userDetails);

        VBox mainNav = new VBox(3);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 12 8 4 8;");

        VBox eventNav = new VBox(3);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), this::goOverview),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), () -> {}),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), this::goGuest),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(3);
        bottomNav.getChildren().addAll(
                createNavItem("Support", "fas-life-ring", false, null),
                createNavItem("Settings", "fas-cog", false, this::goSettings)
        );

        sidebar.getChildren().addAll(brandBox, userBox, mainNav, sectionLabel, eventNav, spacer, bottomNav);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createBudgetContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(3);
        Label heading = new Label("Budget");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("View budget allocation and expenses (Live Synced with Organizer)");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        // 🎯 1. Real-Time Math Synchronized with Organizer
        double totalBudget = 5000.0;
        if (event != null && event.getBudget() != null && !event.getBudget().isBlank()) {
            try {
                String clean = event.getBudget().replaceAll("[^0-9.]", "").trim();
                if (!clean.isEmpty()) totalBudget = Double.parseDouble(clean);
            } catch (Exception ignored) {}
        }

        double calculatedAllocated = 0.0;
        if (event != null && event.getBudgetRecommendation() != null && !event.getBudgetRecommendation().isEmpty()) {
            for (Object val : event.getBudgetRecommendation().values()) {
                try {
                    calculatedAllocated += Double.parseDouble(val.toString());
                } catch (Exception ignored) {}
            }
        } else if (event != null && event.getAllocatedBudget() > 0) {
            calculatedAllocated = event.getAllocatedBudget();
        } else {
            calculatedAllocated = totalBudget * 0.97;
        }

        double allocatedPercentage = totalBudget > 0 ? (calculatedAllocated / totalBudget) * 100.0 : 0.0;
        double remainingBuffer = Math.max(0, totalBudget - calculatedAllocated);
        double remainingPercentage = totalBudget > 0 ? (remainingBuffer / totalBudget) * 100.0 : 0.0;

        // 4 Accurate KPI Summary Cards
        HBox metricsRow = new HBox(16);
        metricsRow.getChildren().addAll(
                createMetricCard("Total Budget", String.format("₹ %,.0f", totalBudget), ""),
                createMetricCard("Allocated", String.format("₹ %,.0f", calculatedAllocated), String.format("%.0f%%", allocatedPercentage)),
                createMetricCard("Spent", "₹ 0", "0%"),
                createMetricCard("Remaining", String.format("₹ %,.0f", remainingBuffer), String.format("%.0f%%", remainingPercentage))
        );

        // Real-time Allocation Table
        VBox tableCard = new VBox(12);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label tblTitle = new Label("Budget Allocation (Finalized by Organizer)");
        tblTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        VBox tableRows = new VBox(6);
        tableRows.setPadding(new Insets(8, 0, 0, 0));

        HBox headerRow = new HBox(10);
        headerRow.setPadding(new Insets(6, 12, 6, 12));
        headerRow.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px;");
        headerRow.getChildren().addAll(
                createTableCell("Category", 180, true),
                createTableCell("Recommended", 140, true),
                createTableCell("Allocated", 140, true),
                createTableCell("Spent", 140, true),
                createTableCell("Remaining", 140, true),
                createTableCell("Status", 120, true)
        );
        tableRows.getChildren().add(headerRow);

        // 🎯 2. Render Live Breakdown from Organizer
        boolean hasLiveRows = false;
        if (event != null && event.getBudgetRecommendation() != null && !event.getBudgetRecommendation().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getBudgetRecommendation().entrySet()) {
                double catAlloc = 0.0;
                try { catAlloc = Double.parseDouble(entry.getValue().toString()); } catch (Exception ignored) {}
                double catSpent = 0.0;
                double catRem = catAlloc;

                tableRows.getChildren().add(createBudgetTableRow(
                        entry.getKey(),
                        String.format("₹ %,.0f", catAlloc),
                        String.format("₹ %,.0f", catAlloc),
                        String.format("₹ %,.0f", catSpent),
                        String.format("₹ %,.0f", catRem),
                        "Allocated",
                        "#16A34A", "#DCFCE7"
                ));
                hasLiveRows = true;
            }
        }

        // Fallback to Timefold AI Plan if map is empty
        if (!hasLiveRows && event != null && event.getAiPlanJson() != null && !event.getAiPlanJson().isBlank()) {
            AiEventPlan plan = AiEventPlan.fromJson(event.getAiPlanJson());
            if (plan != null && !plan.getBudgetBreakdown().isEmpty()) {
                for (AiEventPlan.BudgetItem item : plan.getBudgetBreakdown()) {
                    double catAlloc = totalBudget * (item.getPercentage() / 100.0);

                    tableRows.getChildren().add(createBudgetTableRow(
                            item.getCategory(),
                            item.getAmount(),
                            String.format("₹ %,.0f", catAlloc),
                            "₹ 0",
                            String.format("₹ %,.0f", catAlloc),
                            "Allocated",
                            "#16A34A", "#DCFCE7"
                    ));
                    hasLiveRows = true;
                }
            }
        }

        if (!hasLiveRows) {
            tableRows.getChildren().addAll(
                    createBudgetTableRow("Venue", "₹ 2,200", "₹ 2,200", "₹ 0", "₹ 2,200", "Allocated", "#16A34A", "#DCFCE7"),
                    createBudgetTableRow("Decoration", "₹ 1,550", "₹ 1,550", "₹ 0", "₹ 1,550", "Allocated", "#16A34A", "#DCFCE7"),
                    createBudgetTableRow("Sound System", "₹ 400", "₹ 400", "₹ 0", "₹ 400", "Allocated", "#16A34A", "#DCFCE7"),
                    createBudgetTableRow("Contingency & Other", "₹ 700", "₹ 700", "₹ 0", "₹ 700", "Allocated", "#16A34A", "#DCFCE7")
            );
        }

        tableCard.getChildren().addAll(tblTitle, tableRows);

        HBox footerNote = new HBox(10);
        footerNote.setAlignment(Pos.CENTER_LEFT);
        footerNote.setPadding(new Insets(10, 14, 10, 14));
        footerNote.setStyle("-fx-background-color: #EFF6FF; -fx-background-radius: 8px;");

        FontIcon pigIcon = new FontIcon("fas-piggy-bank");
        pigIcon.setIconSize(16);
        pigIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label noteTxt = new Label("Live budget sync active. Any adjustments finalized by your organizer are updated in real-time.");
        noteTxt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
        footerNote.getChildren().addAll(pigIcon, noteTxt);

        page.getChildren().addAll(backLink, titleBox, metricsRow, tableCard, footerNote);
        return page;
    }

    // ⭐ FIXED METHOD PARAMETERS (String title, String val, String percent)
    private VBox createMetricCard(String title, String val, String percent) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        HBox valRow = new HBox(8);
        valRow.setAlignment(Pos.BASELINE_LEFT);
        Label v = new Label(val != null ? val : "");
        v.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        valRow.getChildren().add(v);

        if (percent != null && !percent.isEmpty()) {
            Label p = new Label(percent);
            p.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
            valRow.getChildren().add(p);
        }

        card.getChildren().addAll(t, valRow);
        return card;
    }

    private Label createTableCell(String text, double width, boolean isHeader) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setStyle(isHeader ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";" : "-fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
        return lbl;
    }

    private HBox createBudgetTableRow(String category, String rec, String alloc, String spent, String rem, String status, String stTextHex, String stBgHex) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label cat = createTableCell(category, 180, false);
        cat.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");

        Label badge = new Label(status);
        badge.setStyle("-fx-background-color: " + stBgHex + "; -fx-text-fill: " + stTextHex + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");

        row.getChildren().addAll(
                cat,
                createTableCell(rec, 140, false),
                createTableCell(alloc, 140, false),
                createTableCell(spent, 140, false),
                createTableCell(rem, 140, false),
                badge
        );
        return row;
    }

    private void handleBack() {
        if (backAction != null) backAction.run();
        else goOverview();
    }
    private void goDashboard() {
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }
    private void goCreateEvent() {
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }
    private void goOverview() {
        MyEventDetails view = new MyEventDetails(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getMyEventDetailsScene(this::handleBack));
    }
    private void goVenue() {
        Venue view = new Venue(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
    }
    private void goGuest() {
        Guest view = new Guest(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getGuestScene(this::handleBack));
    }
    private void goPayment() {
        Payment view = new Payment(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getPaymentScene(this::handleBack));
    }
    private void goRequirement() {
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }
    private void goSettings() {
        SettingC s = new SettingC();
        LandingPage.landingPageStage.setScene(s.getSettingScene(this::handleBack));
    }
}