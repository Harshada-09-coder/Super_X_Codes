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

public class ProfileO extends Application {

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

    // Dynamic State & Controls
    private Label heroNameLabel;
    private Label heroRoleLabel;
    private Label heroOrgLabel;
    private Label emergencyContactLabel;
    
    private TextField nameInput;
    private TextField phoneInput;
    private TextField locationInput;
    
    private FlowPane skillsFlowPane;
    private final List<String> userSkills = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        initDefaultSkills();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // 1. Sidebar (Left)
        VBox sidebar = createSidebar(primaryStage);
        root.setLeft(sidebar);

        // 2. Top Bar (Top)
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // 3. Main Center Scrollable Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        // Assemble Profile Sections
        mainContent.getChildren().addAll(
                createPageHeader(primaryStage),
                createHeroProfileCard(primaryStage),
                createCompletionCard(primaryStage),
                createOverviewStatsRow(),
                createTwoColumnContentGrid(primaryStage),
                createProfileActionsRow(primaryStage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Scene Setup
        Scene scene = new Scene(root, 1550, 785);
        primaryStage.setTitle("EventXpert - Organizer Profile");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void initDefaultSkills() {
        userSkills.add("Event Planning");
        userSkills.add("Budget Management");
        userSkills.add("Vendor Management");
        userSkills.add("Venue Coordination");
        userSkills.add("Guest Management");
        userSkills.add("Corporate Events");
        userSkills.add("Event Operations");
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
        Label brandSub = new Label("Corporate Management");
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

        sidebar.getChildren().add(createSidebarItem("Notifications", false, e -> showToast(stage, "Opening Notifications...")));
        sidebar.getChildren().add(createSidebarItem("Settings", false, e -> showToast(stage, "Opening Settings...")));
        sidebar.getChildren().add(createSidebarItem("Help Center", false, e -> showToast(stage, "Opening Help Center...")));

        // Highlighted Profile Box (Active Menu)
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(10));
        profileBox.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 8px; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 8px;");

        Circle avatar = new Circle(16, Color.web(PRIMARY_BLUE));
        Text avatarText = new Text("AU");
        avatarText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Admin User");
        userName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        Label userEmail = new Label("admin@eventxpert.com");
        userEmail.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        userDetails.getChildren().addAll(userName, userEmail);

        profileBox.getChildren().addAll(avatarPane, userDetails);

        sidebar.getChildren().addAll(spacer, profileBox);
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
        searchField.setPromptText("🔍 Search organizer profile, events...");
        searchField.setPrefWidth(380);
        searchField.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 6 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> showToast(stage, "No new notifications"));

        Button helpBtn = new Button("❓");
        helpBtn.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BORDER_COLOR + "; -fx-background-radius: 50%; -fx-border-radius: 50%; -fx-cursor: hand;");

        Circle topAvatar = new Circle(14, Color.web(PRIMARY_BLUE));
        Text topAvatarText = new Text("AU");
        topAvatarText.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane profilePill = new StackPane(topAvatar, topAvatarText);

