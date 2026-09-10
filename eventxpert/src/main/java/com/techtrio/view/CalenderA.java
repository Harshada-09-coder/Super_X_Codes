package com.techtrio.view;

import com.techtrio.controller.AdminController;
import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Conflict;
import com.techtrio.model.Event;
import org.kordamp.ikonli.javafx.FontIcon;

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
import javafx.stage.Stage;

public class CalenderA {
    private Scene calenderScene;

    private final String PRIMARY = "#002A60";
    private final String LIGHT = "#C2E0F4";
    private final String BG = "#F8FAFC";
    private final String BORDER = "#E2E8F0";

    private final AdminController adminController = new AdminController();
    private List<Event> allEvents = new ArrayList<>();
    private List<Conflict> allConflicts = new ArrayList<>();

    private YearMonth currentYearMonth = YearMonth.now();

    private Label monthLabel;
    private GridPane calendarGrid;
    private VBox upcomingEventsBox;

    private Label todayKpiVal;
    private Label weekKpiVal;
    private Label upcomingKpiVal;
    private Label conflictsKpiVal;

    public Scene getcalenderScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createAdminSidebar("Calendar"));

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("Event Calendar");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle = new Label("Live schedule overview, venue timeline and real-time conflict tracking.");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#64748B"));

        VBox titleBox = new VBox(2, title, subtitle);

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        HBox headerRow = new HBox(12, titleBox, topSpace);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        todayKpiVal = new Label("0");
        weekKpiVal = new Label("0");
        upcomingKpiVal = new Label("0");
        conflictsKpiVal = new Label("0");

        HBox kpiCards = new HBox(14);
        kpiCards.getChildren().addAll(
                createKpiCard("TODAY'S SCHEDULE", todayKpiVal, "fas-calendar-day", "#0284C7"),
                createKpiCard("THIS WEEK", weekKpiVal, "fas-calendar-week", "#16A34A"),
                createKpiCard("TOTAL UPCOMING", upcomingKpiVal, "fas-calendar-alt", PRIMARY),
                createKpiCard("ACTIVE CONFLICTS", conflictsKpiVal, "fas-exclamation-triangle", "#DC2626")
        );

        VBox calendarCard = createCalendarContainer();

        VBox upcomingCard = createUpcomingSideCard();
        upcomingCard.setPrefWidth(340);
        upcomingCard.setMinWidth(320);

        HBox calendarLayout = new HBox(18, calendarCard, upcomingCard);
        HBox.setHgrow(calendarCard, Priority.ALWAYS);

        content.getChildren().addAll(headerRow, kpiCards, calendarLayout);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BG + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        loadRealCalendarData();

        calenderScene = new Scene(root, 1550, 785);
        return calenderScene;
    }

    private VBox createCalendarContainer() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        Button prevBtn = new Button("‹");
        prevBtn.setStyle("-fx-background-color: #F1F5F9; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand; -fx-padding: 4 12;");
        prevBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            renderCalendarMonth();
        });

        Button nextBtn = new Button("›");
        nextBtn.setStyle("-fx-background-color: #F1F5F9; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand; -fx-padding: 4 12;");
        nextBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            renderCalendarMonth();
        });

        Button todayBtn = new Button("Today");
        todayBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 4; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 11;");
        todayBtn.setOnAction(e -> {
            currentYearMonth = YearMonth.now();
            renderCalendarMonth();
        });

        monthLabel = new Label();
        monthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        monthLabel.setTextFill(Color.web(PRIMARY));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox toolbar = new HBox(8, todayBtn, prevBtn, nextBtn, monthLabel, sp);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(4);
        calendarGrid.setVgap(4);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            calendarGrid.getColumnConstraints().add(cc);
        }

        box.getChildren().addAll(toolbar, calendarGrid);
        return box;
    }

    // ⭐ Event Completed झाल्यावर बॉक्सला हिरवी बॉर्डर देणारी पद्धत
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
            dayHead.setPadding(new Insets(6));
            dayHead.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 4;");
            calendarGrid.add(dayHead, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        LocalDate today = LocalDate.now();

        int row = 1;
        int col = dayOfWeekOffset;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);

            VBox cell = new VBox(4);
            cell.setPadding(new Insets(6));
            cell.setPrefHeight(75);
            boolean isToday = date.equals(today);

            // या तारखेचे इव्हेंट्स शोधणे
            List<Event> dateEvents = new ArrayList<>();
            boolean hasCompletedEvent = false;

            for (Event ev : allEvents) {
                LocalDate evDate = parseDate(ev.getEventDate());
                if (evDate == null) evDate = parseDate(ev.getStartDate());

                if (evDate != null && evDate.equals(date)) {
                    dateEvents.add(ev);
                    if ("COMPLETED".equalsIgnoreCase(ev.getStatus())) {
                        hasCompletedEvent = true; // ⭐ इव्हेंट पूर्ण झाला आहे का तपासणे
                    }
                }
            }

            // ⭐ बॉर्डर आणि बॅकग्राउंड स्टाइल ठरवणे
            if (hasCompletedEvent) {
                // Event Complete झाल्यावर ठळक हिरवी बॉर्डर!
                cell.setStyle("-fx-background-color: #F0FDF4; -fx-border-color: #16A34A; -fx-border-width: 2px; -fx-border-radius: 6; -fx-background-radius: 6;");
            } else if (isToday) {
                cell.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: " + PRIMARY + "; -fx-border-width: 1.5px; -fx-border-radius: 6; -fx-background-radius: 6;");
            } else {
                cell.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
            }

            Label dayNum = new Label(String.valueOf(day));
            dayNum.setFont(Font.font("Arial", isToday || hasCompletedEvent ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            dayNum.setTextFill(hasCompletedEvent ? Color.web("#16A34A") : (isToday ? Color.web(PRIMARY) : Color.web("#1E293B")));
            cell.getChildren().add(dayNum);

            // तारीख बॉक्समध्ये इव्हेंट टॅग्स जोडणे
            for (Event ev : dateEvents) {
                boolean isEvCompleted = "COMPLETED".equalsIgnoreCase(ev.getStatus());
                String eName = (ev.getEventName() != null ? ev.getEventName() : "Event");

                Label evTag = new Label(isEvCompleted ? "✓ " + eName : eName);
                evTag.setMaxWidth(Double.MAX_VALUE);

                if (isEvCompleted) {
                    evTag.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 3;");
                } else {
                    evTag.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 3;");
                }

                cell.getChildren().add(evTag);
                if (cell.getChildren().size() > 3) break;
            }

            calendarGrid.add(cell, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createUpcomingSideCard() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");

        HBox top = new HBox();
        Label t = new Label("Upcoming Events");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        t.setTextFill(Color.web(PRIMARY));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button viewAll = new Button("View All →");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 11; -fx-cursor: hand;");
        viewAll.setOnAction(e -> {
            Stage stage = (Stage) viewAll.getScene().getWindow();
            NavigationManager.navigate("Events", stage);
        });

        top.getChildren().addAll(t, sp, viewAll);
        upcomingEventsBox = new VBox(10);

        box.getChildren().addAll(top, upcomingEventsBox);
        return box;
    }

    private void loadRealCalendarData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                allEvents = adminController.getAllEvents();
                allConflicts = adminController.getAllConflicts();

                LocalDate today = LocalDate.now();
                LocalDate weekEnd = today.plusDays(7);

                int todayCount = 0;
                int weekCount = 0;
                int upcomingCount = 0;

                List<Event> upcomingList = new ArrayList<>();

                if (allEvents != null) {
                    for (Event ev : allEvents) {
                        LocalDate d = parseDate(ev.getEventDate());
                        if (d == null) d = parseDate(ev.getStartDate());

                        if (d != null) {
                            if (d.equals(today)) todayCount++;
                            if (!d.isBefore(today) && !d.isAfter(weekEnd)) weekCount++;
                            if (!d.isBefore(today)) {
                                upcomingCount++;
                                upcomingList.add(ev);
                            }
                        }
                    }
                }

                int conflictCount = (allConflicts != null) ? allConflicts.size() : 0;
                final int fToday = todayCount;
                final int fWeek = weekCount;
                final int fUpcoming = upcomingCount;
                final int fConflicts = conflictCount;

                Platform.runLater(() -> {
                    todayKpiVal.setText(String.valueOf(fToday));
                    weekKpiVal.setText(String.valueOf(fWeek));
                    upcomingKpiVal.setText(String.valueOf(fUpcoming));
                    conflictsKpiVal.setText(String.valueOf(fConflicts));

                    renderCalendarMonth();
                    renderUpcomingList(upcomingList);
                });

                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void renderUpcomingList(List<Event> list) {
        if (upcomingEventsBox == null) return;
        upcomingEventsBox.getChildren().clear();

        if (list.isEmpty()) {
            Label noEv = new Label("No upcoming events scheduled.");
            noEv.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12;");
            upcomingEventsBox.getChildren().add(noEv);
            return;
        }

        int count = 0;
        for (Event ev : list) {
            String dateText = ev.getEventDate() != null ? ev.getEventDate() : (ev.getStartDate() != null ? ev.getStartDate() : "Upcoming");
            String name = ev.getEventName() != null ? ev.getEventName() : "Event";
            String loc = ev.getCity() != null ? ev.getCity() : "Pune";

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8));
            row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

            StackPane dateBox = new StackPane();
            dateBox.setPrefSize(40, 40);
            dateBox.setStyle("-fx-background-color: #E0E7FF; -fx-background-radius: 6;");
            Label dt = new Label(dateText.length() > 6 ? dateText.substring(5) : dateText);
            dt.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: #4338CA;");
            dateBox.getChildren().add(dt);

            VBox info = new VBox(2);
            Label n = new Label(name);
            n.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY + ";");
            Label l = new Label("📍 " + loc + " • " + ev.getGuests() + " Guests");
            l.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
            info.getChildren().addAll(n, l);

            row.getChildren().addAll(dateBox, info);
            upcomingEventsBox.getChildren().add(row);

            count++;
            if (count >= 5) break;
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            try {
                return LocalDate.parse(s.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (Exception ignored2) {}
        }
        return null;
    }

    private VBox createKpiCard(String title, Label valLbl, String iconLit, String colorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER + "; -fx-border-radius:10; -fx-background-radius:10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(Color.web(colorHex));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        t.setTextFill(Color.web("#64748B"));
        top.getChildren().addAll(icon, t);

        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valLbl.setTextFill(Color.web(PRIMARY));

        card.getChildren().addAll(top, valLbl);
        return card;
    }

    private VBox createAdminSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 6, 12, 6));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label adminLabel = new Label("ADMIN DASHBOARD");
        adminLabel.setTextFill(Color.web(LIGHT));
        adminLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        brandTexts.getChildren().addAll(logo, adminLabel);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"Events", "fas-calendar-check"},
                {"Calendar", "fas-calendar"},
                {"Customers", "fas-users"},
                {"Organizers", "fas-user-tie"},
                {"Vendors", "fas-truck"},
                {"Conflict Detection", "fas-shield-alt"},
                {"Reports & Analytics", "fas-chart-pie"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab)));
        }

        Region sidebarSpace = new Region();
        VBox.setVgrow(sidebarSpace, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false),
                createSidebarButton("Profile", "fas-user", false),
                createSidebarButton("Settings", "fas-cog", false),
                createSidebarButton("Support", "fas-question-circle", false)
        );

        sidebar.getChildren().addAll(brandBox, menu, sidebarSpace, bottomMenu);
        return sidebar;
    }

    private Button createSidebarButton(String name, String iconLit, boolean active) {
        Button btn = new Button("  " + name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(PRIMARY) : Color.web("#CBD5E1"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color:" + LIGHT + "; -fx-text-fill:" + PRIMARY + "; -fx-font-weight:bold; -fx-font-size:12px; -fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px; -fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08); -fx-text-fill:white; -fx-font-size:12px; -fx-background-radius:6; -fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#CBD5E1; -fx-font-size:12px;"));
        }

        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            NavigationManager.navigate(name, stage);
        });

        return btn;
    }
}