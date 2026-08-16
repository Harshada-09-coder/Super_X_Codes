package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AIEventPlanner extends Application {

    // Color Palette
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    
    // Priority Badges & Alerts
    private static final String RED_TEXT = "#C5221F";
    private static final String RED_BG = "#FCE8E6";
    private static final String ORANGE_TEXT = "#B06000";
    private static final String ORANGE_BG = "#FEF7E0";
    private static final String GREEN_TEXT = "#137333";
    private static final String GREEN_BG = "#E6F4EA";

    private TextField aiPromptInput;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;");

        // Top Navigation Bar
        root.setTop(createTopBar());

        // Sidebar Navigation
        root.setLeft(createSidebar());

        // Main Scrollable Area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1440, 900);
        primaryStage.setTitle("EventXpert - AI Event Planner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================
    // TOP BAR
    // ==========================================
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        // Search Bar
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(6, 14, 6, 14));
        searchBox.setMaxWidth(480);
        searchBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 20; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        Label searchIcon = new Label("🔍 ");
        searchIcon.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search events, vendors, or tasks...");
        topSearch.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(topSearch, Priority.ALWAYS);

        searchBox.getChildren().addAll(searchIcon, topSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        Button msgBtn = new Button("✉️");
        msgBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");

        // Profile Box
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Circle circle = new Circle(18, Color.web(PRIMARY_BLUE));
        Label avatarText = new Label("AS");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        avatar.getChildren().addAll(circle, avatarText);

        VBox userInfo = new VBox(1);
        Label userName = new Label("Alex Sterling");
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px;");
        Label userRole = new Label("Lead Organizer");
        userRole.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);

        profileBox.getChildren().addAll(avatar, userInfo);

        topBar.getChildren().addAll(searchBox, spacer, notifBtn, msgBtn, profileBox);
        return topBar;
    }

    // ==========================================
    // SIDEBAR
    // ==========================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setSpacing(6);

        // Logo
        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 10, 6));

        StackPane logoBg = new StackPane();
        Rectangle rect = new Rectangle(34, 34);
        rect.setArcWidth(8);
        rect.setArcHeight(8);
        rect.setFill(Color.web(PRIMARY_BLUE));
        Label logoText = new Label("EX");
        logoText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        logoBg.getChildren().addAll(rect, logoText);

        VBox brandTitles = new VBox(1);
        Label brandName = new Label("EventXpert");
        brandName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Corporate Management");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        brandTitles.getChildren().addAll(brandName, brandSub);

        brand.getChildren().addAll(logoBg, brandTitles);
        sidebar.getChildren().addAll(brand, new Separator());

        // Nav Menu
        sidebar.getChildren().addAll(
            createNavHeader("EVENT MANAGEMENT"),
            createNavItem("Dashboard", false),
            createNavItem("Event Requests", false),
            createNavItem("My Events", false),

            createNavHeader("PLANNING HUB"),
            createNavItem("AI Planner", true), // Highlighted
            createNavItem("Budget", false),
            createNavItem("Venue", false),
            createNavItem("Vendors", false),
            createNavItem("Schedule", false),
            createNavItem("Guest Management", false),

            createNavHeader("EXECUTION"),
            createNavItem("QR Check-In", false),
            createNavItem("Live Event", false)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomMenu = new VBox(4);
        bottomMenu.getChildren().addAll(
            new Separator(),
            createNavItem("Settings", false),
            createNavItem("Support", false)
        );

        sidebar.getChildren().addAll(spacer, bottomMenu);
        return sidebar;
    }

    private Label createNavHeader(String title) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 8 0 2 6;");
        return header;
    }

    private Button createNavItem(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(7, 10, 7, 10));

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-background-radius: 6; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-background-radius: 6; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-background-radius: 6; -fx-cursor: hand;"));
        }

        btn.setOnAction(e -> {
            if (!isActive) {
                showInfoAlert("Navigation", "Navigating to " + text + "...");
            }
        });

        return btn;
    }

    // ==========================================
    // MAIN CONTENT AREA
    // ==========================================
    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(24, 30, 30, 30));

        // Breadcrumb
        HBox breadcrumbs = new HBox(8);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);

        Label bc1 = new Label("My Events");
        bc1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px; -fx-cursor: hand;");
        bc1.setOnMouseClicked(e -> showInfoAlert("Navigation", "Navigating to My Events..."));

        Label bcSep1 = new Label(">");
        bcSep1.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc2 = new Label("TechVision Annual Summit");
        bc2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 13px;");

        Label bcSep2 = new Label(">");
        bcSep2.setStyle("-fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-size: 12px;");

        Label bc3 = new Label("AI Planner");
        bc3.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        breadcrumbs.getChildren().addAll(bc1, bcSep1, bc2, bcSep2, bc3);

        // Header Title Row
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("AI Event Planner");
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label betaBadge = new Label(" BETA ");
        betaBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + BRIGHT_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 12;");

        titleRow.getChildren().addAll(pageTitle, betaBadge);

        Label pageSubtitle = new Label("Plan smarter with intelligent recommendations tailored to your event parameters and current progress.");
        pageSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox headerGroup = new VBox(4);
        headerGroup.getChildren().addAll(breadcrumbs, titleRow, pageSubtitle);

        // Top AI Analysis Complete Summary Card
        VBox aiSummaryCard = createAISummaryCard();

        // 2-Column Main Layout
        HBox columns = new HBox(24);

        // Left Column: Recommended Next Steps Grid
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label recTitle = new Label("! Recommended Next Steps");
        recTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane stepsGrid = createStepsGrid();

        leftCol.getChildren().addAll(recTitle, stepsGrid);

        // Right Column: Risk Analysis & Budget Insights
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        rightCol.getChildren().addAll(
            createRiskAnalysisCard(),
            createBudgetInsightCard()
        );

        columns.getChildren().addAll(leftCol, rightCol);

        // Bottom AI Chat & Prompt Bar
        VBox aiChatBar = createAIChatBar();

        mainBox.getChildren().addAll(headerGroup, aiSummaryCard, columns, aiChatBar);
        return mainBox;
    }

    // ==========================================
    // AI SUMMARY CARD
    // ==========================================
    private VBox createAISummaryCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        Circle c = new Circle(22, Color.web(PRIMARY_BLUE));
        Label ic = new Label("✨");
        ic.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        iconBox.getChildren().addAll(c, ic);

        VBox textGroup = new VBox(4);
        HBox.setHgrow(textGroup, Priority.ALWAYS);

        Label summaryTitle = new Label("Analysis Complete for TechVision Summit");
        summaryTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label summaryDesc = new Label("I've analyzed your event type, guest count (250), location, budget (₹5,00,000) and current planning progress. Here are the most important actions to keep your corporate conference on track for May 18th.");
        summaryDesc.setWrapText(true);
        summaryDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        textGroup.getChildren().addAll(summaryTitle, summaryDesc);

        // Right side Progress Box
        HBox healthBox = new HBox(20);
        healthBox.setAlignment(Pos.CENTER);
        healthBox.setPadding(new Insets(10, 16, 10, 16));
        healthBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        VBox hVal1 = new VBox(2);
        Label hLabel1 = new Label("PLANNING HEALTH");
        hLabel1.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label hStatus = new Label("● Good");
        hStatus.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");
        hVal1.getChildren().addAll(hLabel1, hStatus);

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);

        VBox hVal2 = new VBox(4);
        Label hLabel2 = new Label("PROGRESS");
        hLabel2.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox pBox = new HBox(8);
        pBox.setAlignment(Pos.CENTER_LEFT);
        Label pText = new Label("65%");
        pText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");
        ProgressBar pb = new ProgressBar(0.65);
        pb.setPrefWidth(70);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 6;");
        pBox.getChildren().addAll(pText, pb);

        hVal2.getChildren().addAll(hLabel2, pBox);

        healthBox.getChildren().addAll(hVal1, sep, hVal2);

        row.getChildren().addAll(iconBox, textGroup, healthBox);
        card.getChildren().add(row);
        return card;
    }

    // ==========================================
    // RECOMMENDED NEXT STEPS GRID
    // ==========================================
    private GridPane createStepsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        // Card 1: Finalize AV & Sound (High Priority)
        grid.add(createStepCard(
            "HIGH PRIORITY", RED_TEXT, RED_BG, RED_TEXT,
            "Finalize AV & Sound",
            "Your corporate conference requires microphones, projection screens and presentation AV equipment before next week.",
            "Review Recommendation", true
        ), 0, 0);

        // Card 2: Confirm Catering Capacity (Medium Priority)
        grid.add(createStepCard(
            "MEDIUM PRIORITY", ORANGE_TEXT, ORANGE_BG, ORANGE_TEXT,
            "Confirm Catering Capacity",
            "250 guests are expected. Current catering vendor contract only covers 200 pax. Confirm capacity immediately.",
            "View Vendors", false
        ), 1, 0);

        // Card 3: Review Weather Impact (Medium Priority)
        grid.add(createStepCard(
            "MEDIUM PRIORITY", ORANGE_TEXT, ORANGE_BG, ORANGE_TEXT,
            "Review Weather Impact",
            "30% rain probability detected for May 18. This may affect outdoor movement between halls.",
            "Check Weather", false
        ), 0, 1);

        // Card 4: Complete Guest List (Task)
        grid.add(createStepCard(
            "TASK", PRIMARY_BLUE, LIGHT_BLUE, PRIMARY_BLUE,
            "Complete Guest List",
            "180 out of 250 expected guests are currently confirmed. Send follow-up reminders to pending invites.",
            "Manage Guests", false
        ), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createStepCard(String tagText, String tagColor, String tagBg, String borderColor, String title, String desc, String btnText, boolean isPrimary) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + borderColor + "; -fx-border-width: 1 1 1 4; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label tag = new Label(tagText);
        tag.setStyle("-fx-background-color: " + tagBg + "; -fx-text-fill: " + tagColor + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        topRow.getChildren().add(tag);

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label cardDesc = new Label(desc);
        cardDesc.setWrapText(true);
        cardDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button(btnText);
        actionBtn.setMaxWidth(Double.MAX_VALUE);

        if (isPrimary) {
            actionBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 6; -fx-cursor: hand;");
        } else {
            actionBtn.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        }

        actionBtn.setOnAction(e -> showInfoAlert(title, "Executing AI recommendation for: " + title));

        card.getChildren().addAll(topRow, cardTitle, cardDesc, spacer, actionBtn);
        return card;
    }

    // ==========================================
    // RIGHT COLUMN WIDGETS
    // ==========================================
    private VBox createRiskAnalysisCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🎯 Risk Analysis");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label updated = new Label("Updated 10m ago");
        updated.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, spacer, updated);

        VBox rows = new VBox(10);
        rows.getChildren().addAll(
            createRiskRow("⏰ Scheduling", "SAFE", GREEN_TEXT, GREEN_BG),
            createRiskRow("☁️ Weather", "WARNING", ORANGE_TEXT, ORANGE_BG),
            createRiskRow("🔊 AV Vendor", "WARNING", ORANGE_TEXT, ORANGE_BG),
            createRiskRow("📊 Budget", "SAFE", GREEN_TEXT, GREEN_BG),
            createRiskRow("👥 Capacity", "SAFE", GREEN_TEXT, GREEN_BG)
        );

        card.getChildren().addAll(header, new Separator(), rows);
        return card;
    }

    private HBox createRiskRow(String labelText, String badgeText, String textCol, String bgCol) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label l = new Label(labelText);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + bgCol + "; -fx-text-fill: " + textCol + "; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");

        row.getChildren().addAll(l, spacer, badge);
        return row;
    }

    private VBox createBudgetInsightCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label title = new Label("📊 Budget Insight");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox valRow = new HBox();
        valRow.setAlignment(Pos.CENTER_LEFT);
        Label appLbl = new Label("Total Approved");
        appLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label valLbl = new Label("₹5,00,000");
        valLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        valRow.getChildren().addAll(appLbl, spacer, valLbl);

        // Budget breakdown bar
        ProgressBar pb = new ProgressBar(0.80);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + "; -fx-pref-height: 8;");

        HBox legendRow = new HBox(12);
        legendRow.setPadding(new Insets(4, 0, 0, 0));
        legendRow.getChildren().addAll(
            createLegend("Spent", "₹2.25L", PRIMARY_BLUE),
            createLegend("Allocated", "₹1.75L", BRIGHT_BLUE),
            createLegend("Remaining", "₹1.00L", "#A0AEC0")
        );

        VBox noteBox = new VBox();
        noteBox.setPadding(new Insets(10));
        noteBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6;");

        Label note = new Label("Budget allocation is tracking well. Main action: Monitor AV vendor.");
        note.setWrapText(true);
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        noteBox.getChildren().add(note);

        card.getChildren().addAll(title, valRow, pb, legendRow, noteBox);
        return card;
    }

    private VBox createLegend(String title, String val, String colorHex) {
        VBox box = new VBox(1);
        Label t = new Label("● " + title);
        t.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorHex + "; -fx-font-weight: bold;");
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().addAll(t, v);
        return box;
    }

    // ==========================================
    // BOTTOM AI PROMPT & CHAT BAR
    // ==========================================
    private VBox createAIChatBar() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        // Quick Prompt Pills
        FlowPane quickPrompts = new FlowPane();
        quickPrompts.setHgap(8);
        quickPrompts.setVgap(8);

        Label askLbl = new Label("ASK AI:");
        askLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 4 0 0 0;");

        quickPrompts.getChildren().addAll(
            askLbl,
            createPromptChip("How can I reduce the budget?"),
            createPromptChip("Suggest an indoor backup plan"),
            createPromptChip("Draft a speaker confirmation email"),
            createPromptChip("Generate Checklist"),
            createPromptChip("Optimize Budget"),
            createPromptChip("Suggest Vendors")
        );

        // Input Box
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setPadding(new Insets(8, 14, 8, 14));
        inputBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20; -fx-background-radius: 20;");

        Label sparkIcon = new Label("✨ ");
        sparkIcon.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 14px;");

        aiPromptInput = new TextField();
        aiPromptInput.setPromptText("Ask EventXpert AI to analyze, generate, or recommend...");
        aiPromptInput.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 13px;");
        HBox.setHgrow(aiPromptInput, Priority.ALWAYS);

        Button sendBtn = new Button("➢");
        sendBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        sendBtn.setOnAction(e -> handleAISend());

        inputBox.getChildren().addAll(sparkIcon, aiPromptInput, sendBtn);

        container.getChildren().addAll(quickPrompts, inputBox);
        return container;
    }

    private Button createPromptChip(String text) {
        Button chip = new Button(text);
        chip.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 16; -fx-background-radius: 16; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + "; -fx-cursor: hand;");
        chip.setOnAction(e -> {
            aiPromptInput.setText(text);
            handleAISend();
        });
        return chip;
    }

    private void handleAISend() {
        String query = aiPromptInput.getText().trim();
        if (!query.isEmpty()) {
            showInfoAlert("EventXpert AI", "AI Recommendation for: \"" + query + "\"\n\nGenerating custom plan response...");
            aiPromptInput.clear();
        }
    }

    // ==========================================
    // UTILITIES
    // ==========================================
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}