        topBar.getChildren().addAll(searchField, spacer, notifBtn, helpBtn, profilePill);
        return topBar;
    }

    // ==========================================
    // 3. PAGE HEADER
    // ==========================================
    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        Label breadcrumb = new Label("Home  >  My Profile");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editProfileBtn = createPrimaryButton("Edit Profile");
        editProfileBtn.setOnAction(e -> showToast(stage, "Editing enabled in form sections below."));

        titleRow.getChildren().addAll(title, spacer, editProfileBtn);

        Label subtitle = new Label("Manage your personal information, organizer details and account preferences.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        headerBox.getChildren().addAll(breadcrumb, titleRow, subtitle);
        return headerBox;
    }

    // ==========================================
    // 4. PROFILE HERO CARD
    // ==========================================
    private VBox createHeroProfileCard(Stage stage) {
        VBox card = createCardContainer();

        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        // Left Avatar Column
        VBox avatarBox = new VBox(8);
        avatarBox.setAlignment(Pos.CENTER);

        Circle bigAvatar = new Circle(40, Color.web(PRIMARY_BLUE));
        Text bigText = new Text("AU");
        bigText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 22px;");
        StackPane avatarPane = new StackPane(bigAvatar, bigText);

        Button changePhotoBtn = createSecondaryButton("Change Photo");
        changePhotoBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px;");
        changePhotoBtn.setOnAction(e -> showToast(stage, "Profile photo upload will be available in the production version."));

        avatarBox.getChildren().addAll(avatarPane, changePhotoBtn);

        // Right Info Details Column
        VBox infoBox = new VBox(6);
        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        heroNameLabel = new Label("Admin User");
        heroNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label statusBadge = new Label("● Active");
        statusBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 10px;");

        nameRow.getChildren().addAll(heroNameLabel, statusBadge);

        heroRoleLabel = new Label("Event Organizer  •  EventXpert Events");
        heroRoleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        heroOrgLabel = new Label("📍 Pune, Maharashtra  |  ✉ admin@eventxpert.com  |  📞 +91 98765 43210");
        heroOrgLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label memberSince = new Label("Organizer since May 2026");
        memberSince.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        infoBox.getChildren().addAll(nameRow, heroRoleLabel, heroOrgLabel, memberSince);

        heroRow.getChildren().addAll(avatarBox, new Separator(), infoBox);
        card.getChildren().add(heroRow);
        return card;
    }

    // ==========================================
    // 5. PROFILE COMPLETION CARD
    // ==========================================
    private VBox createCompletionCard(Stage stage) {
        VBox card = createCardContainer();

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Profile Completion");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label perc = new Label("85%");
        perc.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        topRow.getChildren().addAll(title, sp, perc);

        ProgressBar pb = new ProgressBar(0.85);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label msg = new Label("Complete your profile to get better event recommendations. Missing: Emergency Contact");
        msg.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Button completeBtn = createSecondaryButton("Complete Profile →");
        completeBtn.setOnAction(e -> openAddEmergencyContactModal(stage));

        bottomRow.getChildren().addAll(msg, sp2, completeBtn);

        card.getChildren().addAll(topRow, pb, bottomRow);
        return card;
    }

    // ==========================================
    // 6. ORGANIZER STATISTICS OVERVIEW
    // ==========================================
    private HBox createOverviewStatsRow() {
        HBox row = new HBox(16);

        row.getChildren().addAll(
                createStatCard("EVENTS CREATED", "24", PRIMARY_BLUE),
                createStatCard("EVENTS COMPLETED", "18", GREEN_TEXT),
                createStatCard("TOTAL GUESTS", "4,850", NAVY),
                createStatCard("SUCCESS RATE", "96%", BRIGHT_BLUE)
        );

        return row;
    }

    private VBox createStatCard(String label, String value, String colorHex) {
        VBox card = createCardContainer();
        HBox.setHgrow(card, Priority.ALWAYS);

        Label l = new Label(label);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label v = new Label(value);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        card.getChildren().addAll(l, v);
        return card;
    }

    // ==========================================
    // 7. TWO-COLUMN MAIN CONTENT GRID
    // ==========================================
    private HBox createTwoColumnContentGrid(Stage stage) {
        HBox grid = new HBox(20);

        // Left Column (Forms & Bio)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createPersonalInfoCard(stage),
                createOrganizerInfoCard(stage),
                createAboutMeCard(stage)
        );

        // Right Column (Skills, Security, Contact)
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(400);

        rightCol.getChildren().addAll(
                createSkillsCard(stage),
                createContactInfoCard(stage),
                createPreferencesCard(stage),
                createRecentActivityCard()
        );

        grid.getChildren().addAll(leftCol, rightCol);
        return grid;
    }

    private VBox createPersonalInfoCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Personal Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Your basic personal details.");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(12);

        nameInput = new TextField("Admin User");
        TextField emailInput = new TextField("admin@eventxpert.com"); emailInput.setDisable(true);
        phoneInput = new TextField("+91 98765 43210");
        TextField dobInput = new TextField("15 August 2004");

        ComboBox<String> genderCB = new ComboBox<>();
        genderCB.getItems().addAll("Prefer not to say", "Male", "Female", "Other");
        genderCB.setValue("Prefer not to say");
        genderCB.setMaxWidth(Double.MAX_VALUE);

        locationInput = new TextField("Pune, Maharashtra");

        form.add(createFormField("Full Name", nameInput), 0, 0);
        form.add(createFormField("Email Address 🔒", emailInput), 1, 0);
        form.add(createFormField("Phone Number", phoneInput), 0, 1);
        form.add(createFormField("Date of Birth", dobInput), 1, 1);
        form.add(createFormField("Gender", genderCB), 0, 2);
        form.add(createFormField("Location", locationInput), 1, 2);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = createSecondaryButton("Cancel");
        Button saveBtn = createPrimaryButton("Save Changes");

        saveBtn.setOnAction(e -> {
            heroNameLabel.setText(nameInput.getText());
            heroOrgLabel.setText("📍 " + locationInput.getText() + "  |  ✉ admin@eventxpert.com  |  📞 " + phoneInput.getText());
            showToast(stage, "✓ Personal information updated successfully!");
        });

        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(title, subtitle, new Separator(), form, new Separator(), btnRow);
        return card;
    }

    private VBox createOrganizerInfoCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Organizer Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(12);

        form.add(createFormField("Organization Name", new TextField("EventXpert Events")), 0, 0);
        form.add(createFormField("Role", new TextField("Event Organizer")), 1, 0);
        form.add(createFormField("Experience", new TextField("2 Years")), 0, 1);
        form.add(createFormField("Events Managed", new TextField("24")), 1, 1);
        form.add(createFormField("Specialization", new TextField("Corporate Events")), 0, 2);
        form.add(createFormField("Preferred Capacity", new TextField("100 – 500 Guests")), 1, 2);

        Button updateBtn = createPrimaryButton("Update Organizer Details");
        updateBtn.setOnAction(e -> showToast(stage, "✓ Organizer details updated!"));

        card.getChildren().addAll(title, new Separator(), form, new Separator(), updateBtn);
        return card;
    }

    private VBox createAboutMeCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("About Me");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label subtitle = new Label("Tell your clients and team a little about yourself.");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextArea bioArea = new TextArea("Passionate event organizer focused on creating professional, memorable and well-managed corporate events.");
        bioArea.setPrefRowCount(3);
        bioArea.setWrapText(true);
        bioArea.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-padding: 8;");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label charCount = new Label("112 / 500");
        charCount.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button saveBioBtn = createPrimaryButton("Save Bio");
        saveBioBtn.setOnAction(e -> showToast(stage, "✓ Bio updated successfully!"));

        bottomRow.getChildren().addAll(charCount, sp, saveBioBtn);

        card.getChildren().addAll(title, subtitle, bioArea, bottomRow);
        return card;
    }

    private VBox createSkillsCard(Stage stage) {
        VBox card = createCardContainer();

        HBox top = new HBox();
        Label title = new Label("Skills & Expertise");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addSkillBtn = createSecondaryButton("+ Add Skill");
        addSkillBtn.setOnAction(e -> openAddSkillModal(stage));

        top.getChildren().addAll(title, sp, addSkillBtn);

        skillsFlowPane = new FlowPane();
        skillsFlowPane.setHgap(8); skillsFlowPane.setVgap(8);

        renderSkills();

        card.getChildren().addAll(top, new Separator(), skillsFlowPane);
        return card;
    }

    private void renderSkills() {
        skillsFlowPane.getChildren().clear();
        for (String skill : userSkills) {
            Label tag = new Label(skill);
            tag.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 12px;");
            skillsFlowPane.getChildren().add(tag);
        }
    }

    private VBox createContactInfoCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("Contact Information");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox details = new VBox(8);
        details.getChildren().addAll(
                new Label("• Primary Email: admin@eventxpert.com"),
                new Label("• Phone Number: +91 98765 43210")
        );
        details.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";"));

        HBox emBox = new HBox();
        emergencyContactLabel = new Label("Emergency Contact: Not Added");
        emergencyContactLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addEmBtn = createSecondaryButton("+ Add");
        addEmBtn.setOnAction(e -> openAddEmergencyContactModal(stage));

        emBox.getChildren().addAll(emergencyContactLabel, sp, addEmBtn);

        card.getChildren().addAll(title, new Separator(), details, new Separator(), emBox);
        return card;
    }

    private VBox createPreferencesCard(Stage stage) {
        VBox card = createCardContainer();

        Label title = new Label("My Preferences");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox prefList = new VBox(6);
        prefList.getChildren().addAll(
                new Label("• Language: English"),
                new Label("• Currency: INR (₹)"),
                new Label("• Time Zone: IST (India Standard Time)"),
                new Label("• AI Recommendations: Enabled")
        );
        prefList.getChildren().forEach(n -> n.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";"));

        Button manageBtn = createSecondaryButton("Manage Preferences →");
        manageBtn.setOnAction(e -> showToast(stage, "Opening Settings.java..."));

        card.getChildren().addAll(title, new Separator(), prefList, manageBtn);
        return card;
    }

    private VBox createRecentActivityCard() {
        VBox card = createCardContainer();

        Label title = new Label("Recent Account Activity");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        VBox timeline = new VBox(8);
        timeline.getChildren().addAll(
                createActivityItem("✓ Profile updated", "Today, 10:30 AM"),
                createActivityItem("✓ Password changed", "Yesterday, 4:15 PM"),
                createActivityItem("✓ TechVision Summit created", "2 days ago")
        );

        card.getChildren().addAll(title, new Separator(), timeline);
        return card;
    }

    private VBox createActivityItem(String text, String time) {
        VBox b = new VBox(2);
        Label t = new Label(text); t.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label tm = new Label(time); tm.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        b.getChildren().addAll(t, tm);
        return b;
    }

    private VBox createFormField(String labelStr, Control control) {
        VBox box = new VBox(4);
        Label lbl = new Label(labelStr);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        control.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 10;");
        box.getChildren().addAll(lbl, control);
        return box;
    }

    // ==========================================
    // 8. BOTTOM PROFILE ACTIONS ROW
    // ==========================================
    private HBox createProfileActionsRow(Stage stage) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_RIGHT);

        Button settingsBtn = createSecondaryButton("Settings");
        settingsBtn.setOnAction(e -> showToast(stage, "Opening Settings.java..."));

        Button helpBtn = createSecondaryButton("Help & Support");
        helpBtn.setOnAction(e -> showToast(stage, "Opening Support.java..."));

        Button signOutBtn = createPrimaryButton("Sign Out");
        signOutBtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 18; -fx-cursor: hand;");
        signOutBtn.setOnAction(e -> openSignOutModal(stage));

        row.getChildren().addAll(settingsBtn, helpBtn, signOutBtn);
        return row;
    }

    // ==========================================
    // MODAL DIALOGS
    // ==========================================
    private void openAddSkillModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add Skill");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Add Skill & Expertise");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField skillInput = new TextField();
        skillInput.setPromptText("Enter skill name (e.g. Stage Management)");

        Button addBtn = createPrimaryButton("Add Skill");
        addBtn.setOnAction(e -> {
            if (!skillInput.getText().isEmpty()) {
                userSkills.add(skillInput.getText());
                renderSkills();
                showToast(parentStage, "✓ Skill added!");
                modal.close();
            }
        });

        layout.getChildren().addAll(title, skillInput, addBtn);

        Scene scene = new Scene(layout, 320, 180);
        modal.setScene(scene);
        modal.show();
    }

    private void openAddEmergencyContactModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Add Emergency Contact");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + WHITE + ";");

        Label title = new Label("Emergency Contact Details");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField nameIn = new TextField(); nameIn.setPromptText("Contact Name");
        TextField relIn = new TextField(); relIn.setPromptText("Relationship");
        TextField phoneIn = new TextField(); phoneIn.setPromptText("Phone Number");

        Button saveBtn = createPrimaryButton("Save Contact");
        saveBtn.setOnAction(e -> {
            if (!nameIn.getText().isEmpty()) {
                emergencyContactLabel.setText("Emergency: " + nameIn.getText() + " (" + phoneIn.getText() + ")");
                showToast(parentStage, "✓ Emergency contact saved!");
                modal.close();
            }
        });

        layout.getChildren().addAll(title, new Label("Name:"), nameIn, new Label("Relationship:"), relIn, new Label("Phone:"), phoneIn, saveBtn);

        Scene scene = new Scene(layout, 340, 320);
        modal.setScene(scene);
        modal.show();
    }

    private void openSignOutModal(Stage parentStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sign Out");
        alert.setHeaderText("Sign Out of EventXpert?");
        alert.setContentText("Are you sure you want to log out?");
        alert.initOwner(parentStage);
        alert.show();
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================
    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
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
        alert.setTitle("EventXpert Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.show();
    }
}