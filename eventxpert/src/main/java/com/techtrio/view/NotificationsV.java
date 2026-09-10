package com.techtrio.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.techtrio.controller.VendorController;
import com.techtrio.model.VendorBooking;
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

public class NotificationsV {

    private Scene notificationsScene;
    private VendorDashboardV vendorDashboard;
    private String uid;
    private String idToken;

    private static final String SIDEBAR_NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#16A34A";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#D97706";

    private static class NotificationItem {
        String id;
        String title;
        String desc;
        String timeAgo;
        String category;
        boolean isUnread;
        String eventTag;
        String budgetTag;
        String iconLiteral;
        VendorBooking rawBooking;

        NotificationItem(String id, String title, String desc, String timeAgo, String category,
                         boolean isUnread, String eventTag, String budgetTag, String iconLiteral, VendorBooking rawBooking) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.timeAgo = timeAgo;
            this.category = category;
            this.isUnread = isUnread;
            this.eventTag = eventTag;
            this.budgetTag = budgetTag;
            this.iconLiteral = iconLiteral;
            this.rawBooking = rawBooking;
        }
    }

    private final List<NotificationItem> notificationsList = new ArrayList<>();
    private VBox notificationStreamContainer;
    private Label unreadCountCardLabel;
    private Label totalCountCardLabel;
    private String activeCategoryFilter = "All";

    // Focus Card Labels
    private Label detailTitleLabel;
    private Label detailTimeLabel;
    private Label detailMsgLabel;
    private Label detailEventLabel;
    private Label detailBudgetLabel;

    public NotificationsV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public NotificationsV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getnotificationsScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Standard Sidebar
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        root.setLeft(dashHelper.createCommonVendorSidebar("Notifications"));

        // 2. Main ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createSummaryCardsRow(),
                createTwoColumnMainLayout(LandingPage.landingPageStage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadRealtimeNotificationsAsync();

        notificationsScene = new Scene(root, 1550, 785);
        return notificationsScene;
    }

    private void loadRealtimeNotificationsAsync() {
        Task<List<VendorBooking>> task = new Task<>() {
            @Override
            protected List<VendorBooking> call() {
                VendorController controller = new VendorController();
                return controller.getVendorBookings(uid);
            }
        };

        task.setOnSucceeded(e -> {
            List<VendorBooking> bookings = task.getValue();
            Platform.runLater(() -> {
                notificationsList.clear();
                if (bookings != null && !bookings.isEmpty()) {
                    for (VendorBooking b : bookings) {
                        String st = b.getStatus() != null ? b.getStatus().toUpperCase() : "PENDING";
                        if ("PENDING".equals(st) || "NEW".equals(st)) {
                            notificationsList.add(new NotificationItem(
                                    b.getBookingId(),
                                    "New Service Request for " + b.getEventName(),
                                    (b.getOrganizerName() != null ? b.getOrganizerName() : "Organizer") + " has assigned you for " + b.getServiceCategory() + " with budget " + b.getAmount(),
                                    "Live Synced",
                                    "Service Requests",
                                    true,
                                    b.getEventName(),
                                    b.getAmount(),
                                    "fas-inbox",
                                    b
                            ));
                        } else if ("CONFIRMED".equals(st) || "ACCEPTED".equals(st)) {
                            notificationsList.add(new NotificationItem(
                                    b.getBookingId(),
                                    "Booking Confirmed: " + b.getEventName(),
                                    "Service assignment for " + b.getServiceCategory() + " is active. Date: " + b.getEventDate(),
                                    "Recent",
                                    "Bookings",
                                    false,
                                    b.getEventName(),
                                    b.getAmount(),
                                    "fas-calendar-check",
                                    b
                            ));
                        }
                    }
                }

                updateCounts();
                renderNotificationFeed(LandingPage.landingPageStage);
            });
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Notifications & Alerts");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button markAllBtn = new Button("✓ Mark All as Read");
        markAllBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6px; -fx-cursor: hand;");
        markAllBtn.setOnAction(e -> {
            for (NotificationItem n : notificationsList) n.isUnread = false;
            updateCounts();
            renderNotificationFeed(LandingPage.landingPageStage);
        });

        titleRow.getChildren().addAll(title, spacer, markAllBtn);

        Label subtitle = new Label("Stay updated with your real-time service requests, confirmed bookings and payments.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    private HBox createSummaryCardsRow() {
        HBox row = new HBox(14);
        unreadCountCardLabel = new Label("0");
        totalCountCardLabel = new Label("0");

        row.getChildren().addAll(
                createSummaryCard("Unread Alerts", unreadCountCardLabel, "fas-envelope", PRIMARY_BLUE),
                createSummaryCard("Total Alerts", totalCountCardLabel, "fas-bell", SECONDARY_TEXT)
        );
        return row;
    }

    private VBox createSummaryCard(String title, Label valLbl, String iconLit, String colorHex) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox(6);
        top.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(12);
        icon.setIconColor(Color.web(colorHex));

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        top.getChildren().addAll(icon, t);

        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        card.getChildren().addAll(top, valLbl);
        return card;
    }

    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        HBox filterTabs = createFilterTabsRow(stage);
        VBox feedCard = createCardContainer();
        feedCard.setPadding(new Insets(0));

        notificationStreamContainer = new VBox();
        feedCard.getChildren().add(notificationStreamContainer);

        leftCol.getChildren().addAll(filterTabs, feedCard);

        // Right Focus Card
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(360);
        rightCol.getChildren().add(createNotificationDetailsCard(stage));

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private HBox createFilterTabsRow(Stage stage) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        String[] tabs = {"All", "Unread", "Service Requests", "Bookings"};

        for (String tab : tabs) {
            Button tabBtn = new Button(tab);
            if (tab.equals(activeCategoryFilter)) {
                tabBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
            } else {
                tabBtn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_MAIN + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 5 14; -fx-cursor: hand;");
            }

            tabBtn.setOnAction(e -> {
                activeCategoryFilter = tab;
                renderNotificationFeed(stage);
            });

            row.getChildren().add(tabBtn);
        }
        return row;
    }

    private void renderNotificationFeed(Stage stage) {
        if (notificationStreamContainer == null) return;
        notificationStreamContainer.getChildren().clear();

        List<NotificationItem> filtered = notificationsList.stream().filter(n -> {
            if ("All".equals(activeCategoryFilter)) return true;
            if ("Unread".equals(activeCategoryFilter)) return n.isUnread;
            return n.category.equalsIgnoreCase(activeCategoryFilter);
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox empty = new VBox(10); empty.setAlignment(Pos.CENTER); empty.setPadding(new Insets(30));
            empty.getChildren().add(new Label("No notifications match the selected filter."));
            notificationStreamContainer.getChildren().add(empty);
            return;
        }

        for (NotificationItem n : filtered) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14, 16, 14, 16));
            row.setStyle("-fx-background-color: " + (n.isUnread ? LIGHT_BLUE : WHITE) + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

            FontIcon icon = new FontIcon(n.iconLiteral);
            icon.setIconSize(16);
            icon.setIconColor(Color.web(PRIMARY_BLUE));

            VBox textGroup = new VBox(2);
            Label title = new Label(n.title);
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label desc = new Label(n.desc);
            desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            textGroup.getChildren().addAll(title, desc);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label timeLbl = new Label(n.timeAgo);
            timeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            row.getChildren().addAll(icon, textGroup, sp, timeLbl);

            row.setOnMouseClicked(e -> {
                n.isUnread = false;
                updateFocusDetails(n);
                renderNotificationFeed(stage);
                updateCounts();
            });

            notificationStreamContainer.getChildren().add(row);
        }
    }

    private VBox createNotificationDetailsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Alert Focus Details");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        detailTitleLabel = new Label("Select a notification to view full details");
        detailTitleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        detailTitleLabel.setWrapText(true);

        detailTimeLabel = new Label("Timestamp: -");
        detailTimeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        detailMsgLabel = new Label("Detailed message will appear here.");
        detailMsgLabel.setWrapText(true);
        detailMsgLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        detailEventLabel = new Label("• Event: -");
        detailBudgetLabel = new Label("• Budget: -");

        VBox metaBox = new VBox(4, detailEventLabel, detailBudgetLabel);
        metaBox.setPadding(new Insets(8));
        metaBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        card.getChildren().addAll(title, detailTitleLabel, detailTimeLabel, new Separator(), detailMsgLabel, metaBox);
        return card;
    }

    private void updateFocusDetails(NotificationItem n) {
        detailTitleLabel.setText(n.title);
        detailTimeLabel.setText("Status: " + n.timeAgo);
        detailMsgLabel.setText(n.desc);
        detailEventLabel.setText("• Event: " + n.eventTag);
        detailBudgetLabel.setText("• Allocated Budget: " + n.budgetTag);
    }

    private void updateCounts() {
        long unread = notificationsList.stream().filter(n -> n.isUnread).count();
        if (unreadCountCardLabel != null) unreadCountCardLabel.setText(String.valueOf(unread));
        if (totalCountCardLabel != null) totalCountCardLabel.setText(String.valueOf(notificationsList.size()));
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}