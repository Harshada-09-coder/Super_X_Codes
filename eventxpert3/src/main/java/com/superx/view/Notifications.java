package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Notifications extends Application {

    

   
    final String NAVY = "#062957";
    final String BG = "#F6F8FB";

    VBox notificationList = new VBox(12);
    Label total = new Label("12");
    Label unread = new Label("5");
    Label critical = new Label("2");
    Label today = new Label("4");

    @Override
    public void start(Stage stage) {

        /* ================= SIDEBAR ================= */

        VBox side = new VBox(10);
        side.setPrefWidth(258);
        side.setPadding(new Insets(25, 14, 15, 14));
        side.setStyle("-fx-background-color:" + NAVY + ";");

        

        Label logo = new Label("EventXpert");
        logo.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label sub = new Label("Admin Dashboard");
        sub.setStyle(
                "-fx-text-fill:#AAB9CC;" +
                "-fx-font-size:18px;"
        );

        VBox brandText = new VBox(2, logo, sub);
        HBox brand = new HBox(10, brandText);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 5, 25, 5));

        

        VBox menu = new VBox(3);

        String[] menus = {
                "▦   Dashboard",
                "◫   Event Requests",
                "▣   Events",
                "▤   Calendar",
                "♧   Customers",
                "♟   Organizers",
                "▤   Vendors",
                "⚠   Conflict Detection",
                "▣   Reports & Analytics"
        };

        for (String text : menus) {
            Label item = new Label(text);
            item.setMaxWidth(Double.MAX_VALUE);
            item.setPrefHeight(40);
            item.setPadding(new Insets(10, 8, 10, 8));
            item.setStyle(
                    "-fx-text-fill:#AAB9CC;" +
                    "-fx-font-size:14px;"
            );
            menu.getChildren().add(item);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label notification = sideItem("♟   Notifications");
        notification.setStyle(
                "-fx-background-color:#BFE1FA;" +
                "-fx-text-fill:#062957;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7;" +
                "-fx-padding:10px 8px;"
        );

        Label settings = sideItem("⚙   Settings");
        Label profile = sideItem("◉   Profile");
        Label support = sideItem("?   Support");

        VBox bottom = new VBox(5,
                notification,
                settings,
                profile,
                support
        );

        side.getChildren().addAll(
                brand,
                menu,
                spacer,
                bottom
        );

        /* ================= MAIN ================= */

        VBox main = new VBox(15);
        main.setPadding(new Insets(25, 25, 25, 25));
        main.setStyle("-fx-background-color:" + BG + ";");

        /* HEADER */

        Label title = new Label("Notifications");
        title.setStyle(
                "-fx-font-size:38px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#20242A;"
        );

        Label subtitle = new Label(
                "Stay updated with important system activities and alerts."
        );
        subtitle.setStyle(
                "-fx-font-size:16px;" +
                "-fx-text-fill:#5D6470;"
        );

        VBox heading = new VBox(4, title, subtitle);

        Button clear = new Button("≡  Clear all");
        clear.setStyle(topButton());

        Button markRead = new Button("✓  Mark all as read");
        markRead.setStyle(topButton());

        HBox actions = new HBox(18, clear, markRead);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpace = new Region();
        HBox.setHgrow(headerSpace, Priority.ALWAYS);

        HBox header = new HBox(
                heading,
                headerSpace,
                actions
        );
        header.setAlignment(Pos.CENTER_LEFT);

        Separator separator = new Separator();

        /* ================= SUMMARY CARDS ================= */

        HBox cards = new HBox(15);

        cards.getChildren().addAll(
                summaryCard("▣", "All Notifications",
                        total, "Total notifications"),

                summaryCard("•", "Unread",
                        unread, "Need your attention"),

                summaryCard("!", "Critical",
                        critical, "Urgent notifications"),

                summaryCard("▣", "Today",
                        today, "Received today")
        );

        /* ================= FILTERS ================= */

        HBox filters = new HBox(8);

        String[] filterNames = {
                "All", "Unread", "Read", "Critical",
                "Event", "Payment", "User", "System"
        };

        for (String name : filterNames) {

            Button b = new Button(name);

            b.setStyle(filterStyle(name.equals("All")));

            b.setOnAction(e -> {

                for (javafx.scene.Node n : filters.getChildren())
                    n.setStyle(filterStyle(false));

                b.setStyle(filterStyle(true));
            });

            filters.getChildren().add(b);
        }

        /* ================= NOTIFICATIONS ================= */

        addNotification(
                "⚠",
                "CONFLICT DETECTION",
                "10 minutes ago",
                "Critical Conflict Detected",
                "Organizer Elite Event Planners has been double-booked for Corporate Seminar and Corporate Workshop.",
                true
        );

        addNotification(
                "▣",
                "EVENT REQUEST",
                "25 minutes ago",
                "New Event Request",
                "A new event request has been submitted by Incubators Pvt.",
                false
        );

        addNotification(
                "▣",
                "PAYMENT",
                "1 hour ago",
                "Payment Received",
                "Payment of ₹40,000 received for Corporate Seminar.",
                false
        );

        addNotification(
                "♟",
                "ORGANIZER",
                "2 hours ago",
                "New Organizer Registered",
                "Prime Event Mgmt has successfully registered as a new organizer.",
                false
        );

        addNotification(
                "▤",
                "VENDOR APPROVAL",
                "3 hours ago",
                "Vendor Approval Required",
                "Vision AV Solutions is waiting for admin approval.",
                false
        );

        addNotification(
                "✓",
                "EVENT",
                "Yesterday",
                "Event Successfully Completed",
                "Corporate Seminar has been successfully completed.",
                false
        );

        ScrollPane scroll = new ScrollPane(notificationList);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-border-color:transparent;"
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);

        /* ================= BUTTON FUNCTIONS ================= */

        clear.setOnAction(e -> {

            notificationList.getChildren().clear();

            total.setText("0");
            unread.setText("0");
            critical.setText("0");
            today.setText("0");
        });

        markRead.setOnAction(e -> {

            unread.setText("0");

            for (javafx.scene.Node n : notificationList.getChildren()) {
                n.setStyle(
                        "-fx-background-color:white;" +
                        "-fx-border-color:#D9DEE5;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;"
                );
            }
        });

        main.getChildren().addAll(
                header,
                separator,
                cards,
                filters,
                scroll
        );

        /* ================= ROOT ================= */

        BorderPane root = new BorderPane();
        root.setLeft(side);
        root.setCenter(main);

        Scene scene = new Scene(root, 1550, 750);

        stage.setTitle("EventXpert - Notifications");
        stage.setScene(scene);
        stage.show();
    }

    /* ================= SUMMARY CARD ================= */

    private VBox summaryCard(
            String icon,
            String heading,
            Label number,
            String description
    ) {

        Label i = new Label(icon);
        i.setStyle(
                "-fx-font-size:20px;" +
                "-fx-text-fill:#65707C;"
        );

        Label h = new Label(heading);
        h.setStyle(
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        number.setStyle(
                "-fx-font-size:25px;" +
                "-fx-font-weight:bold;"
        );

        Label d = new Label(description);
        d.setStyle(
                "-fx-font-size:14px;" +
                "-fx-text-fill:#606873;"
        );

        VBox box = new VBox(7, i, h, number, d);

        box.setPadding(new Insets(18));
        box.setPrefHeight(130);

        HBox.setHgrow(box, Priority.ALWAYS);

        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9DEE5;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        return box;
    }

    /* ================= NOTIFICATION CARD ================= */

    private void addNotification(
            String icon,
            String type,
            String time,
            String heading,
            String message,
            boolean isCritical
    ) {

        Label iconLabel = new Label(icon);

        iconLabel.setMinSize(48, 48);
        iconLabel.setAlignment(Pos.CENTER);

        iconLabel.setStyle(
                "-fx-background-color:" +
                (isCritical ? "#FFD9D9" : "#D8EEFF") +
                ";" +
                "-fx-background-radius:50%;" +
                "-fx-font-size:20px;" +
                "-fx-text-fill:" +
                (isCritical ? "#D21E1E" : "#075B9E") +
                ";"
        );

        Label typeLabel = new Label(type);

        typeLabel.setStyle(
                "-fx-background-color:" +
                (isCritical ? "#FFDADA" : "#E8EAED") +
                ";" +
                "-fx-text-fill:" +
                (isCritical ? "#D21E1E" : "#626973") +
                ";" +
                "-fx-padding:4px 9px;" +
                "-fx-background-radius:10;" +
                "-fx-font-size:11px;"
        );

        Label timeLabel = new Label(time);
        timeLabel.setStyle(
                "-fx-text-fill:#666D76;" +
                "-fx-font-size:13px;"
        );

        HBox top = new HBox(
                10,
                typeLabel,
                timeLabel
        );

        top.setAlignment(Pos.CENTER_LEFT);

        Label headingLabel = new Label(heading);
        headingLabel.setStyle(
                "-fx-font-size:20px;" +
                "-fx-font-weight:bold;"
        );

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
                "-fx-font-size:15px;" +
                "-fx-text-fill:#626873;"
        );

        VBox text = new VBox(
                6,
                top,
                headingLabel,
                messageLabel
        );

        HBox.setHgrow(text, Priority.ALWAYS);

        Label dot = new Label("●");
        dot.setStyle(
                "-fx-text-fill:" + NAVY + ";" +
                "-fx-font-size:12px;"
        );

        HBox notificationBox = new HBox(
                18,
                iconLabel,
                text,
                dot
        );

        notificationBox.setAlignment(Pos.TOP_LEFT);
        notificationBox.setPadding(new Insets(20));

        notificationBox.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9DEE5;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        if (isCritical) {

            Button review = new Button("Review");

            review.setStyle(
                    "-fx-background-color:#C91D1D;" +
                    "-fx-text-fill:white;" +
                    "-fx-font-size:14px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-background-radius:6;" +
                    "-fx-padding:10px 20px;"
            );

            review.setOnAction(e -> {

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION
                );

                alert.setTitle("Conflict Detection");
                alert.setHeaderText("Critical Conflict Detected");
                alert.setContentText(
                        "Opening Conflict Detection review..."
                );

                alert.showAndWait();
            });

            notificationBox.getChildren().add(review);
        }

        notificationList.getChildren().add(notificationBox);
    }

    /* ================= SIDEBAR ITEM ================= */

    private Label sideItem(String text) {

        Label item = new Label(text);

        item.setMaxWidth(Double.MAX_VALUE);
        item.setPrefHeight(40);
        item.setPadding(new Insets(10, 8, 10, 8));

        item.setStyle(
                "-fx-text-fill:#AAB9CC;" +
                "-fx-font-size:14px;"
        );

        return item;
    }

    /* ================= TOP BUTTON ================= */

    private String topButton() {

        return
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#173B68;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;";
    }

    /* ================= FILTER BUTTON ================= */

    private String filterStyle(boolean active) {

        return
                "-fx-background-color:" +
                (active ? "#BFE1FA" : "white") +
                ";" +
                "-fx-text-fill:#26384F;" +
                "-fx-border-color:#C7CED8;" +
                "-fx-border-radius:20;" +
                "-fx-background-radius:20;" +
                "-fx-padding:8px 18px;" +
                "-fx-font-size:14px;";
    }

}