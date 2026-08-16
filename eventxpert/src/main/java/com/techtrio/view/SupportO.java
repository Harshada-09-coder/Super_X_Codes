package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SupportO extends Application {

    // Color Palette Constants
    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String BRIGHT_BLUE = "#2563C7";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";

    // Data Structure for Support Ticket
    private static class TicketData {
        String id;
        String issue;
        String category;
        String priority;
        String created;
        String status;

        TicketData(String id, String issue, String category, String priority, String created, String status) {
            this.id = id;
            this.issue = issue;
            this.category = category;
            this.priority = priority;
            this.created = created;
            this.status = status;
        }
    }

    private final List<TicketData> myTickets = new ArrayList<>();
    private VBox ticketsTableContainer;

    @Override
    public void start(Stage primaryStage) {
        initSampleTickets();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Content inside ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Assemble Sections
        mainContent.getChildren().addAll(
                createHeroSearchSection(primaryStage),
                createBrowseCategoriesAndArticlesRow(primaryStage),
                createBlueHelpBanner(primaryStage),
                createAiAssistantAndEmergencyRow(primaryStage),
                createTicketAndFaqRow(primaryStage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Help & Support Center");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initSampleTickets() {
        myTickets.clear();
        myTickets.add(new TicketData("EX-1024", "Schedule Conflict Resolution", "Schedule", "High", "Today", "In Progress"));
        myTickets.add(new TicketData("EX-1018", "Vendor Quote Confirmation", "Vendors", "Medium", "Yesterday", "Resolved"));
        myTickets.add(new TicketData("EX-1012", "QR Scanner Device Sync", "QR Check-In", "High", "2 days ago", "Resolved"));
    }

    // ==========================================
    // 1. SIDEBAR NAVIGATION
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(6);

        Label brandLabel = new Label("EventXpert");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label brandSub = new Label("Enterprise Manager");
        brandSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        VBox brandBox = new VBox(2, brandLabel, brandSub);
        brandBox.setPadding(new Insets(0, 8, 16, 8));

        sidebar.getChildren().add(brandBox);

        sidebar.getChildren().add(createSidebarCategory("MAIN"));
        sidebar.getChildren().add(createSidebarItem("Dashboard", false, e -> showToast(stage, "Opening Dashboard...")));
        sidebar.getChildren().add(createSidebarItem("Event Requests", false, e -> showToast(stage, "Opening Event Requests...")));
        sidebar.getChildren().add(createSidebarItem("My Events", false, e -> showToast(stage, "Opening My Events...")));

        sidebar.getChildren().add(createSidebarCategory("EVENT PLANNING"));
        sidebar.getChildren().add(createSidebarItem("AI Planner", false, e -> showToast(stage, "Opening AI Planner...")));
        sidebar.getChildren().add(createSidebarItem("Budget", false, e -> showToast(stage, "Opening Budget...")));
        sidebar.getChildren().add(createSidebarItem("Venue", false, e -> showToast(stage, "Opening Venue...")));
        sidebar.getChildren().add(createSidebarItem("Vendors", false, e -> showToast(stage, "Opening Vendors...")));
        sidebar.getChildren().add(createSidebarItem("Schedule", false, e -> showToast(stage, "Opening Schedule...")));

        sidebar.getChildren().add(createSidebarCategory("EXECUTION"));
        sidebar.getChildren().add(createSidebarItem("Guest Management", false, e -> showToast(stage, "Opening Guest Management...")));
        sidebar.getChildren().add(createSidebarItem("QR Check-In", false, e -> showToast(stage, "Opening QR Check-In...")));
        sidebar.getChildren().add(createSidebarItem("Live Event", false, e -> showToast(stage, "Opening Live Event...")));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().add(createSidebarItem("Settings", false, e -> showToast(stage, "Opening Settings...")));

        // Highlighted Help Center Menu
        HBox helpMenuRow = new HBox(8);
        helpMenuRow.setAlignment(Pos.CENTER_LEFT);
        helpMenuRow.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 6px; -fx-padding: 8 12;");
        Label helpText = new Label("❓ Help Center");
        helpText.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        helpMenuRow.getChildren().add(helpText);

        // Profile Box
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(10));
        profileBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        Text avatarText = new Text("OP");
        avatarText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Admin User");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label userEmail = new Label("admin@eventxpert.com");
        userEmail.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        userDetails.getChildren().addAll(userName, userEmail);

        profileBox.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, helpMenuRow, profileBox);
        return sidebar;
    }

    private Label createSidebarCategory(String title) {
        Label cat = new Label(title);
        cat.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 12 8 4 8;");
        return cat;
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 13px; -fx-padding: 8 12;"));
        return btn;
    }

    // ==========================================
    // 2. TOP BAR
    // ==========================================
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(16);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search help, articles, FAQs...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "No new system alerts"));

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("OP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, notifBtn, helpBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. HERO SEARCH SECTION (Matching Stitch Design)
    // ==========================================
    private VBox createHeroSearchSection(Stage stage) {
        VBox heroCard = createCardContainer();
        heroCard.setAlignment(Pos.CENTER);
        heroCard.setPadding(new Insets(28, 20, 28, 20));

        Label systemBadge = new Label("🟢 All systems operational");
        systemBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12px;");

        Label title = new Label("How can we help you?");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Find answers, explore guides, or contact our support team.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setMaxWidth(620);
        searchBox.setPadding(new Insets(10, 0, 0, 0));

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search for articles, guides, and FAQs...");
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 10 16; -fx-font-size: 13px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        Button searchBtn = createPrimaryButton("Search");
        searchBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 20px; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> {
            if (!searchInput.getText().isEmpty()) {
                openArticleModal(stage, "Search Results", "Showing articles matching: '" + searchInput.getText() + "'");
            }
        });

        searchBox.getChildren().addAll(searchInput, searchBtn);

        // Popular Chips
        HBox chipsRow = new HBox(8);
        chipsRow.setAlignment(Pos.CENTER);
        Label popLabel = new Label("Popular:");
        popLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        chipsRow.getChildren().add(popLabel);

        String[] popularChips = {"Event creation", "QR Check-In", "Budget", "Vendors"};
        for (String chipText : popularChips) {
            Label chip = new Label(chipText);
            chip.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand; -fx-underline: true;");
            chip.setOnMouseClicked(e -> openArticleModal(stage, chipText, "Guide and instructions for " + chipText + " in EventXpert."));
            chipsRow.getChildren().add(chip);
        }

        heroCard.getChildren().addAll(systemBadge, title, subtitle, searchBox, chipsRow);
        return heroCard;
    }

    // ==========================================
    // 4. BROWSE CATEGORIES & POPULAR ARTICLES
    // ==========================================
    private HBox createBrowseCategoriesAndArticlesRow(Stage stage) {
        HBox row = new HBox(20);

        // Browse Categories Grid (Left)
        VBox catSection = new VBox(12);
        HBox.setHgrow(catSection, Priority.ALWAYS);

        Label catTitle = new Label("Browse Categories");
        catTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(16);

        grid.add(createCategoryCard(stage, "📋 Event Management", "Guides on creating, duplicating, and managing event lifecycles."), 0, 0);
        grid.add(createCategoryCard(stage, "🤖 AI Planner", "Leverage AI for scheduling, seating charts, and predictive analytics."), 1, 0);
        grid.add(createCategoryCard(stage, "💼 Budget & Finance", "Tracking expenses, managing invoices, and ROI reporting."), 0, 1);
        grid.add(createCategoryCard(stage, "🏢 Vendors & Venue", "Procurement, contracts, and floor plan management tools."), 1, 1);

        catSection.getChildren().addAll(catTitle, grid);

        // Popular Articles Card (Right - matching Stitch Design)
        VBox articlesCard = createCardContainer();
        articlesCard.setPrefWidth(380);

        Label artTitle = new Label("📈 Popular Articles");
        artTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox articleList = new VBox(10);
        articleList.getChildren().addAll(
                createArticleLink(stage, "📄 How do I create a new event?"),
                createArticleLink(stage, "📄 How does QR Check-In work?"),
                createArticleLink(stage, "📄 Importing guest lists via CSV")
        );

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);

        Label viewAll = new Label("View all articles →");
        viewAll.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        viewAll.setOnMouseClicked(e -> showToast(stage, "Opening complete knowledge base..."));

        articlesCard.getChildren().addAll(artTitle, new Separator(), articleList, sp, viewAll);

        row.getChildren().addAll(catSection, articlesCard);
        return row;
    }

    private VBox createCategoryCard(Stage stage, String title, String desc) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(t, d);
        card.setOnMouseClicked(e -> openArticleModal(stage, title, desc));
        return card;
    }

    private HBox createArticleLink(Stage stage, String title) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-cursor: hand;");

        Label text = new Label(title);
        text.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label arrow = new Label("›"); arrow.setStyle("-fx-font-size: 14px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        row.getChildren().addAll(text, sp, arrow);
        row.setOnMouseClicked(e -> openArticleModal(stage, title, "Detailed step-by-step article content for: " + title));
        return row;
    }

    // ==========================================
    // 5. BLUE "STILL NEED HELP?" BANNER (Matching Stitch Design)
    // ==========================================
    private HBox createBlueHelpBanner(Stage stage) {
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(24));
        banner.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 12px;");

        VBox textGroup = new VBox(4);
        Label title = new Label("Still need help?");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Our enterprise support team is available 24/7 to assist you with complex event configurations.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #DCE5F0;");

        textGroup.getChildren().addAll(title, subtitle);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button liveChatBtn = new Button("💬 Live Chat");
        liveChatBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8px; -fx-cursor: hand;");
        liveChatBtn.setOnAction(e -> showToast(stage, "Connecting to Live Support Chat Agent..."));

        Button emailBtn = new Button("✉ Email Support");
        emailBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 8px; -fx-font-weight: bold; -fx-padding: 9 19; -fx-cursor: hand;");
        emailBtn.setOnAction(e -> openReportTicketModal(stage));

        banner.getChildren().addAll(textGroup, sp, liveChatBtn, emailBtn);
        return banner;
    }

    // ==========================================
    // 6. AI ASSISTANT & EMERGENCY SUPPORT
    // ==========================================
    private HBox createAiAssistantAndEmergencyRow(Stage stage) {
        HBox row = new HBox(20);

        // AI Help Card (Left)
        VBox aiCard = createCardContainer();
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        aiCard.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");

        Label aiTitle = new Label("✨ EventXpert AI Help Assistant");
        aiTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label aiDesc = new Label("Type your problem below to get instant AI troubleshooting guidance:");
        aiDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        HBox inputGroup = new HBox(8);
        TextField aiInput = new TextField();
        aiInput.setPromptText("e.g. schedule conflict, vendor payment, QR scanner...");
        aiInput.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 8 12;");
        HBox.setHgrow(aiInput, Priority.ALWAYS);

        Button askBtn = createPrimaryButton("Ask AI");
        inputGroup.getChildren().addAll(aiInput, askBtn);

        Label aiOutput = new Label("💡 AI Suggestion will appear here.");
        aiOutput.setWrapText(true);
        aiOutput.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 6 0 0 0;");

        askBtn.setOnAction(e -> {
            String query = aiInput.getText().toLowerCase();
            if (query.contains("schedule")) {
                aiOutput.setText("💡 AI Response: Open Schedule -> Conflict Detection to review and resolve overlapping sessions.");
            } else if (query.contains("vendor")) {
                aiOutput.setText("💡 AI Response: Open Vendors -> Find Vendors to discover and compare available service providers.");
            } else if (query.contains("budget")) {
                aiOutput.setText("💡 AI Response: Navigate to Budget to adjust allocated vendor expenses and payment due dates.");
            } else if (query.contains("qr")) {
                aiOutput.setText("💡 AI Response: Ensure your device camera permissions are enabled in Guest Management -> QR Check-In.");
            } else {
                aiOutput.setText("💡 AI Response: Please contact EventXpert Enterprise Support for personalized assistance.");
            }
        });

        aiCard.getChildren().addAll(aiTitle, aiDesc, inputGroup, aiOutput);

        // Emergency Event-Day Support Card (Right)
        VBox emergencyCard = createCardContainer();
        emergencyCard.setPrefWidth(380);

        Label emTitle = new Label("🚨 Event-Day Emergency Support");
        emTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label emDesc = new Label("Priority hotline for live events experiencing immediate operational issues.");
        emDesc.setWrapText(true);
        emDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label phoneLbl = new Label("📞 Hotline: +91 1800 123 456");
        phoneLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Button getHelpBtn = createSecondaryButton("Get Emergency Help");
        getHelpBtn.setOnAction(e -> showToast(stage, "Routing call to Event-Day Emergency Support Manager..."));

        emergencyCard.getChildren().addAll(emTitle, emDesc, phoneLbl, getHelpBtn);

        row.getChildren().addAll(aiCard, emergencyCard);
        return row;
    }

    // ==========================================
    // 7. SUPPORT TICKETS & FAQ ROW
    // ==========================================
    private HBox createTicketAndFaqRow(Stage stage) {
        HBox row = new HBox(20);

        // My Support Tickets Table Card (Left)
        VBox ticketsCard = createCardContainer();
        HBox.setHgrow(ticketsCard, Priority.ALWAYS);

        HBox tHeader = new HBox();
        Label tTitle = new Label("My Support Requests");
        tTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button newTicketBtn = createPrimaryButton("+ Report Issue");
        newTicketBtn.setOnAction(e -> openReportTicketModal(stage));

        tHeader.getChildren().addAll(tTitle, sp, newTicketBtn);

        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(12);
        tableHeader.setPadding(new Insets(8, 10, 8, 10));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addGridHeader(tableHeader, "Ticket ID", 0, 80);
        addGridHeader(tableHeader, "Issue Title", 1, 160);
        addGridHeader(tableHeader, "Category", 2, 90);
        addGridHeader(tableHeader, "Priority", 3, 70);
        addGridHeader(tableHeader, "Status", 4, 90);

        ticketsTableContainer = new VBox(4);
        renderTicketsTable();

        ticketsCard.getChildren().addAll(tHeader, tableHeader, ticketsTableContainer);

        // Accordion FAQ Card (Right)
        VBox faqCard = createCardContainer();
        faqCard.setPrefWidth(380);

        Label faqTitle = new Label("Frequently Asked Questions");
        faqTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox faqList = new VBox(8);
        faqList.getChildren().addAll(
                createFaqItem("How do I create a new event?", "Go to Event Requests or My Events and click + Create Event to fill details."),
                createFaqItem("How do I resolve schedule conflicts?", "Open Schedule to view detected overlapping slots and apply auto-solutions."),
                createFaqItem("Can I export vendor contracts?", "Yes, open Vendor Management to view and download attached documents.")
        );

        faqCard.getChildren().addAll(faqTitle, new Separator(), faqList);

        row.getChildren().addAll(ticketsCard, faqCard);
        return row;
    }

    private void addGridHeader(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void renderTicketsTable() {
        ticketsTableContainer.getChildren().clear();

        for (TicketData t : myTickets) {
            GridPane row = new GridPane();
            row.setHgap(12);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            Label idLbl = new Label(t.id); idLbl.setPrefWidth(80); idLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
            Label issueLbl = new Label(t.issue); issueLbl.setPrefWidth(160); issueLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label catLbl = new Label(t.category); catLbl.setPrefWidth(90); catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            Label prioLbl = new Label(t.priority); prioLbl.setPrefWidth(70); prioLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            Label statusBadge = new Label(t.status); statusBadge.setPrefWidth(90);
            if (t.status.equals("Resolved")) {
                statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-font-size: 10px; -fx-font-weight: bold;");
            } else {
                statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-font-size: 10px; -fx-font-weight: bold;");
            }

            row.add(idLbl, 0, 0);
            row.add(issueLbl, 1, 0);
            row.add(catLbl, 2, 0);
            row.add(prioLbl, 3, 0);
            row.add(statusBadge, 4, 0);

            ticketsTableContainer.getChildren().add(row);
        }
    }

    private VBox createFaqItem(String question, String answer) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label q = new Label("❓ " + question);
        q.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + NAVY + "; -fx-cursor: hand;");

        Label a = new Label(answer);
        a.setWrapText(true);
        a.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(q, a);
        return box;
    }

    // ==========================================
    // MODAL DIALOGS
    // ==========================================
    private void openArticleModal(Stage parentStage, String titleStr, String contentStr) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle(titleStr);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label(titleStr);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label content = new Label(contentStr);
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Button closeBtn = createSecondaryButton("Close Article");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, new Separator(), content, new Separator(), closeBtn);

        Scene scene = new Scene(layout, 400, 260);
        modal.setScene(scene);
        modal.show();
    }

    private void openReportTicketModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Report an Issue");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("📝 Submit Support Ticket");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField subjectInput = new TextField(); subjectInput.setPromptText("Issue Summary");
        ComboBox<String> catCB = new ComboBox<>();
        catCB.getItems().addAll("Schedule", "Vendors", "Budget", "QR Check-In", "Technical Issue");
        catCB.setValue("Technical Issue");

        TextArea descInput = new TextArea(); descInput.setPromptText("Describe your problem in detail...");
        descInput.setPrefRowCount(3);

        Button submitBtn = createPrimaryButton("Submit Ticket");
        submitBtn.setOnAction(e -> {
            if (!subjectInput.getText().isEmpty()) {
                String newId = "EX-" + (1025 + myTickets.size());
                myTickets.add(0, new TicketData(newId, subjectInput.getText(), catCB.getValue(), "Medium", "Today", "In Progress"));
                renderTicketsTable();
                showToast(parentStage, "✓ Ticket " + newId + " created successfully!");
                modal.close();
            } else {
                showToast(parentStage, "Please enter an issue summary.");
            }
        });

        layout.getChildren().addAll(title, new Label("Subject:"), subjectInput, new Label("Category:"), catCB, new Label("Details:"), descInput, submitBtn);

        Scene scene = new Scene(layout, 380, 360);
        modal.setScene(scene);
        modal.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 2);");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 7 15; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Support");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}