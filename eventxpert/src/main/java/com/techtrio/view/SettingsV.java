package com.techtrio.view;

import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SettingsV {

    private Scene settingsVScene;
    private VendorDashboardV vendorDashboard;
    private String uid;
    private String idToken;

    private static final String PRIMARY_BLUE = "#0B4EA2";
    private static final String LIGHT_BLUE = "#EAF3FF";
    private static final String BACKGROUND = "#F7FAFE";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_MAIN = "#102A43";
    private static final String SECONDARY_TEXT = "#52677D";
    private static final String BORDER_COLOR = "#DCE5F0";

    private VBox settingsContentHolder;
    private Button btnGeneralNav;
    private Button btnNotifNav;
    private Button btnSecurityNav;

    public SettingsV() {
        this(UserSession.getUid(), UserSession.getIdToken());
    }

    public SettingsV(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public Scene getsettingsVScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // 1. Standard Sidebar
        VendorDashboardV dashHelper = new VendorDashboardV(uid, idToken);
        root.setLeft(dashHelper.createCommonVendorSidebar("Settings"));

        // 2. Main ScrollPane (Top Bar Removed)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createTwoColumnMainLayout()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        settingsVScene = new Scene(root, 1550, 785);
        return settingsVScene;
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        Label title = new Label("Account Settings & Preferences");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label subtitle = new Label("Configure your vendor notification alerts, security and working preferences.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createTwoColumnMainLayout() {
        HBox layout = new HBox(20);

        VBox navCard = createCardContainer();
        navCard.setPrefWidth(220);
        navCard.setSpacing(4);

        btnGeneralNav = createNavBtn("⚙ General", true);
        btnNotifNav = createNavBtn("🔔 Notifications", false);
        btnSecurityNav = createNavBtn("🛡 Security", false);

        btnGeneralNav.setOnAction(e -> switchSettingsPane(btnGeneralNav, createGeneralSettingsPane()));
        btnNotifNav.setOnAction(e -> switchSettingsPane(btnNotifNav, createNotificationSettingsPane()));
        btnSecurityNav.setOnAction(e -> switchSettingsPane(btnSecurityNav, createSecuritySettingsPane()));

        navCard.getChildren().addAll(btnGeneralNav, btnNotifNav, btnSecurityNav);

        settingsContentHolder = new VBox();
        HBox.setHgrow(settingsContentHolder, Priority.ALWAYS);
        settingsContentHolder.getChildren().add(createGeneralSettingsPane());

        layout.getChildren().addAll(navCard, settingsContentHolder);
        return layout;
    }

    private Button createNavBtn(String title, boolean isActive) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 8 12;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px; -fx-padding: 8 12; -fx-cursor: hand;");
        }
        return btn;
    }

    private void switchSettingsPane(Button activeBtn, VBox pane) {
        Button[] all = {btnGeneralNav, btnNotifNav, btnSecurityNav};
        for (Button b : all) b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MAIN + "; -fx-font-size: 12px; -fx-padding: 8 12; -fx-cursor: hand;");
        activeBtn.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-padding: 8 12;");

        settingsContentHolder.getChildren().clear();
        settingsContentHolder.getChildren().add(pane);
    }

    private VBox createGeneralSettingsPane() {
        VBox card = createCardContainer();
        Label title = new Label("General Working Preferences");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createToggleRow("Auto Confirmation Prompt", "Prompt confirmation before declining bookings.", true),
                createToggleRow("Show Availability Online", "Display available status to event organizers.", true)
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private VBox createNotificationSettingsPane() {
        VBox card = createCardContainer();
        Label title = new Label("Notification Settings");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createToggleRow("New Request Alerts", "Instant in-app alerts when an organizer assigns your service.", true),
                createToggleRow("Booking Updates", "Receive alerts on booking confirmations.", true)
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private VBox createSecuritySettingsPane() {
        VBox card = createCardContainer();
        Label title = new Label("Password & Security");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Label stat = new Label("Account Security Status: Active & Secured ✓");
        stat.setStyle("-fx-font-size: 12px; -fx-text-fill: #16A34A; -fx-font-weight: bold;");

        card.getChildren().addAll(title, new Separator(), stat);
        return card;
    }

    private HBox createToggleRow(String title, String desc, boolean isChecked) {
        HBox r = new HBox();
        r.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(2);
        Label t = new Label(title); t.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label d = new Label(desc); d.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        text.getChildren().addAll(t, d);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        CheckBox toggle = new CheckBox(); toggle.setSelected(isChecked);

        r.getChildren().addAll(text, sp, toggle);
        return r;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}