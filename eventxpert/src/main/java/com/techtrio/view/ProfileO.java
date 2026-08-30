package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.controller.OrganizerController;
import com.techtrio.controller.OrganizerState;
import com.techtrio.model.OrganizerProfile;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ProfileO {

    private Scene profileOScene;
    private OrganizerDashboardO activeDashboard;
    private InactiveOrgDash inactiveDashboard;
    private final String uid;
    private String idToken;

    private static final String NAVY = "#062A60";
    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";
    private static final String GREEN_BG = "#E6F4EA";
    private static final String GREEN_TEXT = "#137333";

    private Label heroNameLabel;
    private Label heroRoleLabel;
    private Label heroOrgLabel;

    private TextField nameInput;
    private TextField phoneInput;
    private TextField locationInput;
    private TextField orgNameInput;

    public ProfileO() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public ProfileO(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Organizer");
    }

    public void setOrganizerDashboard(OrganizerDashboardO dashboard) {
        this.activeDashboard = dashboard;
    }

    public void setInactiveOrgDash(InactiveOrgDash dashboard) {
        this.inactiveDashboard = dashboard;
    }

    public Scene getProfileScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        Stage stage = LandingPage.landingPageStage;

        VBox sidebar = createSidebar(stage);
        root.setLeft(sidebar);

        HBox topBar = createTopBar(stage);
        root.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(stage),
                createHeroProfileCard(stage),
                createPersonalInfoCard(stage)
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        loadOrganizerProfileAsync();

        profileOScene = new Scene(root, 1550, 785);
        return profileOScene;
    }

    private void loadOrganizerProfileAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<OrganizerProfile> task = new Task<>() {
            @Override
            protected OrganizerProfile call() {
                OrganizerController controller = new OrganizerController();
                return controller.getOrganizerProfile(uid);
            }
        };

        task.setOnSucceeded(e -> {
            OrganizerProfile p = task.getValue();
            if (p != null) {
                Platform.runLater(() -> {
                    String name = p.getName() != null && !p.getName().isBlank() ? p.getName() : "Organizer";
                    String org = p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : "EventXpert Events";
                    String phone = p.getMobileNo() != null && !p.getMobileNo().isBlank() ? p.getMobileNo() : "Not Provided";
                    String loc = p.getAddress() != null && !p.getAddress().isBlank() ? p.getAddress() : "Pune, Maharashtra";

                    if (heroNameLabel != null) heroNameLabel.setText(name);
                    if (heroRoleLabel != null) heroRoleLabel.setText("Event Organizer • " + org);
                    if (heroOrgLabel != null) heroOrgLabel.setText("📍 " + loc + " | ✉ " + p.getEmail() + " | 📞 " + phone);

                    if (nameInput != null) nameInput.setText(name);
                    if (phoneInput != null) phoneInput.setText(phone);
                    if (locationInput != null) locationInput.setText(loc);
                    if (orgNameInput != null) orgNameInput.setText(org);
                });
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setSpacing(6);

        Label brandLabel = new Label("EventXpert");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        sidebar.getChildren().addAll(brandLabel, new Separator(),
            createSidebarItem("Dashboard", false, e -> navigateBackToDashboard())
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().add(spacer);
        return sidebar;
    }

    private void navigateBackToDashboard() {
        if (activeDashboard != null) {
            activeDashboard.backToOrganizerDashboard();
        } else if (inactiveDashboard != null) {
            inactiveDashboard.backToInactiveOrganizerDashboard();
        } else {
            OrganizerController controller = new OrganizerController();
            OrganizerState state = controller.getOrganizerState(uid, idToken);
            if (state != null && state.isProfileCompleted()) {
                OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
                LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
            } else {
                InactiveOrgDash inactDash = new InactiveOrgDash(uid, idToken);
                LandingPage.landingPageStage.setScene(inactDash.getinactiveOrganizerScene());
            }
        }
    }

    private Button createSidebarItem(String title, boolean isActive, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(handler);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand;");
        return btn;
    }

    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label label = new Label("Organizer Profile");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        topBar.getChildren().add(label);
        return topBar;
    }

    private VBox createPageHeader(Stage stage) {
        VBox headerBox = new VBox(6);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = createSecondaryButton("← Back to Dashboard");
        backBtn.setOnAction(e -> navigateBackToDashboard());

        titleRow.getChildren().addAll(title, spacer, backBtn);
        headerBox.getChildren().add(titleRow);
        return headerBox;
    }

    private VBox createHeroProfileCard(Stage stage) {
        VBox card = createCardContainer();
        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        Circle bigAvatar = new Circle(32, Color.web(PRIMARY_BLUE));
        Text bigText = new Text("OP");
        bigText.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        StackPane avatarPane = new StackPane(bigAvatar, bigText);

        VBox infoBox = new VBox(4);
        heroNameLabel = new Label("Loading Name...");
        heroNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        heroRoleLabel = new Label("Event Organizer");
        heroRoleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        heroOrgLabel = new Label("Loading profile details...");
        heroOrgLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        infoBox.getChildren().addAll(heroNameLabel, heroRoleLabel, heroOrgLabel);
        heroRow.getChildren().addAll(avatarPane, infoBox);
        card.getChildren().add(heroRow);
        return card;
    }

    private VBox createPersonalInfoCard(Stage stage) {
        VBox card = createCardContainer();
        Label title = new Label("Personal & Organization Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(12);

        nameInput = new TextField("Loading...");
        phoneInput = new TextField("Loading...");
        locationInput = new TextField("Loading...");
        orgNameInput = new TextField("Loading...");

        form.add(createFormField("Full Name", nameInput), 0, 0);
        form.add(createFormField("Phone Number", phoneInput), 1, 0);
        form.add(createFormField("Organization Name", orgNameInput), 0, 1);
        form.add(createFormField("Location", locationInput), 1, 1);

        Button saveBtn = createPrimaryButton("Save & Activate Account");
        saveBtn.setOnAction(e -> {
            String newName = nameInput.getText().trim();
            String newPhone = phoneInput.getText().trim();
            String newLoc = locationInput.getText().trim();
            String newOrg = orgNameInput.getText().trim();

            saveBtn.setDisable(true);
            saveBtn.setText("Saving...");

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("mobileNo", newPhone);
            updates.put("address", newLoc);
            updates.put("organizationName", newOrg);

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    OrganizerController controller = new OrganizerController();
                    boolean updated = controller.updateOrganizerProfileFields(uid, updates);
                    if (updated) {
                        controller.markOrganizerAsExisting(uid);
                    }
                    return updated;
                }
            };

            task.setOnSucceeded(evt -> {
                boolean success = task.getValue();
                saveBtn.setDisable(false);
                saveBtn.setText("Save & Activate Account");

                if (success) {
                    OrganizerDashboardO activeDash = new OrganizerDashboardO(uid, idToken);
                    LandingPage.landingPageStage.setScene(activeDash.getorganizerDashboardScene(null));
                }
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        card.getChildren().addAll(title, new Separator(), form, new Separator(), saveBtn);
        return card;
    }

    private VBox createFormField(String labelStr, Control control) {
        VBox box = new VBox(4);
        Label lbl = new Label(labelStr);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        control.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 10;");
        box.getChildren().addAll(lbl, control);
        return box;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: " + WHITE + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + WHITE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6 12; -fx-cursor: hand;");
        return btn;
    }
}