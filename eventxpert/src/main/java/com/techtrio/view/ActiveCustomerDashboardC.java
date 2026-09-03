package com.techtrio.view;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.techtrio.controller.CustomerController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.WeatherData;
import com.techtrio.services.WeatherService;
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
import javafx.scene.shape.Circle;

public class ActiveCustomerDashboardC {

    private Scene activeCustomerScene;
    private String uid;
    private String idToken;

    // BRAND PALETTE
    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    // KPI Labels
    private Label totalEventsValLabel;
    private Label upcomingEventsValLabel;
    private Label completedEventsValLabel;
    private Label totalSpentValLabel;

    // Header & Profile
    private Label welcomeHeadingLabel;

    // Upcoming Event UI
    private Label upcomingEventNameLabel;
    private Label upcomingEventDateLabel;
    private Label upcomingEventTimeLabel;
    private Label upcomingEventLocationLabel;
    private Label upcomingEventGuestsLabel;
    private Label upcomingEventStatusBadge;
    private Button viewUpcomingEventBtn;
    private Event latestUpcomingEvent = null;

    // Budget Overview UI
    private Label budgetOverviewAmountLabel;
    private ProgressBar budgetOverviewProgressBar;
    private Label budgetOverviewProgressPercent;
    private Label budgetAllocatedValLabel;
    private Label budgetRemainingValLabel;

    // Weather UI
    private Label weatherLocationLabel;
    private Label weatherTempLabel;
    private Label weatherConditionLabel;
    private Label weatherHumidityLabel;
    private Label weatherWindLabel;
    private Label weatherFeelsLikeLabel;
    private Label weatherAlertTextLabel;

    // Calendar Navigation
    private YearMonth currentCalendarMonth = YearMonth.of(2024, 5);
    private Label calendarMonthLabel;
    private GridPane calendarGrid;

