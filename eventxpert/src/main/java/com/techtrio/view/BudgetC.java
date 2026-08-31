package com.techtrio.view;

import com.techtrio.session.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BudgetC {

    private Scene scene;
    private String uid;
    private String idToken;

    private final String NAVY = "#002A60";
    private final String BLUE = "#0057B8";
    private final String LIGHT = "#C2F0F4";
    private final String BG = "#F5F8FA";
    private final String BORDER = "#D5DEE7";
    private final String TEXT = "#102A43";
    private final String GREY = "#667085";

    private Label totalAmount;

    // Constructor with user information
    public BudgetC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank())
                ? uid
                : UserSession.getUid();

        this.idToken = (idToken != null && !idToken.isBlank())
                ? idToken
                : UserSession.getIdToken();
    }

    // No-argument constructor
    public BudgetC() {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
    }

    // Default scene
    public Scene getBudgetScene() {
        return getBudgetScene(null, null);
    }

    // Budget scene
    public Scene getBudgetScene(
            Runnable backAction,
            Runnable nextAction
    ) {

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox(7);

        sidebar.setPrefWidth(205);
        sidebar.setMinWidth(205);
        sidebar.setMaxWidth(205);

        sidebar.setPadding(
                new Insets(24, 8, 20, 8)
        );

        sidebar.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        Label logo = label(
                "EventXpert",
                21,
                true,
                Color.WHITE
        );

        VBox eventTitle = new VBox(
                2,
                label(
                        "Create Event",
                        16,
                        true,
                        Color.WHITE
                ),
                label(
                        "Step-by-step setup",
                        10,
                        false,
                        Color.web("#D1DFEB")
                )
        );

        eventTitle.setPadding(
                new Insets(7, 12, 15, 12)
        );

        VBox navigation = new VBox(2);

        navigation.getChildren().addAll(
                navItem("ⓘ", "Event Details", false),
                navItem("▤", "Requirements", false),
                navItem("⌖", "Location", false),
                navItem("▣", "Budget", true),
                navItem("✦", "AI Planning", false),
                navItem("▤", "Review", false)
        );

        Region sidebarSpace = new Region();

        VBox.setVgrow(
                sidebarSpace,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                logo,
                eventTitle,
                navigation,
                sidebarSpace
        );

        // =========================================================
        // TOP BAR
        // =========================================================

        HBox topBar = new HBox(24);

        topBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        topBar.setPadding(
                new Insets(12, 28, 12, 28)
        );

        topBar.setPrefHeight(58);

        topBar.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:#DCE5EC;" +
                "-fx-border-width:0 0 1 0;"
        );

        topBar.getChildren().addAll(
                label(
                        "♧",
                        19,
                        false,
                        Color.web(NAVY)
                ),
                label(
                        "?",
                        16,
                        true,
                        Color.web(NAVY)
                )
        );

        // =========================================================
        // TITLE
        // =========================================================

        VBox title = new VBox(
                4,
                label(
                        "Set Your Event Budget",
                        28,
                        true,
                        Color.web("#111111")
                ),
                label(
                        "Define your total spend and let our AI optimize the distribution.",
                        13,
                        false,
                        Color.web(GREY)
                )
        );

        title.setPadding(
                new Insets(22, 32, 18, 32)
        );

        // =========================================================
        // ESTIMATED BUDGET CARD
        // =========================================================

        VBox estimated = card();

        estimated.getChildren().add(
                label(
                        "Estimated Total Budget",
                        13,
                        true,
                        Color.web(TEXT)
                )
        );

        TextField amount = new TextField(
                ReviewPageC.EventData.budget != null
                        ? ReviewPageC.EventData.budget
                        : "₹ 500,000"
        );

        amount.setPrefHeight(48);

        amount.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        amount.setStyle(
                "-fx-border-color:#C9D3DC;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-padding:8 12;"
        );

        estimated.getChildren().add(
                amount
        );

        estimated.getChildren().add(
                label(
                        "ⓘ  Minimum recommended for this scale: ₹450,000",
                        10,
                        false,
                        Color.web(GREY)
                )
        );

        // =========================================================
        // SMART BUDGET OPTIMIZER
        // =========================================================

        VBox smart = card();

        HBox smartHeader = new HBox();

        smartHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        Label smartTitle = label(
                "✦  Smart Budget Optimizer",
                15,
                true,
                Color.web(TEXT)
        );

        Region smartSpace = new Region();

        HBox.setHgrow(
                smartSpace,
                Priority.ALWAYS
        );

        ToggleButton toggle =
                new ToggleButton("ON");

        toggle.setSelected(true);

        toggle.setPrefSize(
                58,
                27
        );

        toggle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        toggle.setTextFill(
                Color.WHITE
        );

        toggle.setStyle(
                "-fx-background-color:" + BLUE + ";" +
                "-fx-background-radius:20;"
        );

        toggle.selectedProperty().addListener(
                (obs, oldValue, newValue) -> {

                    toggle.setText(
                            newValue ? "ON" : "OFF"
                    );

                    toggle.setStyle(
                            "-fx-background-color:" +
                            (newValue ? BLUE : "#8A96A3") +
                            ";" +
                            "-fx-background-radius:20;"
                    );
                }
        );

        smartHeader.getChildren().addAll(
                smartTitle,
                smartSpace,
                toggle
        );

        Label smartInfo = label(
                "Auto-allocate funds based on industry standards.",
                10,
                false,
                Color.web(GREY)
        );

        VBox recommendation = new VBox(8);

        recommendation.setPadding(
                new Insets(12)
        );

        recommendation.setStyle(
                "-fx-background-color:#F4F7F9;" +
                "-fx-border-color:" + NAVY + ";" +
                "-fx-border-width:0 0 0 3;"
        );

        HBox tags = new HBox(
                6,
                tag("Venue 30%"),
                tag("Catering 25%"),
                tag("Decor 20%")
        );

        recommendation.getChildren().addAll(
                label(
                        "AI Recommendation Applied:",
                        10,
                        true,
                        Color.web(NAVY)
                ),
                tags,
                tag("Other 25%")
        );

        smart.getChildren().addAll(
                smartHeader,
                smartInfo,
                recommendation
        );

        // =========================================================
        // LEFT SIDE
        // =========================================================

        VBox left = new VBox(
                16,
                estimated,
                smart
        );

        left.setPrefWidth(330);
        left.setMinWidth(330);
        left.setMaxWidth(330);

        VBox.setVgrow(
                estimated,
                Priority.ALWAYS
        );

        VBox.setVgrow(
                smart,
                Priority.ALWAYS
        );

        // =========================================================
        // ALLOCATION CARD
        // =========================================================

        VBox allocation = card();

        allocation.setPrefWidth(480);
        allocation.setMinWidth(450);

        HBox allocationHeader = new HBox();

        allocationHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox allocationText = new VBox(
                3,
                label(
                        "Budget Allocation",
                        16,
                        true,
                        Color.web("#111111")
                ),
                label(
                        "Visual breakdown of your expenses",
                        10,
                        false,
                        Color.web(GREY)
                )
        );

        Region allocationSpace = new Region();

        HBox.setHgrow(
                allocationSpace,
                Priority.ALWAYS
        );

        totalAmount = label(
                "₹500,000",
                21,
                true,
                Color.web(NAVY)
        );

        VBox total = new VBox(1);

        total.setAlignment(
                Pos.CENTER_RIGHT
        );

        total.getChildren().addAll(
                totalAmount,
                label(
                        "Total Allocated",
                        9,
                        false,
                        Color.web(GREY)
                )
        );

        allocationHeader.getChildren().addAll(
                allocationText,
                allocationSpace,
                total
        );

        VBox rows = new VBox(12);

        rows.getChildren().addAll(
                budgetRow("▣", "Venue", "30%"),
                budgetRow("🍴", "Catering", "25%"),
                budgetRow("✧", "Decoration", "20%"),
                budgetRow("▣", "Photography", "10%"),
                budgetRow("▯", "Sound", "5%"),
                budgetRow("•••", "Other", "10%")
        );

        allocation.getChildren().addAll(
                allocationHeader,
                new Separator(),
                rows
        );

        // =========================================================
        // BUDGET AMOUNT LISTENER
        // =========================================================

        amount.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    try {

                        String clean =
                                newValue.replaceAll(
                                        "[^0-9.]",
                                        ""
                                );

                        if (!clean.isEmpty()) {

                            double value =
                                    Double.parseDouble(clean);

                            totalAmount.setText(
                                    String.format(
                                            "₹%,.0f",
                                            value
                                    )
                            );
                        }

                    } catch (Exception e) {
                        // Ignore invalid input while typing
                    }
                }
        );

        // =========================================================
        // CONTENT
        // =========================================================

        HBox content = new HBox(22);

        content.setAlignment(
                Pos.TOP_LEFT
        );

        content.setPadding(
                new Insets(0, 32, 20, 32)
        );

        content.getChildren().addAll(
                left,
                allocation
        );

        HBox.setHgrow(
                allocation,
                Priority.ALWAYS
        );

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        // =========================================================
        // FOOTER BUTTONS
        // =========================================================





        Button back = footerButton(
                "Back",
                80,
                false
        );

        Button next = footerButton(
                "Next",
                82,
                true
        );

        // =========================================================
        // BACK BUTTON
        // =========================================================

        back.setOnAction(e -> {

            if (backAction != null) {

                backAction.run();

            } else {

                LocationC locationPage =
                        new LocationC(uid, idToken);

                LandingPage.landingPageStage.setScene(
                        locationPage.getLocationScene()
                );
            }
        });

        // =========================================================
        // NEXT BUTTON
        // =========================================================

        next.setOnAction(e -> {

            ReviewPageC.EventData.budget =
                    amount.getText().trim();

            if (nextAction != null) {

                nextAction.run();

            } else {

                AiPlanningC aiPlanningPage =
                        new AiPlanningC(uid, idToken);

                LandingPage.landingPageStage.setScene(
                        aiPlanningPage.getAiPlanningScene()
                );
            }
        });

        HBox buttons = new HBox(
                10,
                back,
                next
        );

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        Region footerSpace = new Region();

        HBox.setHgrow(
                footerSpace,
                Priority.ALWAYS
        );

        HBox footer = new HBox(
                footerSpace,
                buttons
        );

        footer.setAlignment(
                Pos.CENTER_RIGHT
        );

        footer.setPadding(
                new Insets(10, 30, 12, 30)
        );

        footer.setPrefHeight(64);

        footer.setStyle(
                "-fx-background-color:#F8FAFC;" +
                "-fx-border-color:#DCE5EC;" +
                "-fx-border-width:1 0 0 0;"
        );

        // =========================================================
        // CENTER
        // =========================================================

        VBox center = new VBox(
                topBar,
                title,
                content,
                footer
        );

        center.setStyle(
                "-fx-background-color:" + BG + ";"
        );

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        // =========================================================
        // ROOT
        // =========================================================

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + NAVY + ";"
        );

        root.setLeft(sidebar);
        root.setCenter(center);

        scene = new Scene(
                root,
                1365,
                850
        );

        return scene;
    }

    // =============================================================
    // LABEL
    // =============================================================

    private Label label(
            String text,
            double size,
            boolean bold,
            Color color
    ) {

        Label l = new Label(text);

        l.setFont(
                Font.font(
                        "Arial",
                        bold
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        size
                )
        );

        l.setTextFill(color);

        return l;
    }

    // =============================================================
    // CARD
    // =============================================================

    private VBox card() {

        VBox v = new VBox(12);

        v.setPadding(
                new Insets(18)
        );

        v.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        return v;
    }

    // =============================================================
    // NAV ITEM
    // =============================================================

    private HBox navItem(
            String icon,
            String text,
            boolean active
    ) {

        Label i = label(
                icon,
                16,
                false,
                active
                        ? Color.WHITE
                        : Color.web("#D0DFEC")
        );

        Label t = label(
                text,
                12,
                true,
                active
                        ? Color.WHITE
                        : Color.web("#D0DFEC")
        );

        HBox h = new HBox(
                12,
                i,
                t
        );

        h.setAlignment(
                Pos.CENTER_LEFT
        );

        h.setPrefHeight(38);

        h.setPadding(
                new Insets(7, 10, 7, 12)
        );

        if (active) {

            h.setStyle(
                    "-fx-background-color:#07396E;" +
                    "-fx-border-color:" + LIGHT + ";" +
                    "-fx-border-width:0 0 0 3;" +
                    "-fx-background-radius:7;"
            );

        } else {

            h.setStyle(
                    "-fx-background-color:transparent;"
            );
        }

        return h;
    }

    // =============================================================
    // FOOTER BUTTON
    // =============================================================

    private Button footerButton(
            String text,
            double width,
            boolean primary
    ) {

        Button b = new Button(text);

        b.setPrefSize(
                width,
                42
        );

        b.setStyle(
                primary
                        ? "-fx-background-color:" + BLUE + ";" +
                          "-fx-background-radius:7;" +
                          "-fx-text-fill:white;" +
                          "-fx-font-weight:bold;"
                        : "-fx-background-color:white;" +
                          "-fx-border-color:#A7B9C8;" +
                          "-fx-border-radius:7;" +
                          "-fx-background-radius:7;" +
                          "-fx-text-fill:" + NAVY + ";"
        );

        return b;
    }

    // =============================================================
    // TAG
    // =============================================================

    private Label tag(String text) {

        Label t = label(
                text,
                9,
                true,
                Color.web(NAVY)
        );

        t.setPadding(
                new Insets(5, 8, 5, 8)
        );

        t.setStyle(
                "-fx-background-color:" + LIGHT + ";" +
                "-fx-background-radius:12;"
        );

        return t;
    }

    // =============================================================
    // BUDGET ROW
    // =============================================================

    private VBox budgetRow(
            String icon,
            String name,
            String percent
    ) {

        HBox top = new HBox(10);

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label iconLabel = label(
                icon,
                15,
                false,
                Color.web(NAVY)
        );

        iconLabel.setPrefSize(
                32,
                32
        );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setStyle(
                "-fx-background-color:#E5F0FF;" +
                "-fx-background-radius:7;"
        );

        Label nameLabel = label(
                name,
                11,
                true,
                Color.web(TEXT)
        );

        Region space = new Region();

        HBox.setHgrow(
                space,
                Priority.ALWAYS
        );

        Label percentLabel = label(
                percent,
                10,
                true,
                Color.web(NAVY)
        );

        top.getChildren().addAll(
                iconLabel,
                nameLabel,
                space,
                percentLabel
        );

        ProgressBar line = new ProgressBar(
                Double.parseDouble(
                        percent.replace("%", "")
                ) / 100
        );

        line.setMaxWidth(
                Double.MAX_VALUE
        );

        line.setPrefHeight(7);

        line.setStyle(
                "-fx-accent:#111111;"
        );

        return new VBox(
                5,
                top,
                line
        );
    }
}