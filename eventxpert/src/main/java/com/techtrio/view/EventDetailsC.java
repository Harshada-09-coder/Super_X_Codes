package com.techtrio.view;

import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EventDetailsC {

    private final String BLUE = "#031D49";
    private final String ACTIVE = "#073E82";
    private final String TEXT = "#102653";

    // =========================================================
    // USER SESSION DATA
    // =========================================================

    private final String uid;
    private final String idToken;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EventDetailsC() {

        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
    }

    // =========================================================
    // EVENT DETAILS SCENE
    // =========================================================

    public Scene getEventDetailsScene() {

        HBox root = new HBox();

        root.setStyle(
                "-fx-background-color:#F5F8FC;"
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar = new VBox(5);

        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);

        sidebar.setPadding(
                new Insets(24, 12, 15, 12)
        );

        sidebar.setStyle(
                "-fx-background-color:" + BLUE + ";"
        );

        Label logo =
                new Label("EventXpert");

        logo.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:27px;" +
                "-fx-font-weight:bold;"
        );

        Label console =
                new Label("Corporate Console");

        console.setStyle(
                "-fx-text-fill:#B9CCE8;" +
                "-fx-font-size:12px;"
        );

        Button createEvent =
                sideButton(
                        "+   Create Event",
                        false
                );

        createEvent.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#102653;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:7;"
        );

        createEvent.setPadding(
                new Insets(10)
        );

        Button dashboard =
                sideButton(
                        "▦   Dashboard",
                        false
                );

        Button requests =
                sideButton(
                        "▣   Event Requests",
                        false
                );

        Button events =
                sideButton(
                        "▣   Events",
                        true
                );

        Button calendar =
                sideButton(
                        "▦   Calendar",
                        false
                );

        Button customers =
                sideButton(
                        "♙   Customers",
                        false
                );

        Button organizers =
                sideButton(
                        "♙   Organizers",
                        false
                );

        Button vendorsMenu =
                sideButton(
                        "▤   Vendors",
                        false
                );

        VBox menu =
                new VBox(
                        5,
                        dashboard,
                        requests,
                        events,
                        calendar,
                        customers,
                        organizers,
                        vendorsMenu
                );

        Region sidebarSpace =
                new Region();

        VBox.setVgrow(
                sidebarSpace,
                Priority.ALWAYS
        );

        Button settings =
                sideButton(
                        "⚙   Settings",
                        false
                );

        Button help =
                sideButton(
                        "?   Help & Support",
                        false
                );

        Label user =
                new Label(
                        "  AC    Arjun Corporate\n" +
                        "          Corporate Admin"
                );

        user.setMaxWidth(
                Double.MAX_VALUE
        );

        user.setStyle(
                "-fx-text-fill:white;" +
                "-fx-background-color:#092F64;" +
                "-fx-background-radius:7;" +
                "-fx-padding:9;" +
                "-fx-font-size:10px;"
        );

        sidebar.getChildren().addAll(
                logo,
                console,
                new Region(),
                createEvent,
                new Region(),
                menu,
                sidebarSpace,
                settings,
                help,
                user
        );

        // =====================================================
        // RIGHT SIDE
        // =====================================================

        BorderPane right =
                new BorderPane();

        HBox.setHgrow(
                right,
                Priority.ALWAYS
        );

        right.setStyle(
                "-fx-background-color:#F5F8FC;"
        );

        // =====================================================
        // TOP BAR
        // =====================================================

        TextField search =
                new TextField();

        search.setPromptText(
                "Search events, venues, vendors..."
        );

        search.setPrefWidth(325);

        search.setStyle(
                "-fx-background-color:#F8FAFD;" +
                "-fx-border-color:#C7D3E2;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-padding:8 12;" +
                "-fx-font-size:12px;"
        );

        Button notification =
                topButton("♧");

        Button gear =
                topButton("⚙");

        Button helpTop =
                topButton("?");

        Button profile =
                topButton("AC");

        Region topSpace =
                new Region();

        HBox.setHgrow(
                topSpace,
                Priority.ALWAYS
        );

        HBox topBar =
                new HBox(
                        12,
                        search,
                        topSpace,
                        notification,
                        gear,
                        helpTop,
                        profile
                );

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(10, 20, 10, 25)
        );

        topBar.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9E1EC;" +
                "-fx-border-width:0 0 1 0;"
        );

        right.setTop(topBar);

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox main =
                new VBox(10);

        main.setPadding(
                new Insets(14, 22, 12, 22)
        );

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color:#F5F8FC;" +
                "-fx-background:#F5F8FC;"
        );

        // =====================================================
        // HEADING
        // =====================================================

        Label breadcrumb =
                new Label(
                        "Events   ›   Corporate Annual Conference 2026"
                );

        breadcrumb.setStyle(
                "-fx-text-fill:#6D7D95;" +
                "-fx-font-size:10px;"
        );

        Label title =
                new Label("Event Details");

        title.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:24px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "View and manage all details of your corporate event in one place."
                );

        subtitle.setStyle(
                "-fx-text-fill:#687890;" +
                "-fx-font-size:11px;"
        );

        VBox heading =
                new VBox(
                        2,
                        breadcrumb,
                        title,
                        subtitle
                );

        Region headingSpace =
                new Region();

        HBox.setHgrow(
                headingSpace,
                Priority.ALWAYS
        );

        HBox headingRow =
                new HBox(
                        10,
                        heading,
                        headingSpace
                );

        headingRow.setAlignment(
                Pos.CENTER_LEFT
        );

        // =====================================================
        // SUMMARY CARD
        // =====================================================

        VBox summaryCard =
                whiteCard();

        VBox eventImage =
                imageBox(
                        "/assets/images/event.jpg",
                        215,
                        150,
                        "Event Image"
                );

        VBox eventInfo =
                new VBox(6);

        Label eventName =
                new Label(
                        "Corporate Annual Conference 2026"
                );

        eventName.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        Label confirmed =
                pill(
                        "Confirmed",
                        "#D9F6E7",
                        "#087A4A"
                );

        Label date =
                infoText(
                        "▣   18 - 19 June 2026"
                );

        Label time =
                infoText(
                        "◷   09:00 AM - 05:00 PM (Both Days)"
                );

        Label place =
                infoText(
                        "⌖   Grand Hyatt, Mumbai, Maharashtra, India"
                );

        Label eventType =
                pill(
                        "Corporate Conference",
                        "#DCEBFF",
                        "#0757C7"
                );

        eventInfo.getChildren().addAll(
                eventName,
                confirmed,
                date,
                time,
                place,
                eventType
        );

        VBox guestsCard =
                statCard(
                        "♙",
                        "450",
                        "Expected Guests",
                        "#E7EEFF"
                );

        VBox vendorsCard =
                statCard(
                        "▤",
                        "18",
                        "Vendors",
                        "#E4F8EF"
                );

        VBox organizersCard =
                statCard(
                        "♙",
                        "5",
                        "Organizers",
                        "#FFF0DE"
                );

        VBox budgetTotalCard =
                statCard(
                        "₹",
                        "₹ 32,50,000",
                        "Total Budget",
                        "#F1E5FF"
                );

        HBox summaryRow =
                new HBox(
                        14,
                        eventImage,
                        eventInfo,
                        guestsCard,
                        vendorsCard,
                        organizersCard,
                        budgetTotalCard
                );

        HBox.setHgrow(
                eventInfo,
                Priority.ALWAYS
        );

        summaryCard.getChildren().add(
                summaryRow
        );

        // =====================================================
        // TABS
        // =====================================================

        HBox tabs =
                new HBox();

        String[] tabNames = {
                "Overview",
                "Guests",
                "Vendors",
                "Organizers",
                "Budget"
        };

        for (int i = 0; i < tabNames.length; i++) {

            Button tab =
                    new Button(
                            tabNames[i]
                    );

            tab.setPrefWidth(125);

            tab.setPadding(
                    new Insets(8)
            );

            if (i == 0) {

                tab.setStyle(
                        "-fx-background-color:white;" +
                        "-fx-text-fill:#0757E8;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#0757E8;" +
                        "-fx-border-width:0 0 3 0;"
                );

            } else {

                tab.setStyle(
                        "-fx-background-color:white;" +
                        "-fx-text-fill:#30476A;" +
                        "-fx-border-color:transparent;"
                );
            }

            tabs.getChildren().add(tab);
        }

        tabs.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#DCE3ED;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;"
        );

        // =====================================================
        // OVERVIEW CARD
        // =====================================================

        VBox overviewCard =
                whiteCard();

        overviewCard.getChildren().add(
                cardTitle("Event Overview")
        );

        addRow(
                overviewCard,
                "Event Name",
                "Corporate Annual Conference 2026"
        );

        addRow(
                overviewCard,
                "Event Type",
                "Corporate Conference"
        );

        addRow(
                overviewCard,
                "Organizer",
                "EventXpert Solutions Pvt. Ltd."
        );

        addRow(
                overviewCard,
                "Contact Person",
                "Arjun Mehta"
        );

        addRow(
                overviewCard,
                "Email",
                "arjun.mehta@eventxpert.com"
        );

        addRow(
                overviewCard,
                "Phone",
                "+91 98765 43210"
        );

        addRow(
                overviewCard,
                "Description",
                "Annual corporate conference for partners, clients and team members featuring keynote sessions, workshops and networking."
        );

        addRow(
                overviewCard,
                "Status",
                "Confirmed"
        );

        addRow(
                overviewCard,
                "Created On",
                "10 Apr 2026, 11:20 AM"
        );

        // =====================================================
        // TIMELINE CARD
        // =====================================================

        VBox timelineCard =
                whiteCard();

        timelineCard.getChildren().add(
                cardTitle("Event Timeline")
        );

        timelineItem(
                timelineCard,
                "Event Created",
                "10 Apr 2026, 11:20 AM",
                "#1769FF"
        );

        timelineItem(
                timelineCard,
                "Requirements Submitted",
                "15 Apr 2026, 02:30 PM",
                "#0BB36B"
        );

        timelineItem(
                timelineCard,
                "Venue Confirmed",
                "20 Apr 2026, 10:00 AM",
                "#FF9900"
        );

        timelineItem(
                timelineCard,
                "Budget Finalized",
                "25 Apr 2026, 04:15 PM",
                "#8D3DEB"
        );

        timelineItem(
                timelineCard,
                "AI Planning Completed",
                "01 May 2026, 03:40 PM",
                "#0BB36B"
        );

        timelineItem(
                timelineCard,
                "Event Confirmed",
                "05 May 2026, 09:30 AM",
                "#0BB36B"
        );

        HBox row1 =
                new HBox(
                        10,
                        overviewCard,
                        timelineCard
                );

        HBox.setHgrow(
                overviewCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                timelineCard,
                Priority.ALWAYS
        );

        // =====================================================
        // LOCATION CARD
        // =====================================================

        VBox locationCard =
                whiteCard();

        locationCard.getChildren().add(
                cardTitle("Location Details")
        );

        addRow(
                locationCard,
                "Venue",
                "Grand Hyatt Mumbai"
        );

        addRow(
                locationCard,
                "Address",
                "Off Western Express Highway, Santacruz East"
        );

        addRow(
                locationCard,
                "City",
                "Mumbai"
        );

        addRow(
                locationCard,
                "Area",
                "Santacruz East"
        );

        addRow(
                locationCard,
                "Venue Type",
                "5 Star Hotel"
        );

        addRow(
                locationCard,
                "Capacity",
                "600 Pax"
        );

        Button viewMap =
                outlineButton(
                        "⌖  View on Map"
                );

        locationCard.getChildren().add(
                viewMap
        );

        // =====================================================
        // MAP CARD
        // =====================================================

        VBox mapCard =
                imageBox(
                        "/assets/images/map logo.png",
                        250,
                        190,
                        "Google Maps"
                );

        // =====================================================
        // BUDGET CARD
        // =====================================================

        VBox budgetCard =
                whiteCard();

        budgetCard.getChildren().add(
                cardTitle("Budget Summary")
        );

        Label total =
                new Label("₹32,50,000");

        total.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:19px;" +
                "-fx-font-weight:bold;"
        );

        budgetCard.getChildren().add(total);

        budgetItem(
                budgetCard,
                "Venue",
                "₹12,00,000",
                "36.9%",
                "#2358FF"
        );

        budgetItem(
                budgetCard,
                "Catering",
                "₹7,50,000",
                "23.1%",
                "#11A89A"
        );

        budgetItem(
                budgetCard,
                "Decoration",
                "₹4,50,000",
                "13.8%",
                "#FF9800"
        );

        budgetItem(
                budgetCard,
                "AV & Technical",
                "₹3,50,000",
                "10.8%",
                "#D87B00"
        );

        budgetItem(
                budgetCard,
                "Photography",
                "₹2,00,000",
                "6.2%",
                "#567DCB"
        );

        // =====================================================
        // AI CARD
        // =====================================================

        VBox aiCard =
                whiteCard();

        aiCard.getChildren().add(
                cardTitle("AI Planning Insights")
        );

        Label aiEnabled =
                pill(
                        "AI Planning : Enabled",
                        "#D9F6E7",
                        "#087A4A"
                );

        aiCard.getChildren().add(
                aiEnabled
        );

        Label aiText =
                new Label(
                        "AI has generated a smart plan for your event."
                );

        aiText.setWrapText(true);

        aiText.setStyle(
                "-fx-text-fill:#30476A;" +
                "-fx-font-size:10px;"
        );

        aiCard.getChildren().add(
                aiText
        );

        aiCard.getChildren().add(
                checkItem(
                        "Itinerary generated with optimal flow"
                )
        );

        aiCard.getChildren().add(
                checkItem(
                        "Vendor recommendations completed"
                )
        );

        aiCard.getChildren().add(
                checkItem(
                        "Guest management plan ready"
                )
        );

        aiCard.getChildren().add(
                checkItem(
                        "Budget allocation optimized"
                )
        );

        aiCard.getChildren().add(
                checkItem(
                        "Risk analysis completed"
                )
        );

        Button aiDetails =
                new Button(
                        "View AI Plan Details  →"
                );

        aiDetails.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#0757E8;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );

        aiCard.getChildren().add(
                aiDetails
        );

        // =====================================================
        // SECOND ROW
        // =====================================================

        HBox row2 =
                new HBox(
                        10,
                        locationCard,
                        mapCard,
                        budgetCard,
                        aiCard
                );

        HBox.setHgrow(
                locationCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                mapCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                budgetCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                aiCard,
                Priority.ALWAYS
        );

        // =====================================================
        // ADD MAIN CONTENT
        // =====================================================

        main.getChildren().addAll(
                headingRow,
                summaryCard,
                tabs,
                row1,
                row2
        );

        scrollPane.setContent(main);

        right.setCenter(scrollPane);

        // =====================================================
        // FOOTER BUTTONS
        // =====================================================

        Button back =
                outlineButton("Back");

        Button next =
                blueButton("Next");

        // =====================================================
        // NEXT BUTTON
        // =====================================================

        next.setOnAction(e -> {

            Notification notificationPage =
                    new Notification();

            LandingPage.landingPageStage.setScene(
                    notificationPage.getNotificationScene()
            );

        });

        // =====================================================
        // FOOTER
        // =====================================================

        Region footerSpace =
                new Region();

        HBox.setHgrow(
                footerSpace,
                Priority.ALWAYS
        );

        HBox footer =
                new HBox(
                        10,
                        footerSpace,
                        back,
                        next
                );

        footer.setAlignment(
                Pos.CENTER_RIGHT
        );

        footer.setPadding(
                new Insets(7, 20, 7, 20)
        );

        footer.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D8E1EC;" +
                "-fx-border-width:1 0 0 0;"
        );

        right.setBottom(footer);

        // =====================================================
        // SEARCH
        // =====================================================

        search.setOnAction(e -> {

            String text =
                    search.getText().trim();

            if (!text.isEmpty()) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setTitle(
                        "EventXpert Search"
                );

                alert.setHeaderText(
                        "Search Result"
                );

                alert.setContentText(
                        "Searching for: " + text
                );

                alert.showAndWait();
            }
        });

        // =====================================================
        // MAP BUTTON
        // =====================================================

        viewMap.setOnAction(e ->
                System.out.println(
                        "View on Map"
                )
        );

        // =====================================================
        // BACK → REVIEW PAGE
        // =====================================================

        back.setOnAction(e -> {

            ReviewPageC reviewPage =
                    new ReviewPageC(
                            uid,
                            idToken
                    );

            LandingPage.landingPageStage.setScene(
                    reviewPage.getReviewScene()
            );

        });

        // =====================================================
        // CREATE EVENT BUTTON
        // =====================================================

        createEvent.setOnAction(e -> {

            CreateEventC createEventPage =
                    new CreateEventC(
                            uid,
                            idToken
                    );

            LandingPage.landingPageStage.setScene(
                    createEventPage.getCreateEventScene()
            );

        });

        // =====================================================
        // ROOT
        // =====================================================

        root.getChildren().addAll(
                sidebar,
                right
        );

        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(
                        root,
                        1365,
                        850
                );

        return scene;
    }

    // =========================================================
    // SIDEBAR BUTTON
    // =========================================================

    private Button sideButton(
            String text,
            boolean active
    ) {

        Button b =
                new Button(text);

        b.setMaxWidth(
                Double.MAX_VALUE
        );

        b.setAlignment(
                Pos.CENTER_LEFT
        );

        b.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        b.setStyle(
                "-fx-background-color:" +
                (active
                        ? ACTIVE
                        : "transparent") +
                ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:" +
                (active
                        ? "bold"
                        : "normal") +
                ";" +
                "-fx-background-radius:6;"
        );

        return b;
    }

    // =========================================================
    // TOP BUTTON
    // =========================================================

    private Button topButton(
            String text
    ) {

        Button b =
                new Button(text);

        b.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#09265A;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        return b;
    }

    // =========================================================
    // WHITE CARD
    // =========================================================

    private VBox whiteCard() {

        VBox box =
                new VBox(7);

        box.setPadding(
                new Insets(11)
        );

        box.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9E2ED;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;"
        );

        return box;
    }

    // =========================================================
    // CARD TITLE
    // =========================================================

    private Label cardTitle(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        return label;
    }

    // =========================================================
    // ADD ROW
    // =========================================================

    private void addRow(
            VBox box,
            String name,
            String value
    ) {

        HBox row =
                new HBox(8);

        Label n =
                new Label(name);

        n.setPrefWidth(115);

        n.setStyle(
                "-fx-text-fill:#18345E;" +
                "-fx-font-size:10px;"
        );

        Label colon =
                new Label(":");

        colon.setStyle(
                "-fx-text-fill:#71809A;" +
                "-fx-font-size:10px;"
        );

        Label v =
                new Label(value);

        v.setWrapText(true);

        v.setStyle(
                "-fx-text-fill:#18345E;" +
                "-fx-font-size:10px;"
        );

        HBox.setHgrow(
                v,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                n,
                colon,
                v
        );

        box.getChildren().add(row);
    }

    // =========================================================
    // TIMELINE ITEM
    // =========================================================

    private void timelineItem(
            VBox box,
            String title,
            String date,
            String color
    ) {

        HBox row =
                new HBox(8);

        Label dot =
                new Label("●");

        dot.setStyle(
                "-fx-text-fill:" +
                color +
                ";" +
                "-fx-font-size:11px;"
        );

        VBox text =
                new VBox(1);

        Label t =
                new Label(title);

        t.setStyle(
                "-fx-text-fill:#18345E;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );

        Label d =
                new Label(date);

        d.setStyle(
                "-fx-text-fill:#748198;" +
                "-fx-font-size:9px;"
        );

        text.getChildren().addAll(
                t,
                d
        );

        row.getChildren().addAll(
                dot,
                text
        );

        box.getChildren().add(row);
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox statCard(
            String icon,
            String number,
            String label,
            String background
    ) {

        VBox box =
                new VBox(4);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(8)
        );

        box.setMinWidth(105);

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-background-color:" +
                background +
                ";" +
                "-fx-background-radius:30;" +
                "-fx-padding:9;" +
                "-fx-font-size:16px;"
        );

        Label numberLabel =
                new Label(number);

        numberLabel.setStyle(
                "-fx-text-fill:" +
                TEXT +
                ";" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        Label text =
                new Label(label);

        text.setStyle(
                "-fx-text-fill:#30476A;" +
                "-fx-font-size:9px;"
        );

        box.getChildren().addAll(
                iconLabel,
                numberLabel,
                text
        );

        return box;
    }

    // =========================================================
    // BUDGET ITEM
    // =========================================================

    private void budgetItem(
            VBox box,
            String name,
            String amount,
            String percentage,
            String color
    ) {

        HBox row =
                new HBox(5);

        Label nameLabel =
                new Label(
                        "●  " + name
                );

        Label amountLabel =
                new Label(
                        amount +
                        " (" +
                        percentage +
                        ")"
                );

        HBox.setHgrow(
                nameLabel,
                Priority.ALWAYS
        );

        nameLabel.setStyle(
                "-fx-text-fill:" +
                color +
                ";" +
                "-fx-font-size:9px;"
        );

        amountLabel.setStyle(
                "-fx-text-fill:#30476A;" +
                "-fx-font-size:9px;"
        );

        row.getChildren().addAll(
                nameLabel,
                amountLabel
        );

        box.getChildren().add(row);
    }

    // =========================================================
    // CHECK ITEM
    // =========================================================

    private Label checkItem(
            String text
    ) {

        Label label =
                new Label(
                        "●  " + text
                );

        label.setStyle(
                "-fx-text-fill:#0B9B68;" +
                "-fx-font-size:9px;"
        );

        return label;
    }

    // =========================================================
    // INFO TEXT
    // =========================================================

    private Label infoText(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:#30476A;" +
                "-fx-font-size:10px;"
        );

        return label;
    }

    // =========================================================
    // PILL
    // =========================================================

    private Label pill(
            String text,
            String background,
            String color
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-background-color:" +
                background +
                ";" +
                "-fx-text-fill:" +
                color +
                ";" +
                "-fx-background-radius:15;" +
                "-fx-padding:4 9;" +
                "-fx-font-size:9px;" +
                "-fx-font-weight:bold;"
        );

        return label;
    }

    // =========================================================
    // IMAGE BOX
    // =========================================================

    private VBox imageBox(
            String path,
            double width,
            double height,
            String fallback
    ) {

        VBox box =
                new VBox();

        box.setAlignment(
                Pos.CENTER
        );

        box.setPrefWidth(width);
        box.setPrefHeight(height);

        try {

            if (getClass().getResourceAsStream(path) != null) {

                Image image =
                        new Image(
                                getClass()
                                        .getResourceAsStream(path)
                        );

                ImageView view =
                        new ImageView(image);

                view.setFitWidth(width);
                view.setFitHeight(height);

                view.setPreserveRatio(false);

                box.getChildren().add(view);

            } else {

                Label label =
                        new Label(fallback);

                label.setStyle(
                        "-fx-text-fill:#71809A;" +
                        "-fx-font-size:11px;"
                );

                box.getChildren().add(label);
            }

        } catch (Exception e) {

            Label label =
                    new Label(fallback);

            label.setStyle(
                    "-fx-text-fill:#71809A;" +
                    "-fx-font-size:11px;"
            );

            box.getChildren().add(label);
        }

        box.setStyle(
                "-fx-background-color:#EDF3F9;" +
                "-fx-border-color:#D7E0EB;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;"
        );

        return box;
    }

    // =========================================================
    // OUTLINE BUTTON
    // =========================================================

    private Button outlineButton(
            String text
    ) {

        Button b =
                new Button(text);

        b.setPadding(
                new Insets(
                        7,
                        14,
                        7,
                        14
                )
        );

        b.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#173360;" +
                "-fx-border-color:#B8C7DA;" +
                "-fx-border-radius:5;" +
                "-fx-background-radius:5;" +
                "-fx-font-size:10px;"
        );

        return b;
    }

    // =========================================================
    // BLUE BUTTON
    // =========================================================

    private Button blueButton(
            String text
    ) {

        Button b =
                new Button(text);

        b.setPadding(
                new Insets(
                        8,
                        16,
                        8,
                        16
                )
        );

        b.setStyle(
                "-fx-background-color:#0757E8;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:5;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );

        return b;
    }
}