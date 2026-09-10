package com.techtrio.view;

import java.util.List;

import com.techtrio.controller.OrganizerController;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.services.EventService;
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
import javafx.stage.Stage;

public class NotificationsO {

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";

    private final String uid;
    private final String idToken;

    private VBox notificationsListContainer;
    private Label unreadCountLabel;

    public NotificationsO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public NotificationsO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public Scene getNotificationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonOrganizerSidebar("Notifications"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label subtitle = new Label("Live updates on customer advance payments, escrow payouts, and event planning alerts.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button markReadBtn = new Button("✓ Mark all as read");
        markReadBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 7 14; -fx-cursor: hand;");
        markReadBtn.setOnAction(e -> {
            if (unreadCountLabel != null) unreadCountLabel.setText("0");
        });

        titleRow.getChildren().addAll(titleBox, headerSpacer, markReadBtn);

        HBox mainGrid = new HBox(20);

        VBox leftColumn = new VBox(12);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        notificationsListContainer = new VBox(12);
        leftColumn.getChildren().add(notificationsListContainer);

        VBox rightColumn = new VBox(16);
        rightColumn.setPrefWidth(330);
        rightColumn.setMinWidth(310);
        rightColumn.getChildren().addAll(createOverviewWidget());

        mainGrid.getChildren().addAll(leftColumn, rightColumn);

        mainContent.getChildren().addAll(titleRow, mainGrid);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadLiveNotificationsAsync();

        return new Scene(root, 1550, 785);
    }

    private void loadLiveNotificationsAsync() {
        Task<List<Event>> task = new Task<>() {
            @Override
            protected List<Event> call() {
                EventService eventService = new EventService();
                return eventService.getEventsByOrganizerId(uid);
            }
        };

        task.setOnSucceeded(e -> {
            List<Event> events = task.getValue();
            Platform.runLater(() -> {
                notificationsListContainer.getChildren().clear();
                int unread = 0;

                if (events != null && !events.isEmpty()) {
                    for (Event ev : events) {
                        double paidAmt = ev.getPaidAmount();

                        // ⭐ अचूक बदल: केवळ आणि केवळ कस्टमरने पैसे भरले (paidAmt > 0) तरच पेमेंट क्रेडिटचे नोटिफिकेशन येईल!
                        if (paidAmt > 0) {
                            double orgCommission = paidAmt * 0.15;
                            unread++;
                            notificationsListContainer.getChildren().add(
                                    createNotifCard("💳", "Payment Credited to Escrow!",
                                            "Customer made payment for [" + ev.getEventName() + "]. Your 15% management commission (₹ " + String.format("%,.0f", orgCommission) + ") is secured in Escrow.",
                                            "Live Sync", GREEN_BG, GREEN_TEXT, "View Budget & Wallet", () -> {
                                                BudgetExpenseTrackerO view = new BudgetExpenseTrackerO(uid, idToken);
                                                LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::returnToDashboard));
                                            })
                            );
                        }

                        // Vendor Milestone Notification
                        unread++;
                        notificationsListContainer.getChildren().add(
                                createNotifCard("📋", "Event Assignment Active",
                                        "[" + ev.getEventName() + "] is ready in workspace. Check verified vendor assignments and schedules.",
                                        "Recently", LIGHT_BLUE, PRIMARY_BLUE, "Open Planning Workspace", () -> {
                                            ViewEventO view = new ViewEventO(uid, idToken);
                                            LandingPage.landingPageStage.setScene(view.getScene(LandingPage.landingPageStage, this::returnToDashboard));
                                        })
                        );
                    }
                }

                if (unreadCountLabel != null) {
                    unreadCountLabel.setText(String.valueOf(unread));
                }
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createNotifCard(String iconStr, String title, String desc, String time, String bgHex, String titleColor, String actionText, Runnable action) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: " + bgHex + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 18px;");

        VBox textGroup = new VBox(3);
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + titleColor + ";");
        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        textGroup.getChildren().addAll(t, d);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        topRow.getChildren().addAll(icon, textGroup, sp, timeLbl);

        Button linkBtn = new Button(actionText + " →");
        linkBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11.5px; -fx-padding: 0; -fx-cursor: hand;");
        if (action != null) linkBtn.setOnAction(e -> action.run());

        card.getChildren().addAll(topRow, linkBtn);
        return card;
    }

    private VBox createOverviewWidget() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label title = new Label("Notification Summary");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        unreadCountLabel = new Label("0");
        unreadCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox r1 = new HBox(8);
        Label l1 = new Label("Unread Alerts:");
        l1.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        r1.getChildren().addAll(l1, sp, unreadCountLabel);

        card.getChildren().addAll(title, r1);
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
                createSidebarButton("Notifications", "fas-bell", activeTab.equals("Notifications"), () -> handleNav("Notifications")),
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
            btn.setStyle("-fx-background-color:" + LIGHT_BLUE + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-font-size:12px;-fx-background-radius:6;");
        } else {
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;-fx-cursor:hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#CBD5E1;-fx-font-size:12px;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void handleNav(String screen) {
        switch (screen) {
            case "Dashboard" -> returnToDashboard();
            case "Event Requests" -> {
                EventRequestsO reqs = new EventRequestsO(uid, idToken);
                LandingPage.landingPageStage.setScene(reqs.geteventRequestsOScene(null));
            }
            case "My Events" -> {
                MyEventsO myEvents = new MyEventsO(uid, idToken);
                LandingPage.landingPageStage.setScene(myEvents.getMyEventsOScene(null));
            }
            case "Schedule" -> {
                ScheduleO sched = new ScheduleO(uid, idToken);
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
            }
            case "Notifications" -> {}
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

    private void returnToDashboard() {
        OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
        LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
    }
}