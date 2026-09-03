package com.techtrio.view;

import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SupportO {

    private Scene supportScene;
    private String uid;
    private String idToken;
    private Runnable backAction;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F8FAFC";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";
    private static final String WARNING_BG = "#FEF7E0";
    private static final String WARNING_TEXT = "#B06000";

    private static class TicketData {
        String id;
        String issue;
        String category;
        String priority;
        String status;

        TicketData(String id, String issue, String category, String priority, String status) {
            this.id = id;
            this.issue = issue;
            this.category = category;
            this.priority = priority;
            this.status = status;
        }
    }

    private final List<TicketData> myTickets = new ArrayList<>();
    private VBox ticketsTableContainer;

    public SupportO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public SupportO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public Scene getSupportOScene(Runnable backAction) {
        this.backAction = backAction;
        initSampleTickets();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Sidebar on Left
        root.setLeft(createCommonOrganizerSidebar("Support"));

        // 2. Main Content inside ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 28, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createHeroSearchSection(),
                createBrowseCategoriesAndArticlesRow(),
                createBlueHelpBanner(),
                createAiAssistantAndEmergencyRow(),
                createTicketAndFaqRow()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        supportScene = new Scene(root, 1550, 785);
        return supportScene;
    }

    private void initSampleTickets() {
        if (myTickets.isEmpty()) {
            myTickets.add(new TicketData("EX-1024", "Schedule Conflict Resolution", "Schedule", "High", "In Progress"));
            myTickets.add(new TicketData("EX-1018", "Vendor Quote Confirmation", "Vendors", "Medium", "Resolved"));
            myTickets.add(new TicketData("EX-1012", "QR Scanner Device Sync", "QR Check-In", "High", "Resolved"));
        }
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
                createSidebarButton("Notifications", "fas-bell", "Notifications".equals(activeTab), () -> handleNav("Notifications")),
                createSidebarButton("Profile", "fas-user", "Profile".equals(activeTab), () -> handleNav("Profile")),
                createSidebarButton("Settings", "fas-cog", "Settings".equals(activeTab), () -> handleNav("Settings")),
                createSidebarButton("Support", "fas-question-circle", "Support".equals(activeTab), () -> {})
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
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:rgba(255,255,255,0.08);-fx-text-fill:white;-fx-font-size:12px;-fx-background-radius:6;-fx-cursor:hand;"));
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
            case "Venue" -> {
                VenueManagementO venue = new VenueManagementO();
                LandingPage.landingPageStage.setScene(venue.getScene(LandingPage.landingPageStage, null));
            }
            case "Vendors" -> {
                VendorManagementO vendor = new VendorManagementO(uid, idToken);
                LandingPage.landingPageStage.setScene(vendor.getvendorManagementOScene(null));
            }
            case "Schedule" -> {
                ScheduleO sched = new ScheduleO();
                LandingPage.landingPageStage.setScene(sched.getScene(LandingPage.landingPageStage, null));
            }
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
            case "Support" -> {}
        }
    }

    private VBox createHeroSearchSection() {
        VBox heroCard = createCardContainer();
        heroCard.setAlignment(Pos.CENTER);
        heroCard.setPadding(new Insets(24, 20, 24, 20));

        Label systemBadge = new Label("🟢 All systems operational");
        systemBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12px;");

        Label title = new Label("How can we help you?");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Find answers, explore guides, or contact our support team.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setMaxWidth(620);
        searchBox.setPadding(new Insets(10, 0, 0, 0));

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search for articles, guides, and FAQs...");
        searchInput.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 8 16; -fx-font-size: 13px;");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        Button searchBtn = createPrimaryButton("Search");
        searchBtn.setOnAction(e -> {
            if (!searchInput.getText().trim().isEmpty()) {
                openArticleModal("Search Results", "Showing articles matching: '" + searchInput.getText().trim() + "'");
            }
        });

        searchBox.getChildren().addAll(searchInput, searchBtn);
        heroCard.getChildren().addAll(systemBadge, title, subtitle, searchBox);
        return heroCard;
    }

    private HBox createBrowseCategoriesAndArticlesRow() {
        HBox row = new HBox(20);

        VBox catSection = new VBox(12);
        HBox.setHgrow(catSection, Priority.ALWAYS);

        Label catTitle = new Label("Browse Help Topics");
        catTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        grid.add(createCategoryCard("📋 Event Planning", "Managing live schedules, tasks, and client requirements."), 0, 0);
        grid.add(createCategoryCard("🤖 Timefold AI Planner", "Optimizing schedules, time-slots, and vendor recommendations."), 1, 0);
        grid.add(createCategoryCard("💼 Budget & Expenses", "Allocating vendor budgets and managing payment records."), 0, 1);
        grid.add(createCategoryCard("🏢 Venue & Vendors", "Handling venue reservations and vendor confirmation status."), 1, 1);

        catSection.getChildren().addAll(catTitle, grid);

        VBox articlesCard = createCardContainer();
        articlesCard.setPrefWidth(360);

        Label artTitle = new Label("📈 Popular Articles");
        artTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox articleList = new VBox(10);
        articleList.getChildren().addAll(
                createArticleLink("📄 Accepting & Planning Event Requests"),
                createArticleLink("📄 Managing Timefold AI Auto-Generated Timelines"),
                createArticleLink("📄 Real-time Budget Synchronization")
        );

        articlesCard.getChildren().addAll(artTitle, new Separator(), articleList);
        row.getChildren().addAll(catSection, articlesCard);
        return row;
    }

    private VBox createCategoryCard(String title, String desc) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(t, d);
        card.setOnMouseClicked(e -> openArticleModal(title, desc));
        return card;
    }

    private HBox createArticleLink(String title) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-cursor: hand;");

        Label text = new Label(title);
        text.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label arrow = new Label("›");
        arrow.setStyle("-fx-font-size: 14px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        row.getChildren().addAll(text, sp, arrow);
        row.setOnMouseClicked(e -> openArticleModal(title, "Step-by-step instructions for: " + title));
        return row;
    }

    private HBox createBlueHelpBanner() {
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(20));
        banner.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 10px;");

        VBox textGroup = new VBox(3);
        Label title = new Label("Need direct assistance?");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Our enterprise event operations team is on standby to assist organizers.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #DCE5F0;");
        textGroup.getChildren().addAll(title, subtitle);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button emailBtn = new Button("✉ Submit Ticket");
        emailBtn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        emailBtn.setOnAction(e -> openReportTicketModal());

        banner.getChildren().addAll(textGroup, sp, emailBtn);
        return banner;
    }

    private HBox createAiAssistantAndEmergencyRow() {
        HBox row = new HBox(20);

        VBox aiCard = createCardContainer();
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        aiCard.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");

        Label aiTitle = new Label("✨ EventXpert AI Help Assistant");
        aiTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox inputGroup = new HBox(8);
        TextField aiInput = new TextField();
        aiInput.setPromptText("e.g. conflict detection, budget allocation, vendor quote...");
        aiInput.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 8 12;");
        HBox.setHgrow(aiInput, Priority.ALWAYS);

        Button askBtn = createPrimaryButton("Ask AI");
        inputGroup.getChildren().addAll(aiInput, askBtn);

        Label aiOutput = new Label("💡 AI Response will appear here.");
        aiOutput.setWrapText(true);
        aiOutput.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        askBtn.setOnAction(e -> {
            String query = aiInput.getText().toLowerCase();
            if (query.contains("conflict")) {
                aiOutput.setText("💡 AI Guidance: Use Schedule -> Conflict Detection to review auto-resolved slots.");
            } else if (query.contains("budget")) {
                aiOutput.setText("💡 AI Guidance: Budget Breakdown can be finalized in the Event Planning module.");
            } else {
                aiOutput.setText("💡 AI Guidance: All event schedules are continuously optimized with Timefold.");
            }
        });

        aiCard.getChildren().addAll(aiTitle, inputGroup, aiOutput);

        VBox emergencyCard = createCardContainer();
        emergencyCard.setPrefWidth(360);

        Label emTitle = new Label("🚨 Event-Day Emergency Hotline");
        emTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Label phoneLbl = new Label("📞 Hotline: +91 1800 123 456");
        phoneLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        emergencyCard.getChildren().addAll(emTitle, phoneLbl);
        row.getChildren().addAll(aiCard, emergencyCard);
        return row;
    }

    private HBox createTicketAndFaqRow() {
        HBox row = new HBox(20);

        VBox ticketsCard = createCardContainer();
        HBox.setHgrow(ticketsCard, Priority.ALWAYS);

        HBox tHeader = new HBox();
        Label tTitle = new Label("My Support Requests");
        tTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button newTicketBtn = createPrimaryButton("+ Report Issue");
        newTicketBtn.setOnAction(e -> openReportTicketModal());

        tHeader.getChildren().addAll(tTitle, sp, newTicketBtn);

        ticketsTableContainer = new VBox(4);
        renderTicketsTable();

        ticketsCard.getChildren().addAll(tHeader, new Separator(), ticketsTableContainer);

        VBox faqCard = createCardContainer();
        faqCard.setPrefWidth(360);

        Label faqTitle = new Label("Frequently Asked Questions");
        faqTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox faqList = new VBox(8);
        faqList.getChildren().addAll(
                createFaqItem("How do I confirm an assigned request?", "Open Event Requests, review the duration and AI plan, and click 'Accept Request'."),
                createFaqItem("How does the real-time budget sync work?", "Any edits in Budget Breakdown are instantly saved to Firestore and visible to the customer.")
        );

        faqCard.getChildren().addAll(faqTitle, new Separator(), faqList);
        row.getChildren().addAll(ticketsCard, faqCard);
        return row;
    }

    private void renderTicketsTable() {
        ticketsTableContainer.getChildren().clear();

        for (TicketData t : myTickets) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 6, 8, 6));
            row.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

            Label idLbl = new Label(t.id);
            idLbl.setPrefWidth(70);
            idLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + PRIMARY_BLUE + ";");

            Label issueLbl = new Label(t.issue);
            HBox.setHgrow(issueLbl, Priority.ALWAYS);
            issueLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");

            Label catLbl = new Label(t.category);
            catLbl.setPrefWidth(80);
            catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

            Label statusBadge = new Label(t.status);
            statusBadge.setStyle(t.status.equals("Resolved")
                    ? "-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-padding: 2 6; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;"
                    : "-fx-background-color: " + WARNING_BG + "; -fx-text-fill: " + WARNING_TEXT + "; -fx-padding: 2 6; -fx-background-radius: 4px; -fx-font-size: 10px; -fx-font-weight: bold;"
            );

            row.getChildren().addAll(idLbl, issueLbl, catLbl, statusBadge);
            ticketsTableContainer.getChildren().add(row);
        }
    }

    private VBox createFaqItem(String question, String answer) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-background-radius: 6px;");

        Label q = new Label("❓ " + question);
        q.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + NAVY + ";");

        Label a = new Label(answer);
        a.setWrapText(true);
        a.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(q, a);
        return box;
    }

    // ================= MODAL DIALOGS (NO EXTRA STAGE) =================
    private void openArticleModal(String titleStr, String contentStr) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Knowledge Base");
        dialog.setHeaderText(titleStr);
        if (LandingPage.landingPageStage != null) {
            dialog.initOwner(LandingPage.landingPageStage);
        }

        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeBtn);

        Label content = new Label(contentStr);
        content.setWrapText(true);
        content.setPrefWidth(360);
        content.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");

        dialog.getDialogPane().setContent(new VBox(10, content));
        dialog.showAndWait();
    }

    private void openReportTicketModal() {
        Dialog<TicketData> dialog = new Dialog<>();
        dialog.setTitle("Report an Issue");
        dialog.setHeaderText("Submit Support Ticket");
        if (LandingPage.landingPageStage != null) {
            dialog.initOwner(LandingPage.landingPageStage);
        }

        ButtonType submitBtnType = new ButtonType("Submit Ticket", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtnType, ButtonType.CANCEL);

        VBox layout = new VBox(8);
        layout.setPadding(new Insets(10));
        layout.setPrefWidth(360);

        TextField subjectInput = new TextField();
        subjectInput.setPromptText("Issue Summary");

        ComboBox<String> catCB = new ComboBox<>();
        catCB.getItems().addAll("Schedule", "Vendors", "Budget", "QR Check-In", "Technical Issue");
        catCB.setValue("Technical Issue");

        TextArea descInput = new TextArea();
        descInput.setPromptText("Describe your problem in detail...");
        descInput.setPrefRowCount(3);

        layout.getChildren().addAll(
                new Label("Subject:"), subjectInput,
                new Label("Category:"), catCB,
                new Label("Details:"), descInput
        );
        dialog.getDialogPane().setContent(layout);

        dialog.setResultConverter(btn -> {
            if (btn == submitBtnType && !subjectInput.getText().trim().isEmpty()) {
                String newId = "EX-" + (1025 + myTickets.size());
                return new TicketData(newId, subjectInput.getText().trim(), catCB.getValue(), "Medium", "In Progress");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ticket -> {
            myTickets.add(0, ticket);
            renderTicketsTable();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Ticket " + ticket.id + " submitted successfully!");
            if (LandingPage.landingPageStage != null) {
                a.initOwner(LandingPage.landingPageStage);
            }
            a.showAndWait();
        });
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 14; -fx-cursor: hand;");
        return btn;
    }
}