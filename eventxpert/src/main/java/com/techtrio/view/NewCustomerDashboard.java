package com.techtrio.view;

import com.techtrio.model.Event;
import com.techtrio.services.EventService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NewCustomerDashboard {

    private Scene newCustomerScene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String SECONDARY_TEXT = "#64748B";

    private final EventService eventService = new EventService();
    private List<Event> customerEvents = new ArrayList<>();

    private YearMonth currentYearMonth = YearMonth.now();
    private Label monthLabel;
    private GridPane calendarGrid;

    public NewCustomerDashboard() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public NewCustomerDashboard(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }

    public Scene getnewCustomerScene(Runnable callBackAction1) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Sidebar
        root.setLeft(createCustomerSidebar());

        // 2. Main Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Header
        Label welcome = new Label("Welcome to EventXpert! 👋");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        welcome.setTextFill(Color.web(NAVY));

        Label subtitle = new Label("Let's plan your first event seamlessly with AI constraint planning, verified venues and trusted vendors.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox heading = new VBox(4, welcome, subtitle);

        // Top Action Cards
        HBox topCards = new HBox(18);

        // Plan First Event Card
        VBox firstEvent = new VBox(12);
        firstEvent.setPadding(new Insets(20));
        firstEvent.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(firstEvent, Priority.ALWAYS);

        Label firstTitle = new Label("Plan Your First Event");
        firstTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        firstTitle.setTextFill(Color.web(NAVY));

        Label firstDescription = new Label("Create a customized event, specify guests, budget, and let our verified organizers handle venue & vendor coordination.");
        firstDescription.setWrapText(true);
        firstDescription.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button firstButton = new Button("+ Create Your First Event");
        firstButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6px; -fx-cursor: hand;");
        firstButton.setOnAction(e -> openCreateEvent());

        firstEvent.getChildren().addAll(firstTitle, firstDescription, firstButton);

        // AI Smarter Card
        VBox aiCard = new VBox(12);
        aiCard.setPadding(new Insets(20));
        aiCard.setPrefWidth(320);
        aiCard.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 10px;");

        Label aiTitle = new Label("✦ Timefold AI Planning");
        aiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        aiTitle.setTextFill(Color.WHITE);

        Label aiText = new Label("Our constraint solver creates optimal budget breakdowns and vendor timelines tailored to your event type.");
        aiText.setWrapText(true);
        aiText.setStyle("-fx-font-size: 12px; -fx-text-fill: #E0E7FF;");

        Button aiButton = new Button("Try AI Planner →");
        aiButton.setStyle("-fx-background-color: white; -fx-text-fill: " + NAVY + "; -fx-font-weight: bold; -fx-padding: 7 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        aiButton.setOnAction(e -> openCreateEvent());

        aiCard.getChildren().addAll(aiTitle, aiText, aiButton);
        topCards.getChildren().addAll(firstEvent, aiCard);

        // Category Exploration Row
        Label exploreTitle = new Label("Explore Event Packages");
        exploreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        exploreTitle.setTextFill(Color.web(NAVY));

        HBox categoryGrid = new HBox(14);
        categoryGrid.getChildren().addAll(
                createEventCard("fas-birthday-cake", "Birthday Party", "Venue, decor, catering & cakes", this::openCreateEvent),
                createEventCard("fas-building", "Corporate Event", "Seminars, AV, catering & conferences", this::openCreateEvent),
                createEventCard("fas-graduation-cap", "Educational Meet", "Workshops, training & seminars", this::openCreateEvent),
                createEventCard("fas-users", "Family Gathering", "Reunions, celebrations & dining", this::openCreateEvent)
        );

        // Dynamic Calendar Section
        VBox calendarCard = createCustomerCalendarContainer();

        content.getChildren().addAll(heading, topCards, exploreTitle, categoryGrid, calendarCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        root.setCenter(scroll);

        // Initial calendar render and async data fetch
        renderCalendarMonth();
        loadCustomerEventsAsync();

        newCustomerScene = new Scene(root, 1550, 785);
        return newCustomerScene;
    }

    private VBox createCustomerCalendarContainer() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Calendar Header
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label calTitle = new Label("My Events Calendar");
        calTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        calTitle.setTextFill(Color.web(NAVY));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button todayBtn = new Button("Today");
        todayBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 11px;");
        todayBtn.setOnAction(e -> {
            currentYearMonth = YearMonth.now();
            renderCalendarMonth();
        });

        Button prevBtn = new Button("‹");
        prevBtn.setStyle("-fx-background-color: #F1F5F9; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 3 10;");
        prevBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            renderCalendarMonth();
        });

        Button nextBtn = new Button("›");
        nextBtn.setStyle("-fx-background-color: #F1F5F9; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 3 10;");
        nextBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            renderCalendarMonth();
        });

        monthLabel = new Label();
        monthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        monthLabel.setTextFill(Color.web(NAVY));

        topRow.getChildren().addAll(calTitle, sp, todayBtn, prevBtn, nextBtn, monthLabel);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(6);
        calendarGrid.setVgap(6);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            calendarGrid.getColumnConstraints().add(cc);
        }

        box.getChildren().addAll(topRow, calendarGrid);
        return box;
    }

    private void renderCalendarMonth() {
        if (calendarGrid == null) return;
        calendarGrid.getChildren().clear();

        monthLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayHead = new Label(days[i]);
            dayHead.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            dayHead.setTextFill(Color.web("#64748B"));
            dayHead.setAlignment(Pos.CENTER);
            dayHead.setMaxWidth(Double.MAX_VALUE);
            dayHead.setPadding(new Insets(5));
            dayHead.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 4px;");
            calendarGrid.add(dayHead, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = currentYearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        int row = 1;
        int col = dayOfWeekOffset;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate cellDate = currentYearMonth.atDay(day);

            VBox cell = new VBox(4);
            cell.setAlignment(Pos.TOP_CENTER);
            cell.setPadding(new Insets(6, 4, 6, 4));
            cell.setPrefHeight(60);
            boolean isToday = cellDate.equals(today);

            // Find events scheduled on this day
            List<Event> matchedEvents = getEventsOnDate(cellDate);
            boolean hasEvents = !matchedEvents.isEmpty();

            // Cell styling
            if (isToday) {
                cell.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");
            } else if (hasEvents) {
                cell.setStyle("-fx-background-color: #F0FDF4; -fx-border-color: #86EFAC; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;");
            } else {
                cell.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");
            }

            Label dayNum = new Label(String.valueOf(day));
            dayNum.setFont(Font.font("Arial", (isToday || hasEvents) ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            dayNum.setTextFill(Color.web(isToday ? PRIMARY_BLUE : (hasEvents ? "#166534" : "#1E293B")));
            cell.getChildren().add(dayNum);

            // DYNAMIC DOT INDICATOR
            if (hasEvents) {
                HBox dotsBox = new HBox(3);
                dotsBox.setAlignment(Pos.CENTER);

                StringBuilder tipText = new StringBuilder("Events on " + cellDate + ":\n");
                int dotCount = Math.min(matchedEvents.size(), 3);

                for (int i = 0; i < dotCount; i++) {
                    Circle dot = new Circle(3.5, Color.web(PRIMARY_BLUE));
                    dotsBox.getChildren().add(dot);
                }

                for (Event ev : matchedEvents) {
                    String evName = (ev.getEventName() != null && !ev.getEventName().isBlank()) ? ev.getEventName() : "Event";
                    tipText.append("• ").append(evName).append("\n");
                }

                Tooltip tip = new Tooltip(tipText.toString().trim());
                tip.setStyle("-fx-font-size: 11px;");
                Tooltip.install(cell, tip);

                cell.getChildren().add(dotsBox);
            }

            calendarGrid.add(cell, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private List<Event> getEventsOnDate(LocalDate date) {
        List<Event> matched = new ArrayList<>();
        if (customerEvents == null || date == null) return matched;

        for (Event ev : customerEvents) {
            LocalDate d = parseDate(ev.getEventDate());
            if (d == null) d = parseDate(ev.getStartDate());

            if (d != null && d.equals(date)) {
                matched.add(ev);
            }
        }
        return matched;
    }

    private void loadCustomerEventsAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    System.out.println("[CustomerDashboard] Loading events for customer UID: " + uid);
                    customerEvents = fetchCustomerEventsSafely(uid);
                    System.out.println("[CustomerDashboard] Loaded " + (customerEvents != null ? customerEvents.size() : 0) + " events.");

                    Platform.runLater(() -> renderCalendarMonth());
                } catch (Throwable t) {
                    System.err.println("[CustomerDashboard] Error loading events: " + t.getMessage());
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Safely fetches customer events using reflection to prevent compilation issues
     */
    @SuppressWarnings("unchecked")
    private List<Event> fetchCustomerEventsSafely(String uId) {
        if (uId == null || uId.isBlank() || eventService == null) return new ArrayList<>();
        try {
            Class<?> cls = eventService.getClass();
            // 1. Try getEventsByCustomerId(String)
            try {
                Method m = cls.getMethod("getEventsByCustomerId", String.class);
                return (List<Event>) m.invoke(eventService, uId);
            } catch (NoSuchMethodException ignored) {}

            // 2. Try getEventsByUserId(String)
            try {
                Method m = cls.getMethod("getEventsByUserId", String.class);
                return (List<Event>) m.invoke(eventService, uId);
            } catch (NoSuchMethodException ignored) {}

            // 3. Try getEventsByCustomer(String)
            try {
                Method m = cls.getMethod("getEventsByCustomer", String.class);
                return (List<Event>) m.invoke(eventService, uId);
            } catch (NoSuchMethodException ignored) {}

            // 4. Try getEventsByOrganizerId(String) as fallback
            try {
                Method m = cls.getMethod("getEventsByOrganizerId", String.class);
                return (List<Event>) m.invoke(eventService, uId);
            } catch (NoSuchMethodException ignored) {}

        } catch (Exception ex) {
            System.err.println("[CustomerDashboard] Event fetch reflection note: " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        String clean = s.trim();

        if (clean.contains("T")) clean = clean.split("T")[0];
        if (clean.contains(" ")) clean = clean.split(" ")[0];

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        );

        for (DateTimeFormatter dtf : formatters) {
            try {
                return LocalDate.parse(clean, dtf);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private VBox createEventCard(String iconLit, String title, String desc, Runnable onClick) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-cursor: hand;");
        HBox.setHgrow(card, Priority.ALWAYS);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(18);
        icon.setIconColor(Color.web(PRIMARY_BLUE));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web(NAVY));

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button planBtn = new Button("Plan Now →");
        planBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 0;");

        card.getChildren().addAll(icon, t, d, planBtn);
        card.setOnMouseClicked(e -> {
            if (onClick != null) onClick.run();
        });
        return card;
    }

    private VBox createCustomerSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label portal = new Label("CUSTOMER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        menu.getChildren().addAll(
                createSidebarButton("Dashboard", "fas-tachometer-alt", true, () -> {}),
                createSidebarButton("Create Event", "fas-plus-circle", false, this::openCreateEvent),
                createSidebarButton("My Events", "fas-calendar-check", false, this::openCreateEvent),
                createSidebarButton("Notifications", "fas-bell", false, this::openNotifications),
                createSidebarButton("Profile", "fas-user", false, this::openProfile),
                createSidebarButton("Settings", "fas-cog", false, this::openSettings)
        );

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, menu, space);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void openCreateEvent() {
        CreateEventC createEventView = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(createEventView.getCreateEventScene(this::returnToDashboard));
    }

    private void openProfile() {
        ProfilePageC profilePage = new ProfilePageC();
        LandingPage.landingPageStage.setScene(profilePage.getProfileScene(this::returnToDashboard));
    }

    private void openSettings() {
        SettingC settingPage = new SettingC();
        LandingPage.landingPageStage.setScene(settingPage.getSettingScene(this::returnToDashboard));
    }

    private void openNotifications() {
        Notification notificationPage = new Notification();
        LandingPage.landingPageStage.setScene(notificationPage.getNotificationScene());
    }

    private void returnToDashboard() {
        NewCustomerDashboard dash = new NewCustomerDashboard(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getnewCustomerScene(null));
    }
}