    public ActiveCustomerDashboardC() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ActiveCustomerDashboardC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }

    public Scene getactiveCustomerScene(Runnable callBackAction1) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Sidebar on Left
        root.setLeft(createSidebar());

        // 2. Main Scrollable Dashboard Content (Top bar removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createMainDashboardContent();
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadCustomerDashboardDataAsync();

        activeCustomerScene = new Scene(root, 1550, 785);
        return activeCustomerScene;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(6, 10, 14, 10));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label brandSub = new Label("Plan. Manage. Celebrate.");
        brandSub.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px;");
        brandTexts.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox navList = new VBox(5);
        navList.getChildren().addAll(
                createSidebarItem("Dashboard", "fas-tachometer-alt", true, null),
                createSidebarItem("Create Event", "fas-plus-circle", false, this::openCreateEvent),
                createSidebarItem("My Events", "fas-calendar-check", false, this::openMyEvents),
                createSidebarItem("Notifications", "fas-bell", false, this::openNotifications),
                createSidebarItem("Profile", "fas-user", false, this::openProfile),
                createSidebarItem("Settings", "fas-cog", false, this::openSettings)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, navList, spacer);
        return sidebar;
    }

    private Button createSidebarItem(String title, String iconLiteral, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(38);
        btn.setPadding(new Insets(0, 12, 0, 12));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill:" + NAVY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 7px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 7px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E2E8F0; -fx-font-size: 12px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createMainDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 32, 32, 32));

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        welcomeHeadingLabel = new Label("Welcome Message! 👋");
        welcomeHeadingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        Label subtitle = new Label("Let’s plan your next event.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill:" + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(welcomeHeadingLabel, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button createEventBtn = new Button(" Create Event");
        FontIcon plusIcon = new FontIcon("fas-plus");
        plusIcon.setIconSize(12);
        plusIcon.setIconColor(Color.WHITE);
        createEventBtn.setGraphic(plusIcon);
        createEventBtn.setStyle("-fx-background-color:" + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 9 18; -fx-background-radius: 8px; -fx-cursor: hand;");
        createEventBtn.setOnAction(e -> openCreateEvent());

        headerRow.getChildren().addAll(titleBox, headerSpacer, createEventBtn);

        HBox kpiRow = createKpiRow();

        HBox mainGrid = new HBox(20);

        VBox leftColumn = new VBox(20);
        leftColumn.getChildren().addAll(
                createUpcomingEventCard(),
                createCalendarCard()
        );
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(420);
        rightColumn.setMinWidth(400);

        rightColumn.getChildren().addAll(
                createBudgetOverviewCard(),
                createWeatherAlertCard()
        );

        mainGrid.getChildren().addAll(leftColumn, rightColumn);
        content.getChildren().addAll(headerRow, kpiRow, mainGrid);
        return content;
    }

    private HBox createKpiRow() {
        HBox row = new HBox(16);

        totalEventsValLabel = new Label("0");
        upcomingEventsValLabel = new Label("0");
        completedEventsValLabel = new Label("0");
        totalSpentValLabel = new Label("₹0");

        row.getChildren().addAll(
                createKpiCard("My Events", totalEventsValLabel, "Total events", "fas-calendar-alt", "#0284C7"),
                createKpiCard("Upcoming", upcomingEventsValLabel, "Next 30 days", "fas-calendar-check", "#4F46E5"),
                createKpiCard("Completed", completedEventsValLabel, "Events done", "fas-check-circle", "#16A34A"),
                createKpiCard("Total spent", totalSpentValLabel, "All events", "fas-wallet", "#0D3979")
        );
        return row;
    }

    private VBox createKpiCard(String title, Label valLbl, String subtext, String iconLiteral, String iconColorHex) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconHolder = new StackPane();
        Circle c = new Circle(20, Color.web(iconColorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon fontIcon = new FontIcon(iconLiteral);
        fontIcon.setIconSize(18);
        fontIcon.setIconColor(Color.web(iconColorHex));
        iconHolder.getChildren().addAll(c, fontIcon);

        VBox texts = new VBox(1);
        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");

        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        texts.getChildren().addAll(tLbl, valLbl);

        topRow.getChildren().addAll(iconHolder, texts);

        Label subLbl = new Label(subtext);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_MUTED + ";");

        card.getChildren().addAll(topRow, subLbl);
        return card;
    }

    private VBox createUpcomingEventCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon calIcon = new FontIcon("fas-calendar-plus");
        calIcon.setIconSize(16);
        calIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label title = new Label("Upcoming Event");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink viewAllLink = new Hyperlink("View All →");
        viewAllLink.setStyle("-fx-text-fill:" + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        viewAllLink.setOnAction(e -> openMyEvents());

        header.getChildren().addAll(calIcon, title, spacer, viewAllLink);

        HBox bodyRow = new HBox(16);
        bodyRow.setAlignment(Pos.CENTER_LEFT);

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(170, 125);
        imgBox.setMinSize(170, 125);
        imgBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #0F172A 0%, #1E293B 100%); -fx-background-radius: 8px;");

        FontIcon stageIcon = new FontIcon("fas-theater-masks");
        stageIcon.setIconSize(38);
        stageIcon.setIconColor(Color.web("#94A3B8"));
        imgBox.getChildren().add(stageIcon);

        VBox detailsCol = new VBox(5);
        HBox.setHgrow(detailsCol, Priority.ALWAYS);

        upcomingEventNameLabel = new Label("Loading latest event...");
        upcomingEventNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");

        upcomingEventDateLabel = new Label("📅 Date TBD");
        upcomingEventDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");

        upcomingEventTimeLabel = new Label("🕒 10:00 AM");
        upcomingEventTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");

        upcomingEventLocationLabel = new Label("📍 Pune, Maharashtra");
        upcomingEventLocationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");

        upcomingEventGuestsLabel = new Label("👥 100 Guests");
        upcomingEventGuestsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + TEXT_MUTED + ";");

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        actionRow.setPadding(new Insets(4, 0, 0, 0));

        upcomingEventStatusBadge = new Label("Status : Planning");
        upcomingEventStatusBadge.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0284C7; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-font-size: 11px; -fx-font-weight: bold;");

        Region actSpace = new Region();
        HBox.setHgrow(actSpace, Priority.ALWAYS);

        viewUpcomingEventBtn = new Button("View Event →");
        viewUpcomingEventBtn.setStyle("-fx-background-color:" + NAVY + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        viewUpcomingEventBtn.setOnAction(e -> {
            if (latestUpcomingEvent != null) {
                openEventDetails(latestUpcomingEvent);
            } else {
                openMyEvents();
            }
        });

        actionRow.getChildren().addAll(upcomingEventStatusBadge, actSpace, viewUpcomingEventBtn);

        detailsCol.getChildren().addAll(
                upcomingEventNameLabel,
                upcomingEventDateLabel,
                upcomingEventTimeLabel,
                upcomingEventLocationLabel,
                upcomingEventGuestsLabel,
                actionRow
        );

        bodyRow.getChildren().addAll(imgBox, detailsCol);
        card.getChildren().addAll(header, bodyRow);
        return card;
    }

    private VBox createCalendarCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon calIcon = new FontIcon("fas-calendar-alt");
        calIcon.setIconSize(15);
        calIcon.setIconColor(Color.web(PRIMARY_BLUE));

        Label title = new Label("My Calendar");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);

        HBox monthNav = new HBox(8);
        monthNav.setAlignment(Pos.CENTER);
        Label prev = new Label("‹");
        prev.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");
        prev.setOnMouseClicked(e -> {
            currentCalendarMonth = currentCalendarMonth.minusMonths(1);
            renderCalendarTable();
        });

        calendarMonthLabel = new Label("May 2024");
        calendarMonthLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");

        Label next = new Label("›");
        next.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");
        next.setOnMouseClicked(e -> {
            currentCalendarMonth = currentCalendarMonth.plusMonths(1);
            renderCalendarTable();
        });

        monthNav.getChildren().addAll(prev, calendarMonthLabel, next);

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Hyperlink viewAll = new Hyperlink("View All →");
        viewAll.setStyle("-fx-text-fill:" + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        viewAll.setOnAction(e -> openMyEvents());

        header.getChildren().addAll(calIcon, title, sp1, monthNav, sp2, viewAll);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(8);
        calendarGrid.setVgap(6);
        calendarGrid.setAlignment(Pos.CENTER);

        renderCalendarTable();

        HBox legend = new HBox(16);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(6, 0, 0, 0));

        HBox leg1 = new HBox(6, new Circle(4, Color.web(NAVY)), new Label("Upcoming Event"));
        HBox leg2 = new HBox(6, new Circle(4, Color.web("#38BDF8")), new Label("Completed Event"));
        leg1.setStyle("-fx-font-size: 10px; -fx-text-fill:" + TEXT_MUTED + ";");
        leg2.setStyle("-fx-font-size: 10px; -fx-text-fill:" + TEXT_MUTED + ";");
        legend.getChildren().addAll(leg1, leg2);

        card.getChildren().addAll(header, calendarGrid, legend);
        return card;
    }

    private void renderCalendarTable() {
        if (calendarGrid == null) return;
        calendarGrid.getChildren().clear();

        calendarMonthLabel.setText(currentCalendarMonth.getMonth().name().substring(0, 1)
                + currentCalendarMonth.getMonth().name().substring(1).toLowerCase() + " " + currentCalendarMonth.getYear());

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLbl = new Label(days[i]);
            dayLbl.setPrefWidth(44);
            dayLbl.setAlignment(Pos.CENTER);
            dayLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");
            calendarGrid.add(dayLbl, i, 0);
        }

        LocalDate firstOfMonth = currentCalendarMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentCalendarMonth.lengthOfMonth();

        LocalDate prevMonth = currentCalendarMonth.minusMonths(1).atEndOfMonth();
        int prevMonthDays = prevMonth.getDayOfMonth();

        int cell = 0;
        int row = 1;

        for (int i = dayOfWeek - 1; i >= 0; i--) {
            VBox box = createDateCell(String.valueOf(prevMonthDays - i), true, false, false);
            calendarGrid.add(box, cell++, row);
        }

        for (int d = 1; d <= daysInMonth; d++) {
            boolean isHighlighted = (d == 15);
            boolean isDot = (d == 22);

            VBox box = createDateCell(String.valueOf(d), false, isHighlighted, isDot);
            calendarGrid.add(box, cell++, row);

            if (cell == 7) {
                cell = 0;
                row++;
            }
        }

        int nextDay = 1;
        while (cell < 7 && row <= 6) {
            VBox box = createDateCell(String.valueOf(nextDay++), true, false, false);
            calendarGrid.add(box, cell++, row);
        }
    }

    private VBox createDateCell(String text, boolean isOtherMonth, boolean isHighlighted, boolean hasDot) {
        VBox dateCell = new VBox(2);
        dateCell.setAlignment(Pos.CENTER);
        dateCell.setPrefSize(44, 28);

        Label dLbl = new Label(text);
        if (isOtherMonth) {
            dLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #CBD5E1;");
        } else if (isHighlighted) {
            dateCell.setStyle("-fx-background-color: " + NAVY + "; -fx-background-radius: 14px;");
            dLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
        } else {
            dLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_DARK + ";");
        }

        dateCell.getChildren().add(dLbl);

        if (hasDot && !isHighlighted) {
            Circle dot = new Circle(3, Color.web("#38BDF8"));
            dateCell.getChildren().add(dot);
        }

        return dateCell;
    }

    private VBox createBudgetOverviewCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconHolder = new StackPane();
        Circle c = new Circle(14, Color.web(PRIMARY_BLUE));
        FontIcon rupeeIcon = new FontIcon("fas-rupee-sign");
        rupeeIcon.setIconSize(12);
        rupeeIcon.setIconColor(Color.WHITE);
        iconHolder.getChildren().addAll(c, rupeeIcon);

        Label title = new Label("Budget Overview");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink viewDetailsLink = new Hyperlink("View details →");
        viewDetailsLink.setStyle("-fx-text-fill:" + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        viewDetailsLink.setOnAction(e -> {
            if (latestUpcomingEvent != null) {
                openEventDetails(latestUpcomingEvent);
            } else {
                openMyEvents();
            }
        });

        header.getChildren().addAll(iconHolder, title, spacer, viewDetailsLink);

        VBox totalBox = new VBox(1);
        Label curBudgetLbl = new Label("Current Event Budget");
        curBudgetLbl.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_MUTED + ";");
        budgetOverviewAmountLabel = new Label("₹50,000");
        budgetOverviewAmountLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        totalBox.getChildren().addAll(curBudgetLbl, budgetOverviewAmountLabel);

        HBox progressRow = new HBox(8);
        progressRow.setAlignment(Pos.CENTER_LEFT);
        budgetOverviewProgressBar = new ProgressBar(0.84);
        budgetOverviewProgressBar.setMaxWidth(Double.MAX_VALUE);
        budgetOverviewProgressBar.setPrefHeight(8);
        budgetOverviewProgressBar.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");
        HBox.setHgrow(budgetOverviewProgressBar, Priority.ALWAYS);

        budgetOverviewProgressPercent = new Label("84%");
        budgetOverviewProgressPercent.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill:" + PRIMARY_BLUE + ";");
        progressRow.getChildren().addAll(budgetOverviewProgressBar, budgetOverviewProgressPercent);

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.setPadding(new Insets(6, 0, 0, 0));

        HBox allocPill = new HBox(8);
        allocPill.setAlignment(Pos.CENTER_LEFT);
        FontIcon coinsIcon = new FontIcon("fas-coins");
        coinsIcon.setIconSize(15);
        coinsIcon.setIconColor(Color.web(PRIMARY_BLUE));
        VBox allocText = new VBox(1);
        Label allocTitle = new Label("Allocated");
        allocTitle.setStyle("-fx-font-size: 10px; -fx-text-fill:" + TEXT_MUTED + ";");
        budgetAllocatedValLabel = new Label("₹42,000");
        budgetAllocatedValLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        allocText.getChildren().addAll(allocTitle, budgetAllocatedValLabel);
        allocPill.getChildren().addAll(coinsIcon, allocText);

        HBox remPill = new HBox(8);
        remPill.setAlignment(Pos.CENTER_LEFT);
        FontIcon walletIcon = new FontIcon("fas-wallet");
        walletIcon.setIconSize(15);
        walletIcon.setIconColor(Color.web("#16A34A"));
        VBox remText = new VBox(1);
        Label remTitle = new Label("Remaining");
        remTitle.setStyle("-fx-font-size: 10px; -fx-text-fill:" + TEXT_MUTED + ";");
        budgetRemainingValLabel = new Label("₹8,000");
        budgetRemainingValLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        remText.getChildren().addAll(remTitle, budgetRemainingValLabel);
        remPill.getChildren().addAll(walletIcon, remText);

        statsRow.getChildren().addAll(allocPill, remPill);

        card.getChildren().addAll(header, totalBox, progressRow, statsRow);
        return card;
    }

    private VBox createWeatherAlertCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon cloudSunIcon = new FontIcon("fas-cloud-sun");
        cloudSunIcon.setIconSize(20);
        cloudSunIcon.setIconColor(Color.web("#F59E0B"));

        VBox titleBox = new VBox(1);
        Label title = new Label("Weather Alert");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        weatherLocationLabel = new Label("📍 Pune, Maharashtra");
        weatherLocationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(title, weatherLocationLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon sunGraphic = new FontIcon("fas-sun");
        sunGraphic.setIconSize(24);
        sunGraphic.setIconColor(Color.web("#FBBF24"));

        header.getChildren().addAll(cloudSunIcon, titleBox, spacer, sunGraphic);

        HBox tempRow = new HBox(10);
        tempRow.setAlignment(Pos.BASELINE_LEFT);
        weatherTempLabel = new Label("32°C");
        weatherTempLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill:" + NAVY + ";");
        weatherConditionLabel = new Label("Partly Cloudy");
        weatherConditionLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_MUTED + ";");
        tempRow.getChildren().addAll(weatherTempLabel, weatherConditionLabel);

        HBox metricsRow = new HBox(14);
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        weatherHumidityLabel = new Label("60%");
        weatherWindLabel = new Label("12 km/h");
        weatherFeelsLikeLabel = new Label("34°C");

        metricsRow.getChildren().addAll(
                createWeatherMetric("fas-tint", "#0284C7", "Humidity", weatherHumidityLabel),
                createWeatherMetric("fas-wind", "#0D3979", "Wind", weatherWindLabel),
                createWeatherMetric("fas-thermometer-half", "#DC2626", "Feels like", weatherFeelsLikeLabel)
        );

        HBox alertBanner = new HBox(8);
        alertBanner.setAlignment(Pos.CENTER_LEFT);
        alertBanner.setPadding(new Insets(8, 12, 8, 12));
        alertBanner.setStyle("-fx-background-color: #F8FAFC; -fx-border-color:" + BORDER + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        FontIcon infoIco = new FontIcon("fas-exclamation-triangle");
        infoIco.setIconSize(12);
        infoIco.setIconColor(Color.web(NAVY));

        weatherAlertTextLabel = new Label("No alerts for today. Enjoy your day!");
        weatherAlertTextLabel.setStyle("-fx-font-size: 11px; -fx-text-fill:" + TEXT_DARK + ";");
        alertBanner.getChildren().addAll(infoIco, weatherAlertTextLabel);

        card.getChildren().addAll(header, tempRow, metricsRow, alertBanner);
        return card;
    }

    private HBox createWeatherMetric(String iconLit, String colorHex, String labelText, Label valLabel) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(colorHex));

        VBox texts = new VBox(0);
        Label l = new Label(labelText);
        l.setStyle("-fx-font-size: 9px; -fx-text-fill:" + TEXT_MUTED + ";");
        valLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill:" + TEXT_DARK + ";");
        texts.getChildren().addAll(l, valLabel);

        item.getChildren().addAll(icon, texts);
        return item;
    }

    private void loadCustomerDashboardDataAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                CustomerController controller = new CustomerController();
                CustomerProfile profile = controller.getCustomerProfile(uid);

                if (profile != null) {
                    Platform.runLater(() -> {
                        String name = profile.getName() != null && !profile.getName().isBlank() ? profile.getName() : "Customer";
                        welcomeHeadingLabel.setText("Welcome back, " + name + "! 👋");
                    });
                }
                return controller.getEventsByCustomerId(uid);
            }
        };

        task.setOnSucceeded(e -> {
            List<Event> events = task.getValue();
            Platform.runLater(() -> renderDynamicDashboardData(events));
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderDynamicDashboardData(List<Event> events) {
        if (events == null || events.isEmpty()) {
            totalEventsValLabel.setText("0");
            upcomingEventsValLabel.setText("0");
            completedEventsValLabel.setText("0");
            totalSpentValLabel.setText("₹0");
            upcomingEventNameLabel.setText("No active events");
            upcomingEventDateLabel.setText("Click '+ Create Event' to begin");
            return;
        }

        totalEventsValLabel.setText(String.valueOf(events.size()));

        List<Event> sortedEvents = new ArrayList<>(events);
        sortedEvents.sort((a, b) -> {
            String idA = a.getEventId() != null ? a.getEventId() : "";
            String idB = b.getEventId() != null ? b.getEventId() : "";
            return idB.compareTo(idA);
        });

        latestUpcomingEvent = sortedEvents.get(0);

        int upcomingCount = 0;
        int completedCount = 0;
        double totalSpent = 0;

        for (Event ev : events) {
            String st = ev.getStatus() != null ? ev.getStatus().toUpperCase() : "PLANNING";
            if ("COMPLETED".equals(st)) {
                completedCount++;
            } else {
                upcomingCount++;
            }

            double budgetAmt = 0;
            try {
                String clean = ev.getBudget() != null ? ev.getBudget().replaceAll("[^0-9.]", "") : "0";
                if (!clean.isEmpty()) budgetAmt = Double.parseDouble(clean);
            } catch (Exception ignored) {}
            totalSpent += budgetAmt;
        }

        upcomingEventsValLabel.setText(String.valueOf(upcomingCount));
        completedEventsValLabel.setText(String.valueOf(completedCount));
        totalSpentValLabel.setText("₹" + String.format("%,.0f", totalSpent));

        if (latestUpcomingEvent != null) {
            String name = latestUpcomingEvent.getEventName() != null ? latestUpcomingEvent.getEventName() : "College Seminar";
            String date = latestUpcomingEvent.getEventDate() != null ? latestUpcomingEvent.getEventDate() : "15 May 2024";
            String time = latestUpcomingEvent.getTime() != null ? latestUpcomingEvent.getTime() : "10:00 AM";
            String loc = latestUpcomingEvent.getCity() != null ? latestUpcomingEvent.getCity() + ", Maharashtra" : "Pune, Maharashtra";
            int guests = latestUpcomingEvent.getGuests() > 0 ? latestUpcomingEvent.getGuests() : 100;
            String status = latestUpcomingEvent.getStatus() != null ? latestUpcomingEvent.getStatus().toUpperCase() : "PLANNING";

            upcomingEventNameLabel.setText(name);
            upcomingEventDateLabel.setText("📅 " + date);
            upcomingEventTimeLabel.setText("🕒 " + time);
            upcomingEventLocationLabel.setText("📍 " + loc);
            upcomingEventGuestsLabel.setText("👥 " + guests + " Guests");
            upcomingEventStatusBadge.setText("Status : " + status);

            // Live Real-Time Budget Overview Binding with Organizer's Saved Breakdown
            double eventBudget = 5000.0;
            try {
                String clean = latestUpcomingEvent.getBudget() != null ? latestUpcomingEvent.getBudget().replaceAll("[^0-9.]", "") : "5000";
                if (!clean.isEmpty()) eventBudget = Double.parseDouble(clean);
            } catch (Exception ignored) {}

            double allocated = 0.0;
            if (latestUpcomingEvent.getBudgetRecommendation() != null && !latestUpcomingEvent.getBudgetRecommendation().isEmpty()) {
                for (Object val : latestUpcomingEvent.getBudgetRecommendation().values()) {
                    try { allocated += Double.parseDouble(val.toString()); } catch (Exception ignored) {}
                }
            } else if (latestUpcomingEvent.getAllocatedBudget() > 0) {
                allocated = latestUpcomingEvent.getAllocatedBudget();
            } else {
                allocated = eventBudget * 0.97;
            }

            double remaining = Math.max(0, eventBudget - allocated);
            double progress = eventBudget > 0 ? Math.min(1.0, allocated / eventBudget) : 0.0;

            budgetOverviewAmountLabel.setText("₹ " + String.format("%,.0f", eventBudget));
            budgetAllocatedValLabel.setText("₹ " + String.format("%,.0f", allocated));
            budgetRemainingValLabel.setText("₹ " + String.format("%,.0f", remaining));
            budgetOverviewProgressBar.setProgress(progress);
            if (budgetOverviewProgressPercent != null) {
                budgetOverviewProgressPercent.setText(Math.round(progress * 100) + "%");
            }

            fetchWeatherForDashboard(latestUpcomingEvent.getCity(), date);
        }
    }

    private void fetchWeatherForDashboard(String city, String date) {
        new Thread(() -> {
            WeatherService service = new WeatherService();
            WeatherData data = service.getWeatherForEventDate(city, date);

            Platform.runLater(() -> {
                if (data != null && weatherTempLabel != null) {
                    String cleanCity = (city != null && !city.isBlank()) ? city : "Pune";
                    weatherLocationLabel.setText("📍 " + cleanCity + ", Maharashtra");

                    if (data.getTemperature() != 0) {
                        weatherTempLabel.setText(data.getTemperature() + "°C");
                    }
                    weatherConditionLabel.setText(data.getAlertMessage());
                    weatherAlertTextLabel.setText("✓ " + data.getAlertMessage());
                }
            });
        }).start();
    }

    private void openCreateEvent() {
        CreateEventC createView = new CreateEventC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(createView.getCreateEventScene(this::returnToDashboard));
        }
    }

    private void openMyEvents() {
        MyEvents myEventsPage = new MyEvents(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(myEventsPage.getMyEventsScene(this::returnToDashboard));
        }
    }

    private void openEventDetails(Event selectedEvent) {
        MyEventDetails detailsPage = new MyEventDetails(uid, idToken, selectedEvent);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(detailsPage.getMyEventDetailsScene(this::returnToDashboard));
        }
    }

    private void openNotifications() {
        Notification notifPage = new Notification();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(notifPage.getNotificationScene());
        }
    }

    private void openProfile() {
        ProfilePageC profilePage = new ProfilePageC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
        }
    }

    private void openSettings() {
        SettingC settingPage = new SettingC();
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
        }
    }

    private void returnToDashboard() {
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        if (LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
        }
    }
}