package com.techtrio.view;

import com.techtrio.context.AdminContext;
import com.techtrio.model.Conflict;
import com.techtrio.model.Event;
import com.techtrio.services.ConflictService;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ScheduleO {

    private Scene scheduleScene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String BORDER_COLOR = "#DCE5F0";

    private final EventService eventService = new EventService();
    private final ConflictService conflictService = new ConflictService();

    private List<Event> organizerEvents = new ArrayList<>();
    private List<Conflict> organizerConflicts = new ArrayList<>();

    private YearMonth currentYearMonth = YearMonth.now();

    private Label monthLabel;
    private GridPane calendarGrid;
    private VBox upcomingEventsBox;

    private Label todayKpiVal;
    private Label weekKpiVal;
    private Label upcomingKpiVal;
    private Label conflictsKpiVal;

    public ScheduleO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ScheduleO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getScene(Stage stage, Runnable backAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonOrganizerSidebar("Schedule"));

        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 32, 28, 32));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Label title = new Label("Event Schedule & Timeline");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Live schedule overview, assigned event timelines and real-time conflict tracking.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        VBox titleBox = new VBox(2, title, subtitle);

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        HBox headerRow = new HBox(12, titleBox, topSpace);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        todayKpiVal = new Label("00");
        weekKpiVal = new Label("00");
        upcomingKpiVal = new Label("00");
        conflictsKpiVal = new Label("00");

        HBox kpiCards = new HBox(14);
        kpiCards.getChildren().addAll(
                createKpiCard("TODAY'S SCHEDULE", todayKpiVal, "fas-calendar-day", "#0284C7"),
                createKpiCard("THIS WEEK", weekKpiVal, "fas-calendar-week", "#16A34A"),
                createKpiCard("TOTAL UPCOMING", upcomingKpiVal, "fas-calendar-alt", PRIMARY_BLUE),
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
        scroll.setStyle("-fx-background-color:transparent; -fx-background:" + BACKGROUND + "; -fx-border-color:transparent;");

        root.setCenter(scroll);

        renderCalendarMonth();
        loadOrganizerScheduleDataAsync();

        scheduleScene = new Scene(root, 1550, 785);
        return scheduleScene;
    }

    private VBox createCalendarContainer() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER_COLOR + "; -fx-border-radius:10px; -fx-background-radius:10px;");

        Button prevBtn = new Button("‹");
        prevBtn.setStyle("-fx-background-color: #F1F5F9; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 12;");
        prevBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            renderCalendarMonth();
        });

        Button nextBtn = new Button("›");
        nextBtn.setStyle("-fx-background-color: #F1F5F9; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 12;");
        nextBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            renderCalendarMonth();
        });

        Button todayBtn = new Button("Today");
        todayBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 4px; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 11px;");
        todayBtn.setOnAction(e -> {
            currentYearMonth = YearMonth.now();
            renderCalendarMonth();
        });

        monthLabel = new Label();
        monthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        monthLabel.setTextFill(Color.web(NAVY));

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

    // ⭐ Event Completed झाल्यावर बॉक्सला हिरवी बॉर्डर देणारे लॉजिक
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
            dayHead.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 4px;");
            calendarGrid.add(dayHead, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        int row = 1;
        int col = dayOfWeekOffset;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate cellDate = currentYearMonth.atDay(day);

            VBox cell = new VBox(3);
            cell.setPadding(new Insets(6));
            cell.setPrefHeight(80);
            boolean isToday = cellDate.equals(today);

            List<Event> dateEvents = new ArrayList<>();
            boolean hasCompletedEvent = false;

            if (organizerEvents != null) {
                for (Event ev : organizerEvents) {
                    if (isEventOnDate(ev, cellDate)) {
                        dateEvents.add(ev);
                        if ("COMPLETED".equalsIgnoreCase(ev.getStatus())) {
                            hasCompletedEvent = true; // ⭐ इव्हेंट पूर्ण झाला आहे का तपासणे
                        }
                    }
                }
            }

            // ⭐ Admin सारखीच Completed झाल्यावर हिरवी बॉर्डर
            if (hasCompletedEvent) {
                cell.setStyle("-fx-background-color: #F0FDF4; -fx-border-color: #16A34A; -fx-border-width: 2px; -fx-border-radius: 6px; -fx-background-radius: 6px;");
            } else if (isToday) {
                cell.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-width: 1.5px; -fx-border-radius: 6px; -fx-background-radius: 6px;");
            } else {
                cell.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-background-radius: 6px;");
            }

            Label dayNum = new Label(String.valueOf(day));
            dayNum.setFont(Font.font("Arial", isToday || hasCompletedEvent ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            dayNum.setTextFill(hasCompletedEvent ? Color.web("#16A34A") : (isToday ? Color.web(PRIMARY_BLUE) : Color.web("#1E293B")));
            cell.getChildren().add(dayNum);

            VBox eventBadgeContainer = new VBox(2);
            int displayedEvents = 0;

            for (Event ev : dateEvents) {
                boolean isCompleted = "COMPLETED".equalsIgnoreCase(ev.getStatus());
                String eventName = (ev.getEventName() != null && !ev.getEventName().isBlank()) ? ev.getEventName() : "Event";

                if (displayedEvents < 2) {
                    Label evTag = new Label(isCompleted ? "✓ " + eventName : eventName);
                    evTag.setMaxWidth(Double.MAX_VALUE);
                    evTag.setEllipsisString("...");

                    if (isCompleted) {
                        evTag.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 3px; -fx-cursor: hand;");
                    } else {
                        evTag.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 4; -fx-background-radius: 3px; -fx-cursor: hand;");
                    }

                    Tooltip tip = new Tooltip(eventName + "\nStatus: " + (isCompleted ? "Completed" : "Active"));
                    tip.setStyle("-fx-font-size: 11px;");
                    Tooltip.install(evTag, tip);

                    eventBadgeContainer.getChildren().add(evTag);
                }
                displayedEvents++;
            }

            if (displayedEvents > 2) {
                Label moreLbl = new Label("+" + (displayedEvents - 2) + " more");
                moreLbl.setStyle("-fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
                eventBadgeContainer.getChildren().add(moreLbl);
            }

            cell.getChildren().add(eventBadgeContainer);
            calendarGrid.add(cell, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private boolean isEventOnDate(Event ev, LocalDate date) {
        if (ev == null || date == null) return false;
        LocalDate sDate = parseDate(ev.getEventDate());
        if (sDate == null) sDate = parseDate(ev.getStartDate());
        return sDate != null && sDate.equals(date);
    }

    private VBox createUpcomingSideCard() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: white; -fx-border-color:" + BORDER_COLOR + "; -fx-border-radius:10px; -fx-background-radius:10px;");

        HBox top = new HBox();
        Label t = new Label("Upcoming Events");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        t.setTextFill(Color.web(NAVY));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button viewAll = new Button("View All →");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
        viewAll.setOnAction(e -> handleNav("My Events"));

        top.getChildren().addAll(t, sp, viewAll);
        upcomingEventsBox = new VBox(10);

        box.getChildren().addAll(top, upcomingEventsBox);
        return box;
    }

    private void loadOrganizerScheduleDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    if (uid != null && !uid.isBlank()) {
                        try {
                            organizerEvents = eventService.getEventsByOrganizerId(uid);
                        } catch (Exception ex) {
                            System.err.println("[ScheduleO] Error getEventsByOrganizerId: " + ex.getMessage());
                        }
                        organizerConflicts = fetchConflictsSafely(uid, idToken);
                    }

                    if (organizerEvents == null) organizerEvents = new ArrayList<>();
                    if (organizerConflicts == null) organizerConflicts = new ArrayList<>();

                    LocalDate today = LocalDate.now();
                    LocalDate weekEnd = today.plusDays(7);

                    int todayCount = 0;
                    int weekCount = 0;
                    int upcomingCount = 0;

                    List<Event> upcomingList = new ArrayList<>();

                    for (Event ev : organizerEvents) {
                        String st = ev.getStatus() != null ? ev.getStatus().trim().toUpperCase() : "";
                        
                        // ⭐ COMPLETED झालेले इव्हेंट्स Upcoming मध्ये मोजायचे नाहीत!
                        if ("COMPLETED".equals(st)) {
                            continue;
                        }

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

                    upcomingList.sort((e1, e2) -> {
                        LocalDate d1 = parseDate(e1.getEventDate()) != null ? parseDate(e1.getEventDate()) : parseDate(e1.getStartDate());
                        LocalDate d2 = parseDate(e2.getEventDate()) != null ? parseDate(e2.getEventDate()) : parseDate(e2.getStartDate());
                        if (d1 == null) return 1;
                        if (d2 == null) return -1;
                        return d1.compareTo(d2);
                    });

                    final int fToday = todayCount;
                    final int fWeek = weekCount;
                    final int fUpcoming = upcomingCount;
                    final int fConflicts = organizerConflicts.size();
                    final List<Event> fUpcomingList = upcomingList;

                    Platform.runLater(() -> {
                        todayKpiVal.setText(String.format("%02d", fToday));
                        weekKpiVal.setText(String.format("%02d", fWeek));
                        upcomingKpiVal.setText(String.format("%02d", fUpcoming));
                        conflictsKpiVal.setText(String.format("%02d", fConflicts));

                        renderCalendarMonth();
                        renderUpcomingList(fUpcomingList);
                    });

                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @SuppressWarnings("unchecked")
    private List<Conflict> fetchConflictsSafely(String uId, String token) {
        if (conflictService == null) return new ArrayList<>();
        try {
            Class<?> cls = conflictService.getClass();
            for (Method m : cls.getMethods()) {
                if (m.getName().toLowerCase().contains("conflict")) {
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length == 2 && p[0] == String.class && p[1] == String.class) {
                        return (List<Conflict>) m.invoke(conflictService, uId, token);
                    } else if (p.length == 1 && p[0] == String.class) {
                        return (List<Conflict>) m.invoke(conflictService, uId);
                    } else if (p.length == 0) {
                        return (List<Conflict>) m.invoke(conflictService);
                    }
                }
            }
        } catch (Exception ignored) {}
        return new ArrayList<>();
    }

    // ⭐ Upcoming List मध्ये COMPLETED झालेले इव्हेंट गाळणे
    private void renderUpcomingList(List<Event> list) {
        if (upcomingEventsBox == null) return;
        upcomingEventsBox.getChildren().clear();

        if (list == null || list.isEmpty()) {
            Label noEv = new Label("No upcoming events scheduled.");
            noEv.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
            upcomingEventsBox.getChildren().add(noEv);
            return;
        }

        int count = 0;
        for (Event ev : list) {
            if ("COMPLETED".equalsIgnoreCase(ev.getStatus())) continue;

            String dateText = ev.getEventDate() != null ? ev.getEventDate() : (ev.getStartDate() != null ? ev.getStartDate() : "Active");
            String name = ev.getEventName() != null && !ev.getEventName().isBlank() ? ev.getEventName() : "Event";
            String loc = ev.getCity() != null && !ev.getCity().isBlank() ? ev.getCity() : "Location TBD";
            String guests = String.valueOf(ev.getGuests());

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8));
            row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");

            StackPane dateBox = new StackPane();
            dateBox.setPrefSize(42, 42);
            dateBox.setStyle("-fx-background-color: #E0E7FF; -fx-background-radius: 6px;");

            LocalDate parsed = parseDate(dateText);
            String displayDate = (parsed != null) ? parsed.format(DateTimeFormatter.ofPattern("dd MMM")) : (dateText.length() > 6 ? dateText.substring(5) : dateText);

            Label dt = new Label(displayDate);
            dt.setStyle("-fx-font-weight: bold; -fx-font-size: 9px; -fx-text-fill: #4338CA; -fx-text-alignment: center;");
            dt.setWrapText(true);
            dateBox.getChildren().add(dt);

            VBox info = new VBox(2);
            Label n = new Label(name);
            n.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + NAVY + ";");
            n.setMaxWidth(200);
            n.setEllipsisString("...");

            Label l = new Label("📍 " + loc + " • " + guests + " Guests");
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

    private VBox createKpiCard(String title, Label valLbl, String iconLit, String colorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:white; -fx-border-color:" + BORDER_COLOR + "; -fx-border-radius:10px; -fx-background-radius:10px;");
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
        valLbl.setTextFill(Color.web(NAVY));

        card.getChildren().addAll(top, valLbl);
        return card;
    }

    private VBox createCommonOrganizerSidebar(String activeTab) {
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
        Label portal = new Label("ORGANIZER PORTAL");
        portal.setStyle("-fx-text-fill: " + LIGHT_BLUE + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        brandTexts.getChildren().addAll(logo, portal);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        VBox menu = new VBox(3);
        String[][] menuItems = {
                {"Dashboard", "fas-tachometer-alt"},
                {"Event Requests", "fas-inbox"},
                {"My Events", "fas-calendar-check"},
                {"Schedule", "fas-calendar-alt"}
        };

        for (String[] item : menuItems) {
            String name = item[0];
            String iconLit = item[1];
            menu.getChildren().add(createSidebarButton(name, iconLit, name.equals(activeTab), () -> handleNav(name)));
        }

        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);

        VBox bottomMenu = new VBox(3);
        bottomMenu.getChildren().addAll(
                createSidebarButton("Notifications", "fas-bell", false, () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", false, () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", false, () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", false, () -> handleNav("Support"))
        );

        sidebar.getChildren().addAll(brandBox, menu, space, bottomMenu);
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
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6px;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6px;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard" -> {
                OrganizerDashboardO dash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(dash.getorganizerDashboardScene(null));
            }
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> {
                MyEventsO myEvents = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(myEvents.getMyEventsOScene(null));
            }
            case "Schedule" -> {}
            case "Notifications" -> {
                NotificationsO notif = new NotificationsO(uid, idToken);
                LandingPage.landingPageStage.setScene(notif.getNotificationScene());
            }
            case "Profile" -> {
                ProfileO prof = new ProfileO(uid, idToken);
                LandingPage.landingPageStage.setScene(prof.getProfileScene());
            }
            case "Settings" -> {
                SettingsO set = new SettingsO(uid, idToken);
                LandingPage.landingPageStage.setScene(set.getSettingScene(null));
            }
            case "Support" -> {
                SupportO supp = new SupportO(uid, idToken);
                LandingPage.landingPageStage.setScene(supp.getSupportOScene(null));
            }
        }
    }
}