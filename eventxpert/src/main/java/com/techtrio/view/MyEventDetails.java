package com.techtrio.view;

import java.util.Map;

import com.techtrio.controller.CustomerEventController;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MyEventDetails {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backCallback;

    // Dynamic Organizer UI Components
    private Label organizerNameLabel;
    private Label organizerRoleLabel;
    private Label organizerAvatarInitial;
    private String currentOrgEmail = "support@eventxpert.com";
    private String currentOrgPhone = "+91 98765 43210";

    // Theme Colors
    private static final String SIDEBAR_NAVY    = "#03172C";
    private static final String ACTIVE_NAV_BG   = "#0A2544";
    private static final String ACTIVE_NAV_BRD  = "#38BDF8";
    private static final String NEW_PROJECT_BTN = "#D0E5FF";
    private static final String PAGE_BG         = "#F3F6FA";
    private static final String CARD_BG         = "#FFFFFF";
    private static final String CARD_BORDER     = "#E2E8F0";
    private static final String TEXT_DARK       = "#0B192C";
    private static final String TEXT_MUTED      = "#64748B";
    private static final String TEXT_LIGHT      = "#94A3B8";
    private static final String ACCENT_BLUE     = "#0284C7";
    private static final String PLANNING_BG     = "#E0F2FE";
    private static final String AI_BG           = "#EDF5FF";
    private static final String AI_BORDER       = "#CCE3FC";
    private static final String CONTACT_BTN     = "#08203E";
    private static final String ALERT_BG        = "#FFF5F5";
    private static final String ALERT_BORDER    = "#FEE2E2";

    public MyEventDetails() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public MyEventDetails(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getMyEventDetailsScene() {
        return getMyEventDetailsScene(null);
    }

    public Scene getMyEventDetailsScene(Runnable backCallback) {
        this.backCallback = backCallback;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + PAGE_BG + "; -fx-font-family: 'Segoe UI', sans-serif;");

        // 1. Sidebar
        root.setLeft(createSidebar());

        // 2. Main Content
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: " + PAGE_BG + ";");

        mainContainer.getChildren().add(createTopHeader());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + PAGE_BG + "; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox contentBody = createContentBody();
        scrollPane.setContent(contentBody);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().add(scrollPane);
        root.setCenter(mainContainer);

        // 🎯 डेटाबेसमधून Organizer ची माहिती लोड करणे
        loadDynamicOrganizerFromDatabase();

        return new Scene(root, 1550, 750);
    }

    // ================= 1. SIDEBAR =================
    private VBox createSidebar() {
        VBox sidebar = new VBox(22);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_NAVY + ";");

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label logoIcon = new Label("EX");
        logoIcon.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 6 9; -fx-background-radius: 8;");

        VBox logoText = new VBox(0);
        Label brand = new Label("EventXpert");
        brand.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px;");
        Label subBrand = new Label("PREMIUM MANAGEMENT");
        subBrand.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 8px; -fx-font-weight: bold;");
        logoText.getChildren().addAll(brand, subBrand);
        logoBox.getChildren().addAll(logoIcon, logoText);

        VBox navList = new VBox(6);
        Button dashBtn = createNavButton("📊   Dashboard", false);
        dashBtn.setOnAction(e -> navigateBack());

        Button myEventsBtn = createNavButton("📅   My Events", true); // Active
        myEventsBtn.setOnAction(e -> navigateBack());

        Button createEventBtn = createNavButton("➕   Create Event", false);
        Button aiPlannerBtn = createNavButton("✨   AI Event Planner", false);

        navList.getChildren().addAll(dashBtn, myEventsBtn, createEventBtn, aiPlannerBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomSection = new VBox(8);
        bottomSection.getChildren().addAll(
                createNavButton("⚙   Settings", false),
                createNavButton("❓   Support", false)
        );

        Button newProjectBtn = new Button("New Project");
        newProjectBtn.setMaxWidth(Double.MAX_VALUE);
        newProjectBtn.setStyle("-fx-background-color: " + NEW_PROJECT_BTN + "; -fx-text-fill: " + SIDEBAR_NAVY + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 12; -fx-padding: 9; -fx-cursor: hand;");

        bottomSection.getChildren().add(newProjectBtn);
        sidebar.getChildren().addAll(logoBox, navList, spacer, bottomSection);
        return sidebar;
    }

    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(9, 14, 9, 14));

        if (isActive) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BG + "; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-border-color: " + ACTIVE_NAV_BRD + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-cursor: hand;");
        }
        return btn;
    }

    // ================= 2. TOP HEADER =================
    private HBox createTopHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 32, 12, 32));
        header.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + CARD_BORDER + "; -fx-border-width: 0 0 1 0;");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search events, organizers...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 12; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label notifBtn = new Label("🔔");
        notifBtn.setStyle("-fx-font-size: 14px; -fx-cursor: hand; -fx-text-fill: " + TEXT_MUTED + ";");

        Label mailBtn = new Label("✉");
        mailBtn.setStyle("-fx-font-size: 14px; -fx-cursor: hand; -fx-text-fill: " + TEXT_MUTED + ";");

        HBox userProfile = new HBox(8);
        userProfile.setAlignment(Pos.CENTER);
        Circle avatar = new Circle(14, Color.web("#CBD5E1"));
        Label userName = new Label("Sarah J. ▾");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        userProfile.getChildren().addAll(avatar, userName);

        header.getChildren().addAll(searchField, spacer, notifBtn, mailBtn, userProfile);
        return header;
    }

    // ================= 3. CONTENT BODY =================
    private VBox createContentBody() {
        VBox body = new VBox(22);
        body.setPadding(new Insets(24, 32, 32, 32));

        String eventName = (event != null && event.getEventName() != null && !event.getEventName().isBlank()) ? event.getEventName() : "College Seminar";
        String eventType = (event != null && event.getVenueType() != null && !event.getVenueType().isBlank()) ? event.getVenueType() : "Academic Event";
        String status = (event != null && event.getStatus() != null && !event.getStatus().isBlank()) ? event.getStatus().toUpperCase() : "PLANNING";

        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleLeft = new VBox(3);
        Label breadcrumb = new Label("My Events  >  " + eventName);
        breadcrumb.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_LIGHT + ";");

        Label title = new Label(eventName);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Label subtitle = new Label("🎓 " + eventType + " • Organized by Certified Event Team");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        titleLeft.getChildren().addAll(breadcrumb, title, subtitle);

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);

        // Status Badge Styling
        String badgeBg = PLANNING_BG;
        String badgeTxt = ACCENT_BLUE;
        if ("COMPLETED".equals(status) || "PAID".equals(status) || "CONFIRMED".equals(status)) {
            badgeBg = "#DCFCE7"; badgeTxt = "#16A34A";
        } else if ("UPCOMING".equals(status)) {
            badgeBg = "#EEF2FF"; badgeTxt = "#4F46E5";
        } else if ("APPROVED".equals(status)) {
            badgeBg = "#DBEAFE"; badgeTxt = "#1E40AF";
        } else if ("REJECTED".equals(status)) {
            badgeBg = "#FEE2E2"; badgeTxt = "#991B1B";
        } else if ("PENDING".equals(status)) {
            badgeBg = "#FEF3C7"; badgeTxt = "#D97706";
        }

        Label planningBadge = new Label("●  " + status);
        planningBadge.setStyle("-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeTxt + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 20;");

        Button backBtn = new Button("< Back to My Events");
        backBtn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + "; -fx-padding: 6 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        HBox titleRight = new HBox(12);
        titleRight.setAlignment(Pos.CENTER);
        titleRight.getChildren().add(planningBadge);

        // 💳 PAY NOW BUTTON - ONLY FOR APPROVED EVENTS
        if ("APPROVED".equalsIgnoreCase(status)) {
            Button payNowBtn = new Button("💳 Pay Now");
            payNowBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand;");
            
            payNowBtn.setOnAction(e -> {
                Stage stage = (Stage) payNowBtn.getScene().getWindow();
                PaymentView paymentView = new PaymentView();
                
                // Open Payment Window
                paymentView.openPaymentWindow(stage, event, 2500.0, () -> {
                    // Success Callback: Status PAID करा आणि Pay बटण काढा
                    if (event != null) event.setStatus("PAID");
                    planningBadge.setText("●  PAID & CONFIRMED");
                    planningBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 20;");
                    titleRight.getChildren().remove(payNowBtn);
                });
            });
            titleRight.getChildren().add(payNowBtn);
        }

        titleRight.getChildren().add(backBtn);
        titleBar.getChildren().addAll(titleLeft, titleSpacer, titleRight);
        body.getChildren().add(titleBar);

        // ROW 1: Progress & Budget Overview
        HBox row1 = new HBox(20);
        VBox planningCard = createPlanningCard(status);
        VBox budgetCard = createBudgetCard();
        HBox.setHgrow(planningCard, Priority.ALWAYS);
        budgetCard.setPrefWidth(320);
        row1.getChildren().addAll(planningCard, budgetCard);
        body.getChildren().add(row1);

        // ROW 2: Event Overview, Requirements, Location
        HBox row2 = new HBox(20);
        VBox overviewCard = createOverviewCard(eventName, eventType);
        VBox requirementsCard = createRequirementsCard();
        VBox locationCard = createLocationCard();

        HBox.setHgrow(overviewCard, Priority.ALWAYS);
        HBox.setHgrow(requirementsCard, Priority.ALWAYS);
        HBox.setHgrow(locationCard, Priority.ALWAYS);

        row2.getChildren().addAll(overviewCard, requirementsCard, locationCard);
        body.getChildren().add(row2);

        // ROW 3: AI Planner Box & DYNAMIC DATABASE ORGANIZER SECTION
        HBox row3 = new HBox(20);
        VBox aiCard = createAICard();
        VBox rightSideCards = createOrganizerSection();

        HBox.setHgrow(aiCard, Priority.ALWAYS);
        rightSideCards.setPrefWidth(320);

        row3.getChildren().addAll(aiCard, rightSideCards);
        body.getChildren().add(row3);

        return body;
    }

    // ================= 4. DYNAMIC DATABASE ORGANIZER FETCHER =================
    private void loadDynamicOrganizerFromDatabase() {
        Task<Map<String, String>> task = new Task<>() {
            @Override
            protected Map<String, String> call() {
                CustomerEventController controller = new CustomerEventController();
                String eventId = (event != null) ? event.getEventId() : null;
                return controller.getOrganizerDetailsForEvent(eventId);
            }
        };

        task.setOnSucceeded(e -> {
            Map<String, String> data = task.getValue();
            if (data != null) {
                Platform.runLater(() -> {
                    String name = data.getOrDefault("name", "Organizer Assigned");
                    String role = data.getOrDefault("role", "Event Coordinator");
                    currentOrgEmail = data.getOrDefault("email", "support@eventxpert.com");
                    currentOrgPhone = data.getOrDefault("phone", "+91 98765 43210");

                    if (organizerNameLabel != null) organizerNameLabel.setText(name);
                    if (organizerRoleLabel != null) organizerRoleLabel.setText(role);
                    if (organizerAvatarInitial != null && !name.isBlank()) {
                        organizerAvatarInitial.setText(name.substring(0, 1).toUpperCase());
                    }
                });
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // 5. Dynamic Organizer Card
    private VBox createOrganizerSection() {
        VBox container = new VBox(12);

        VBox orgCard = new VBox(8);
        orgCard.setAlignment(Pos.CENTER);
        orgCard.setPadding(new Insets(14));
        orgCard.setStyle(getCardStyle());

        Label orgTitle = new Label("Your Organizer");
        orgTitle.setMaxWidth(Double.MAX_VALUE);
        orgTitle.setAlignment(Pos.CENTER_LEFT);
        orgTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        StackPane avatarPane = new StackPane();
        Circle avatarCircle = new Circle(22, Color.web("#DBEAFE"));
        organizerAvatarInitial = new Label("O");
        organizerAvatarInitial.setStyle("-fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-font-size: 15px;");
        avatarPane.getChildren().addAll(avatarCircle, organizerAvatarInitial);

        organizerNameLabel = new Label("Loading organizer...");
        organizerNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";");

        organizerRoleLabel = new Label("Connecting to database...");
        organizerRoleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_LIGHT + ";");

        Button contactBtn = new Button("✉   Contact Organizer");
        contactBtn.setMaxWidth(Double.MAX_VALUE);
        contactBtn.setStyle("-fx-background-color: " + CONTACT_BTN + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 7; -fx-cursor: hand;");
        contactBtn.setOnAction(e -> {
            String name = organizerNameLabel.getText();
            showContactDialog(name, currentOrgEmail, currentOrgPhone);
        });

        orgCard.getChildren().addAll(orgTitle, avatarPane, organizerNameLabel, organizerRoleLabel, contactBtn);

        // Change Alert Card
        VBox changeCard = new VBox(6);
        changeCard.setPadding(new Insets(12));
        changeCard.setStyle("-fx-background-color: " + ALERT_BG + "; -fx-border-color: " + ALERT_BORDER + "; -fx-background-radius: 16; -fx-border-radius: 16;");

        Label changeTitle = new Label("Need to make a change?");
        changeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #991B1B;");

        Label changeText = new Label("Your event is currently being planned. Modifications are locked, but you can submit a change request.");
        changeText.setWrapText(true);
        changeText.setStyle("-fx-font-size: 10px; -fx-text-fill: #7F1D1D;");

        Button reqBtn = new Button("🛠   Request a Change");
        reqBtn.setMaxWidth(Double.MAX_VALUE);
        reqBtn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + "; -fx-padding: 6; -fx-cursor: hand;");
        reqBtn.setOnAction(e -> showChangeRequestDialog());

        changeCard.getChildren().addAll(changeTitle, changeText, reqBtn);
        container.getChildren().addAll(orgCard, changeCard);
        return container;
    }

    // --- CARDS COMPONENTS ---

    private VBox createPlanningCard(String status) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(getCardStyle());

        double progressVal = 0.65;
        String percentStr = "65%";
        int activeStep = 5;

        if ("COMPLETED".equals(status) || "PAID".equals(status) || "CONFIRMED".equals(status)) {
            progressVal = 1.0; percentStr = "100%"; activeStep = 6;
        } else if ("UPCOMING".equals(status)) {
            progressVal = 0.85; percentStr = "85%"; activeStep = 5;
        } else if ("PENDING".equals(status)) {
            progressVal = 0.30; percentStr = "30%"; activeStep = 2;
        }

        HBox header = new HBox();
        Label title = new Label("📈 Planning Progress");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label percent = new Label(percentStr);
        percent.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px;");
        header.getChildren().addAll(title, sp, percent);

        ProgressBar pb = new ProgressBar(progressVal);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + SIDEBAR_NAVY + ";");

        HBox stepper = new HBox(24);
        stepper.setAlignment(Pos.CENTER);
        stepper.getChildren().addAll(
                createStep("Details", activeStep >= 1 ? 1 : 3),
                createStep("Requirements", activeStep >= 2 ? (activeStep == 2 ? 2 : 1) : 3),
                createStep("Location", activeStep >= 3 ? (activeStep == 3 ? 2 : 1) : 3),
                createStep("Budget", activeStep >= 4 ? (activeStep == 4 ? 2 : 1) : 3),
                createStep("AI Planning", activeStep >= 5 ? (activeStep == 5 ? 2 : 1) : 3),
                createStep("Review", activeStep == 6 ? 1 : 3)
        );

        HBox infoBox = new HBox(10);
        infoBox.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 12; -fx-background-radius: 10; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 10;");
        Label infoText = new Label("ℹ  Your event is currently being planned by the organizer. You will be notified once the AI planning stage is complete for your final review.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        infoBox.getChildren().add(infoText);

        card.getChildren().addAll(header, pb, stepper, infoBox);
        return card;
    }

    private VBox createStep(String name, int type) {
        VBox step = new VBox(4);
        step.setAlignment(Pos.CENTER);
        Label circle = new Label();
        circle.setAlignment(Pos.CENTER);
        circle.setPrefSize(22, 22);

        if (type == 1) {
            circle.setText("✓");
            circle.setStyle("-fx-background-color: " + SIDEBAR_NAVY + "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 11;");
        } else if (type == 2) {
            circle.setText("●");
            circle.setStyle("-fx-background-color: " + ACCENT_BLUE + "; -fx-text-fill: white; -fx-font-size: 8px; -fx-background-radius: 11;");
        } else {
            circle.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 11;");
        }

        Label label = new Label(name);
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: " + (type == 2 ? "bold" : "normal") + "; -fx-text-fill: " + (type == 2 ? ACCENT_BLUE : TEXT_MUTED) + ";");
        step.getChildren().addAll(circle, label);
        return step;
    }

    private VBox createBudgetCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(getCardStyle());

        Label title = new Label("Budget Overview");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";");

        Label totalLabel = new Label("Total Budget");
        totalLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_LIGHT + "; -fx-font-weight: bold;");

        String budgetStr = "₹50,000";
        if (event != null && event.getBudget() != null && !event.getBudget().isBlank()) {
            budgetStr = event.getBudget().startsWith("₹") ? event.getBudget() : "₹" + event.getBudget();
        }

        Label amount = new Label(budgetStr);
        amount.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        VBox stats = new VBox(6);
        stats.setStyle("-fx-border-color: #F1F5F9 transparent transparent transparent; -fx-padding: 8 0 0 0;");
        stats.getChildren().addAll(
                createKeyVal("Allocated", "₹42,000"),
                createKeyVal("Remaining", "₹8,000")
        );

        Button viewBtn = new Button("View Budget Details →");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        viewBtn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + "; -fx-padding: 7; -fx-cursor: hand;");

        card.getChildren().addAll(title, totalLabel, amount, stats, viewBtn);
        return card;
    }

    private VBox createOverviewCard(String eventName, String eventType) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(getCardStyle());

        Label title = new Label("Event Overview");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + "; -fx-border-color: #F1F5F9 transparent #F1F5F9 transparent; -fx-padding: 0 0 6 0;");

        String dateStr = (event != null && event.getEventDate() != null && !event.getEventDate().isBlank()) ? event.getEventDate() : "15 May 2024";

        card.getChildren().addAll(
                title,
                createKeyVal("Name", eventName),
                createKeyVal("Type", eventType),
                createKeyVal("Date", "📅 " + dateStr),
                createKeyVal("Time", "🕒 10:00 AM - 04:00 PM"),
                createKeyVal("Guests", "👥 100 Expected")
        );
        return card;
    }

    private VBox createRequirementsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(getCardStyle());

        Label title = new Label("Requirements & Services");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";");

        FlowPane chips = new FlowPane(6, 6);
        chips.getChildren().addAll(
                createServiceChip("🏢 Venue"),
                createServiceChip("🔊 Sound System"),
                createServiceChip("🍽 Catering"),
                createServiceChip("📷 Photography")
        );

        Label notesTitle = new Label("Additional Notes");
        notesTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_LIGHT + "; -fx-font-weight: bold;");

        Label notesBox = new Label("Require vegetarian options for catering. Ensure sound system has wireless lapel mics for main speakers. Projector needed in main hall.");
        notesBox.setWrapText(true);
        notesBox.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8; -fx-background-radius: 8; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 8; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        card.getChildren().addAll(title, chips, notesTitle, notesBox);
        return card;
    }

    private VBox createLocationCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(getCardStyle());

        Label title = new Label("Location");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";");

        String cityStr = (event != null && event.getCity() != null && !event.getCity().isBlank()) ? event.getCity() + ", Maharashtra" : "Pune, Maharashtra";

        Label city = new Label(cityStr);
        city.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");

        Label status = new Label("📍 Venue To Be Finalized");
        status.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_LIGHT + ";");

        StackPane mapArea = new StackPane();
        mapArea.setPrefHeight(60);
        mapArea.setStyle("-fx-background-color: #F1F5F9; -fx-border-color: " + CARD_BORDER + "; -fx-background-radius: 8; -fx-border-radius: 8;");
        Label mapLabel = new Label("🗺 Map Preview Area");
        mapLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_LIGHT + ";");
        mapArea.getChildren().add(mapLabel);

        Button viewVenueBtn = new Button("View Venue Options →");
        viewVenueBtn.setMaxWidth(Double.MAX_VALUE);
        viewVenueBtn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + "; -fx-padding: 6; -fx-cursor: hand;");

        card.getChildren().addAll(title, city, status, mapArea, viewVenueBtn);
        return card;
    }

    private VBox createAICard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + AI_BG + "; -fx-border-color: " + AI_BORDER + "; -fx-border-radius: 16; -fx-background-radius: 16;");

        HBox top = new HBox(12);
        Label brainIcon = new Label("🧠");
        brainIcon.setStyle("-fx-background-color: " + SIDEBAR_NAVY + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10; -fx-background-radius: 10;");

        VBox aiText = new VBox(2);
        Label title = new Label("AI Event Planner Active");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK + ";");
        Label desc = new Label("Your event is being optimized with AI assisted planning to ensure maximum efficiency and best vendor matching based on your requirements.");
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        aiText.getChildren().addAll(title, desc);

        top.getChildren().addAll(brainIcon, aiText);

        HBox bottom = new HBox(14);
        bottom.setStyle("-fx-border-color: " + AI_BORDER + " transparent transparent transparent; -fx-padding: 10 0 0 0;");
        Label c1 = new Label("✔ Smart Checklist Generation");
        Label c2 = new Label("✔ Schedule Suggestions");
        Label c3 = new Label("✔ Vendor Recommendations");
        c1.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1E40AF;");
        c2.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1E40AF;");
        c3.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1E40AF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewDetails = new Label("View AI Planning Details →");
        viewDetails.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_BLUE + "; -fx-cursor: hand;");

        bottom.getChildren().addAll(c1, c2, c3, sp, viewDetails);
        card.getChildren().addAll(top, bottom);
        return card;
    }

    private void showContactDialog(String name, String email, String phone) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Organizer");
        alert.setHeaderText("Organizer: " + name);
        alert.setContentText("📧 Email: " + email + "\n📞 Phone: " + phone + "\n\nYou can reach out to your organizer for event coordination.");
        alert.showAndWait();
    }

    private void showChangeRequestDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Request a Change");
        dialog.setHeaderText("Submit Change Request to Organizer");
        dialog.setContentText("Describe the modifications needed:");
        dialog.showAndWait().ifPresent(msg -> {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Request Submitted");
            success.setHeaderText(null);
            success.setContentText("Your change request has been sent to the organizer!");
            success.showAndWait();
        });
    }

    private void navigateBack() {
        if (backCallback != null) {
            backCallback.run();
        } else {
            MyEvents myEventsPage = new MyEvents(uid, idToken);
            if (LandingPage.landingPageStage != null) {
                LandingPage.landingPageStage.setScene(myEventsPage.getMyEventsScene(null));
            }
        }
    }

    private String getCardStyle() {
        return "-fx-background-color: " + CARD_BG + "; -fx-background-radius: 16; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 16;";
    }

    private HBox createKeyVal(String key, String val) {
        HBox row = new HBox();
        Label k = new Label(key);
        k.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_LIGHT + ";");
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        row.getChildren().addAll(k, r, v);
        return row;
    }

    private Label createServiceChip(String text) {
        Label b = new Label(text);
        b.setStyle("-fx-background-color: #F1F5F9; -fx-border-color: " + CARD_BORDER + "; -fx-padding: 4 8; -fx-background-radius: 6; -fx-border-radius: 6; -fx-font-size: 10px; -fx-text-fill: " + TEXT_DARK + ";");
        return b;
    }
}