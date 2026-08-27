package com.techtrio.view;

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

public class SupportV {
    private Scene supportVScene;
    private VendorDashboardV vendorDashboard;

    // Color Palette Constants
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
    // Data Structure for Support Ticket
    private static class TicketData {
        String id;
        String subject;
        String category;
        String date;
        String priority;
        String status;

        TicketData(String id, String subject, String category, String date, String priority, String status) {
            this.id = id;
            this.subject = subject;
            this.category = category;
            this.date = date;
            this.priority = priority;
            this.status = status;
        }
    }
    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    private final List<TicketData> ticketList = new ArrayList<>();
    private VBox ticketsTableContainer;

    public Scene getsupportVScene(Stage primaryStage){
        initSampleTickets();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left - Fixed 250px Full Height)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Main Application Container
        BorderPane mainArea = new BorderPane();
        mainArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Top Bar
        HBox topBar = createTopBar(primaryStage);
        mainArea.setTop(topBar);

        // Scrollable Center Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Build Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createSupportHeroCard(primaryStage),
                createSupportStatsRow(),
                createTwoColumnMainLayout(primaryStage),
                createFooter()
        );

        scrollPane.setContent(mainContent);
        mainArea.setCenter(scrollPane);

        root.setCenter(mainArea);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        supportVScene = scene;
        return supportVScene;
    }

    private void initSampleTickets() {
        ticketList.clear();
        ticketList.add(new TicketData("SUP-2026-00128", "Payment not reflected in balance", "Payment", "18 Aug 2026", "High", "Open"));
        ticketList.add(new TicketData("SUP-2026-00124", "Booking schedule conflict review", "Booking", "17 Aug 2026", "Medium", "In Progress"));
        ticketList.add(new TicketData("SUP-2026-00118", "Service package update request", "Services", "15 Aug 2026", "Low", "Resolved"));
    }

    // ==========================================
    // 1. SIDEBAR (FULL HEIGHT)
    // ==========================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setSpacing(6);

        // Logo / Branding Area
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("E");
        logoIcon.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 4 10; -fx-background-radius: 6px;");
        
        VBox brandBox = new VBox(0);
        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label brandSub = new Label("VENDOR PORTAL");
        brandSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(brandTitle, brandSub);
        logoBox.getChildren().addAll(logoIcon, brandBox);

        // Top Action Button
        Button addServiceBtn = new Button("+ Add Service");
        addServiceBtn.setMaxWidth(Double.MAX_VALUE);
        addServiceBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");
        addServiceBtn.setOnAction(e -> showToast(stage, "Add Service Clicked"));

        sidebar.getChildren().addAll(logoBox, new Region(), addServiceBtn);

        // Menu Sections
        sidebar.getChildren().add(createSidebarItem("📊 Dashboard", false, e -> {showToast(stage, "Opening Dashboard...");
           if (vendorDashboard != null) {
                vendorDashboard.backToVendorDashboard();
            } else {
                VendorDashboardV dashboard = new VendorDashboardV();
                Scene dashboardScene = dashboard.getvendorDashboardScene();
                if (LandingPage.landingPageStage != null) {
                    LandingPage.landingPageStage.setScene(dashboardScene);
                }
            }    
    }));
        sidebar.getChildren().add(createSidebarItem("📋 Service Requests", false, e -> showToast(stage, "Opening Service Requests...")));
        sidebar.getChildren().add(createSidebarItem("📱 My Bookings", false, e -> showToast(stage, "Opening My Bookings...")));
        sidebar.getChildren().add(createSidebarItem("📦 Services & Packages", false, e -> showToast(stage, "Opening Services...")));
        sidebar.getChildren().add(createSidebarItem("📅 Calendar", false, e -> showToast(stage, "Opening Calendar...")));
        sidebar.getChildren().add(createSidebarItem("💵 Earnings & Payments", false, e -> showToast(stage, "Opening Earnings...")));
        sidebar.getChildren().add(createSidebarItemWithBadge("🔔 Notifications", "4"));
        sidebar.getChildren().add(createSidebarItem("⚙ Settings", false, e -> showToast(stage, "Opening Settings...")));
        sidebar.getChildren().add(createSidebarItem("❓Support", true, e -> {})); // Active Menu

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom Vendor Profile Pill
        HBox profilePill = new HBox(10);
        profilePill.setAlignment(Pos.CENTER_LEFT);
        profilePill.setPadding(new Insets(10));
        profilePill.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(WHITE));
        Text avatarText = new Text("VP");
        avatarText.setStyle("-fx-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("VisionPro Events");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        Label userSub = new Label("Audio & Visual • ● Available");
        userSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #4ADE80;");
        userDetails.getChildren().addAll(userName, userSub);

        profilePill.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profilePill);
        return sidebar;
    }


    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);

        if (isActive) {
            btn.setStyle("-fx-background-color: white; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 8 12;"));
        }
        return btn;
    }

    private HBox createSidebarItemWithBadge(String title, String badgeCount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Button btn = createSidebarItem(title, false, e -> {});
        HBox.setHgrow(btn, Priority.ALWAYS);

        Label badge = new Label(badgeCount);
        badge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 10px;");

        row.getChildren().addAll(btn, badge);
        return row;
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
        searchField.setPromptText("🔍 Search help articles, FAQs, support...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button helpBtn = new Button("❓"); helpBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button notifBtn = new Button("🔔"); notifBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Button settingsBtn = new Button("⚙"); settingsBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("VP");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, helpBtn, notifBtn, settingsBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox header = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Support");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button myTicketsBtn = createSecondaryButton("My Support Tickets");
        myTicketsBtn.setOnAction(e -> showToast(stage, "Scrolling to My Support Tickets..."));

        Button contactBtn = createPrimaryButton("Contact Support");
        contactBtn.setOnAction(e -> openTicketModal(stage));

        titleRow.getChildren().addAll(title, spacer, myTicketsBtn, contactBtn);

        Label subtitle = new Label("Find answers, get assistance and resolve issues quickly.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    // ==========================================
    // 4. SUPPORT HERO CARD (MATCHING STITCH)
    // ==========================================
    private VBox createSupportHeroCard(Stage stage) {
        VBox card = createCardContainer();
        card.setPadding(new Insets(24));

        Label title = new Label("How can we help you?");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search for help, e.g. payments, bookings, services...");
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-padding: 10 14; -fx-font-size: 13px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        Button searchBtn = createPrimaryButton("Search");
        searchBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8px;");
        searchBtn.setOnAction(e -> {
            if (!searchInput.getText().isEmpty()) {
                showToast(stage, "Showing help articles matching: " + searchInput.getText());
            }
        });

        searchBox.getChildren().addAll(searchInput, searchBtn);

        // Suggestion Chips
        HBox chipsRow = new HBox(8);
        chipsRow.setAlignment(Pos.CENTER_LEFT);
        Label suggestLabel = new Label("Suggested:");
        suggestLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        chipsRow.getChildren().add(suggestLabel);

        String[] suggestions = {"How to accept service requests?", "Payment processing time", "Block unavailable dates"};
        for (String s : suggestions) {
            Label chip = new Label(s);
            chip.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand; -fx-underline: true;");
            chip.setOnMouseClicked(e -> showToast(stage, "Opening guide: " + s));
            chipsRow.getChildren().add(chip);
        }

        card.getChildren().addAll(title, searchBox, chipsRow);
        return card;
    }

    // ==========================================
    // 5. SUPPORT STATISTICS ROW
    // ==========================================
    private HBox createSupportStatsRow() {
        HBox row = new HBox(14);

        row.getChildren().addAll(
                createStatCard("OPEN TICKETS", "2", "Needs attention", PRIMARY_BLUE, "📩"),
                createStatCard("IN PROGRESS", "1", "Being reviewed", WARNING_TEXT, "⏱"),
                createStatCard("RESOLVED", "18", "This month", GREEN_TEXT, "✓")
        );

        return row;
    }

    private VBox createStatCard(String title, String val, String sub, String colorHex, String iconStr) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        HBox top = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label ic = new Label(iconStr);
        ic.setStyle("-fx-font-size: 14px; -fx-background-color: " + LIGHT_BLUE + "; -fx-padding: 4 8; -fx-background-radius: 50%;");
        top.getChildren().addAll(t, sp, ic);

        Label v = new Label(val);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, v, s);
        return card;
    }

    // ==========================================
    // 6. TWO-COLUMN MAIN LAYOUT
    // ==========================================
    private HBox createTwoColumnMainLayout(Stage stage) {
        HBox layout = new HBox(20);

        // Left Column (~2/3 Width)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createQuickHelpCategoriesGrid(stage),
                createFaqCard(),
                createMyTicketsCard(stage)
        );

        // Right Column (~1/3 Width)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        rightCol.getChildren().addAll(
                createHelpAssistantCard(stage),
                createContactChannelsCard(stage),
                createEventDayPriorityCard(stage),
                createStatusAndHoursCard()
        );

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createQuickHelpCategoriesGrid(Stage stage) {
        VBox container = new VBox(12);

        Label title = new Label("Quick Help Topics");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(14);

        grid.add(createCategoryCard(stage, "📋 Service Requests", "Manage and respond to client inquiries.", "12 Articles →"), 0, 0);
        grid.add(createCategoryCard(stage, "📱 My Bookings", "Track your confirmed event schedule.", "18 Articles →"), 1, 0);
        grid.add(createCategoryCard(stage, "📦 Services & Packages", "Manage pricing and service packages.", "10 Articles →"), 2, 0);

        grid.add(createCategoryCard(stage, "📅 Calendar & Availability", "Sync and manage your availability.", "8 Articles →"), 0, 1);
        grid.add(createCategoryCard(stage, "💵 Earnings & Payments", "View invoices and payout history.", "15 Articles →"), 1, 1);
        grid.add(createCategoryCard(stage, "👤 Account & Settings", "Manage vendor account details.", "9 Articles →"), 2, 1);

        container.getChildren().addAll(title, grid);
        return container;
    }

    private VBox createCategoryCard(Stage stage, String topic, String desc, String linkText) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(14));

        Label t = new Label(topic);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label link = new Label(linkText);
        link.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        link.setOnMouseClicked(e -> showToast(stage, "Opening articles for " + topic));

        card.getChildren().addAll(t, d, new Separator(), link);
        return card;
    }

    private VBox createFaqCard() {
        VBox card = createCardContainer();

        Label title = new Label("Popular Questions");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox faqList = new VBox(8);
        faqList.getChildren().addAll(
                createExpandableFaq("How do I respond to a service request?", "Open Service Requests, review the event details, and click Accept Request or Reject Request."),
                createExpandableFaq("How can I add a new service or package?", "Go to Services & Packages and click + Add Service to list new offerings."),
                createExpandableFaq("How do I block an unavailable date on my calendar?", "Open Calendar and click + Add Unavailable Date to block dates."),
                createExpandableFaq("When will my earnings be transferred to my bank?", "Payments are credited to your wallet upon booking confirmation and paid out on your schedule.")
        );

        card.getChildren().addAll(title, new Separator(), faqList);
        return card;
    }

    private VBox createExpandableFaq(String question, String answer) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        HBox qRow = new HBox();
        Label q = new Label("❓ " + question);
        q.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + "; -fx-cursor: hand;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label toggleIcon = new Label("▼"); toggleIcon.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        qRow.getChildren().addAll(q, sp, toggleIcon);

        Label a = new Label(answer);
        a.setWrapText(true);
        a.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(qRow, a);

        qRow.setOnMouseClicked(e -> {
            a.setVisible(!a.isVisible());
            a.setManaged(a.isVisible());
            toggleIcon.setText(a.isVisible() ? "▲" : "▼");
        });

        return box;
    }

    private VBox createMyTicketsCard(Stage stage) {
        VBox card = createCardContainer();

        HBox head = new HBox();
        Label title = new Label("My Support Requests");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button newTicketBtn = createPrimaryButton("+ Report Issue");
        newTicketBtn.setOnAction(e -> openTicketModal(stage));

        head.getChildren().addAll(title, sp, newTicketBtn);

        GridPane tableHeader = new GridPane();
        tableHeader.setHgap(12);
        tableHeader.setPadding(new Insets(8, 10, 8, 10));
        tableHeader.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        addTableHeader(tableHeader, "TICKET ID", 0, 100);
        addTableHeader(tableHeader, "SUBJECT", 1, 160);
        addTableHeader(tableHeader, "CATEGORY", 2, 90);
        addTableHeader(tableHeader, "STATUS", 3, 90);
        addTableHeader(tableHeader, "ACTION", 4, 60);

        ticketsTableContainer = new VBox(4);
        renderTicketsTable(stage);

        card.getChildren().addAll(head, tableHeader, ticketsTableContainer);
        return card;
    }

    private void addTableHeader(GridPane grid, String text, int col, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        grid.add(l, col, 0);
    }

    private void renderTicketsTable(Stage stage) {
        ticketsTableContainer.getChildren().clear();

        for (TicketData t : ticketList) {
            GridPane row = new GridPane();
            row.setHgap(12);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            Label idLbl = new Label(t.id); idLbl.setPrefWidth(100); idLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");
            Label subLbl = new Label(t.subject); subLbl.setPrefWidth(160); subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
            Label catLbl = new Label(t.category); catLbl.setPrefWidth(90); catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            Label statusBadge = new Label(t.status); statusBadge.setPrefWidth(90);
            if (t.status.equals("Resolved")) {
                statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-font-size: 10px; -fx-font-weight: bold;");
            } else if (t.status.equals("In Progress")) {
                statusBadge.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-font-size: 10px; -fx-font-weight: bold;");
            } else {
                statusBadge.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-padding: 2 6; -fx-background-radius: 8px; -fx-font-size: 10px; -fx-font-weight: bold;");
            }

            Button viewBtn = createSecondaryButton("View");
            viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 2 6;");
            viewBtn.setOnAction(e -> openTicketDetailsModal(stage, t));

            row.add(idLbl, 0, 0);
            row.add(subLbl, 1, 0);
            row.add(catLbl, 2, 0);
            row.add(statusBadge, 3, 0);
            row.add(viewBtn, 4, 0);

            ticketsTableContainer.getChildren().add(row);
        }
    }

    // ==========================================
    // RIGHT COLUMN CARDS
    // ==========================================
    private VBox createHelpAssistantCard(Stage stage) {
        VBox card = createCardContainer();
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");

        Label title = new Label("✨ EventXpert Help Assistant");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label desc = new Label("Based on your recent activity, here are recommended resources:");
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox links = new VBox(4);
        links.getChildren().addAll(
                createAssistantLink(stage, "• Managing service requests"),
                createAssistantLink(stage, "• Understanding payment schedules"),
                createAssistantLink(stage, "• Resolving calendar conflicts")
        );

        card.getChildren().addAll(title, desc, links);
        return card;
    }

    private Label createAssistantLink(Stage stage, String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-cursor: hand;");
        l.setOnMouseClicked(e -> showToast(stage, "Opening resource: " + text));
        return l;
    }

    private VBox createContactChannelsCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Contact EventXpert Support");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox liveChatBox = new VBox(4);
        Label chatT = new Label("💬 Live Chat (09:00 AM - 09:00 PM)"); chatT.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        Button chatBtn = createPrimaryButton("Start Chat");
        chatBtn.setOnAction(e -> openChatModal(stage));
        liveChatBox.getChildren().addAll(chatT, chatBtn);

        VBox emailBox = new VBox(4);
        Label emailT = new Label("✉ Email: support@eventxpert.com"); emailT.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Button emailBtn = createSecondaryButton("Send Email");
        emailBtn.setOnAction(e -> openTicketModal(stage));
        emailBox.getChildren().addAll(emailT, emailBtn);

        card.getChildren().addAll(title, new Separator(), liveChatBox, new Separator(), emailBox);
        return card;
    }

    private VBox createEventDayPriorityCard(Stage stage) {
        VBox card = createCardContainer();
        card.setStyle("-fx-background-color: " + WARNING_BG + "; -fx-background-radius: 12px; -fx-border-color: #FCD34D; -fx-border-radius: 12px;");

        Label title = new Label("🚨 Event-Day Priority Support");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + WARNING_TEXT + ";");

        Label desc = new Label("Need urgent help during an ongoing event? Get priority assistance for critical issues.");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 10px; -fx-text-fill: " + WARNING_TEXT + ";");

        Button priorityBtn = createSecondaryButton("Get Priority Support");
        priorityBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + WARNING_TEXT + "; -fx-border-color: " + WARNING_TEXT + "; -fx-border-radius: 6px;");
        priorityBtn.setOnAction(e -> showToast(stage, "Routing to Event-Day Priority Support Manager..."));

        card.getChildren().addAll(title, desc, priorityBtn);
        return card;
    }

    private VBox createStatusAndHoursCard() {
        VBox card = createCardContainer();

        Label title = new Label("SYSTEM STATUS & HOURS");
        title.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label status = new Label("🟢 All systems operational");
        status.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_TEXT + ";");

        VBox hours = new VBox(2);
        hours.getChildren().addAll(
                new Label("• Mon - Fri: 09:00 AM - 09:00 PM"),
                new Label("• Sat - Sun: 10:00 AM - 06:00 PM")
        );
        hours.getChildren().forEach(n -> n.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        card.getChildren().addAll(title, status, new Separator(), hours);
        return card;
    }

    // ==========================================
    // 7. FOOTER
    // ==========================================
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 0, 10, 0));

        Label copy = new Label("© 2026 EventXpert Management System. All rights reserved.  •  Privacy Policy  •  Terms of Service");
        copy.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        footer.getChildren().add(copy);
        return footer;
    }

    // ==========================================
    // MODALS & DIALOGS
    // ==========================================
    private void openTicketModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Create Support Ticket");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("📝 Submit Support Ticket");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        TextField subInput = new TextField(); subInput.setPromptText("Issue Summary");
        ComboBox<String> catCB = new ComboBox<>();
        catCB.getItems().addAll("Service Request", "Booking", "Payment", "Calendar", "Technical Issue");
        catCB.setValue("Technical Issue");

        TextArea descInput = new TextArea(); descInput.setPromptText("Describe your problem in detail...");
        descInput.setPrefRowCount(3);

        Button submitBtn = createPrimaryButton("Submit Ticket");
        submitBtn.setOnAction(e -> {
            if (!subInput.getText().isEmpty()) {
                String newId = "SUP-2026-00" + (129 + ticketList.size());
                ticketList.add(0, new TicketData(newId, subInput.getText(), catCB.getValue(), "Today", "Medium", "Open"));
                renderTicketsTable(parentStage);
                showToast(parentStage, "✓ Ticket " + newId + " created successfully!");
                modal.close();
            } else {
                showToast(parentStage, "Please enter an issue summary.");
            }
        });

        layout.getChildren().addAll(title, new Label("Subject:"), subInput, new Label("Category:"), catCB, new Label("Details:"), descInput, submitBtn);

        Scene scene = new Scene(layout, 360, 360);
        modal.setScene(scene);
        modal.show();
    }

    private void openTicketDetailsModal(Stage parentStage, TicketData t) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Ticket Details - " + t.id);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Ticket " + t.id);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox details = new VBox(4);
        details.getChildren().addAll(
                new Label("• Subject: " + t.subject),
                new Label("• Category: " + t.category),
                new Label("• Date: " + t.date),
                new Label("• Priority: " + t.priority),
                new Label("• Status: " + t.status)
        );

        Label response = new Label("Agent Response: We are investigating your issue. An update will be posted shortly.");
        response.setWrapText(true);
        response.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Button closeBtn = createSecondaryButton("Close");
        closeBtn.setOnAction(e -> modal.close());

        layout.getChildren().addAll(title, new Separator(), details, new Separator(), response, closeBtn);

        Scene scene = new Scene(layout, 340, 280);
        modal.setScene(scene);
        modal.show();
    }

    private void openChatModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Live Support Chat");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(16));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("💬 EventXpert Support (Agent Online)");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        VBox chatBox = new VBox(6);
        chatBox.setPadding(new Insets(8));
        chatBox.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");
        chatBox.getChildren().addAll(
                new Label("Agent: Hello! How can we help you with your vendor account today?"),
                new Label("You: I have a question about payout schedules.")
        );

        TextField msgInput = new TextField(); msgInput.setPromptText("Type message...");
        Button sendBtn = createPrimaryButton("Send");
        sendBtn.setOnAction(e -> showToast(parentStage, "Message sent to agent."));

        layout.getChildren().addAll(title, chatBox, msgInput, sendBtn);

        Scene scene = new Scene(layout, 340, 260);
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
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 7 15; -fx-cursor: hand;");
        return btn;
    }

    private void showToast(Stage stage, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}