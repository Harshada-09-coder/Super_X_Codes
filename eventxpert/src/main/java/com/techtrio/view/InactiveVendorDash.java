package com.techtrio.view;

import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class InactiveVendorDash {
    private Scene inactiveVendorScene;
    private VendorProfile profileV;
    private final String uid;
    private final String idToken;

    // Preserve exact theme constants
    private static final String NAVY = "#062653";
    private static final String BLUE = "#092F70";
    private static final String BG = "#F5F7FB";
    private static final String BORDER = "#DCE2EA";
    private static final String TEXT = "#15171B";
    private static final String MUTED = "#667085";

    // Dynamic UI Labels
    private Label vendorBusinessNameLabel;
    private Label vendorCategoryLabel;

    public InactiveVendorDash(String uid, String idToken) {
        this.uid = uid;
        this.idToken = idToken;
    }

    public Scene getinactiveVendorScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // ================= SIDEBAR =================
        VBox side = new VBox(8);
        side.setPrefWidth(252);
        side.setPadding(new Insets(24, 16, 18, 16));
        side.setStyle("-fx-background-color:" + NAVY + ";");

        HBox logo = new HBox(12);
        logo.setAlignment(Pos.CENTER_LEFT);

        Label e = new Label("E");
        e.setMinSize(44, 44);
        e.setAlignment(Pos.CENTER);
        e.setStyle("-fx-background-color:white;-fx-text-fill:" + NAVY +
                ";-fx-font-size:22px;-fx-font-weight:bold;-fx-background-radius:8px;");

        VBox brand = new VBox(0);
        brand.getChildren().addAll(
                lbl("EventXpert", 21, Color.WHITE, true),
                lbl("VENDOR PORTAL", 11, Color.LIGHTGRAY, false)
        );

        logo.getChildren().addAll(e, brand);

        Button add = sideBtn("＋  Add Service", false);
        add.setStyle("-fx-background-color:white;-fx-text-fill:" + NAVY +
                ";-fx-font-size:16px;-fx-background-radius:8px;");

        side.getChildren().addAll(
                logo, gap(16), add,
                sideBtn("▦   Dashboard", true),
                sideBtn("▣   Service Requests", false),
                sideBtn("▣   My Bookings", false),
                sideBtn("▤   Services & Packages", false),
                sideBtn("▦   Calendar", false),
                sideBtn("▣   Earnings & Payments", false),
                sideBtn("♧   Notifications", false),
                sideBtn("♙   Settings", false),
                sideBtn("□   Support", false)
        );

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);

        HBox profile = new HBox(10);
        profile.setAlignment(Pos.CENTER_LEFT);
        profile.setPadding(new Insets(12, 8, 0, 8));
        profile.setStyle("-fx-cursor: hand;");

        Label vp = new Label("VP");
        vp.setMinSize(44, 44);
        vp.setAlignment(Pos.CENTER);
        vp.setStyle("-fx-background-color:white;-fx-text-fill:" + NAVY +
                ";-fx-font-weight:bold;-fx-background-radius:50%;");

        vendorBusinessNameLabel = lbl("Loading...", 14, Color.WHITE, true);
        vendorCategoryLabel = lbl("Vendor", 11, Color.LIGHTGRAY, false);

        VBox p = new VBox(2);
        p.getChildren().addAll(
                vendorBusinessNameLabel,
                vendorCategoryLabel,
                lbl("● Inactive", 11, Color.web("#FF7777"), false)
        );

        profile.getChildren().addAll(vp, p);
        profile.setOnMouseClicked(event -> openProfileView());

        side.getChildren().addAll(grow, profile);
        root.setLeft(side);

        // ================= MAIN CONTENT =================
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setFillWidth(true);

        // Welcome Header
        HBox welcome = new HBox(12);
        welcome.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(3);
        HBox title = new HBox(10);
        title.setAlignment(Pos.CENTER_LEFT);

        title.getChildren().addAll(
                lbl("Welcome Back 👋", 32, Color.web(TEXT), true),
                inactiveBadge()
        );

        welcomeText.getChildren().addAll(
                title,
                lbl("Your vendor account is currently inactive. Activate your account to start managing events and receiving requests.",
                        14, Color.web(TEXT), false)
        );

        Region wg = new Region();
        HBox.setHgrow(wg, Priority.ALWAYS);

        Button profileBtn = action("View Profile", false);
        profileBtn.setOnAction(evt -> openProfileView());

        Button activateBtn = action("⏻  Activate Account", true);
        activateBtn.setOnAction(evt -> openProfileView());

        welcome.getChildren().addAll(welcomeText, wg, profileBtn, activateBtn);

        // Inactive Card
        HBox inactive = new HBox(20);
        inactive.setAlignment(Pos.CENTER_LEFT);
        inactive.setPadding(new Insets(20));
        inactive.setMinHeight(145);
        inactive.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#FFB5B5;" +
                "-fx-border-radius:12px;" +
                "-fx-background-radius:12px;"
        );

        Label warning = lbl("⚠", 28, Color.web("#C62828"), true);
        StackPane wi = new StackPane(warning);
        wi.setMinSize(65, 65);
        wi.setStyle(
                "-fx-background-color:#FFF0F0;" +
                "-fx-border-color:#FFBBBB;" +
                "-fx-border-radius:50%;" +
                "-fx-background-radius:50%;"
        );

        VBox info = new VBox(6);
        info.getChildren().addAll(
                lbl("Vendor Account Inactive", 23, Color.web(TEXT), true),
                lbl("Your vendor profile is incomplete or pending activation. To receive event requests, manage bookings, and access vendor services, please complete the activation process.",
                        13, Color.web(TEXT), false)
        );

        HBox infoButtons = new HBox(10);
        infoButtons.setPadding(new Insets(4, 0, 0, 0));
        Button completeActivationBtn = action("Complete Activation", true);
        completeActivationBtn.setOnAction(event -> openProfileView());

        Button learnMoreBtn = action("Learn More", false);
        infoButtons.getChildren().addAll(completeActivationBtn, learnMoreBtn);
        info.getChildren().add(infoButtons);

        inactive.getChildren().addAll(wi, info);

        // Summary Cards
        HBox stats = new HBox(14);
        stats.getChildren().addAll(
                stat("Active Events", "00", "Account inactive", "▣"),
                stat("Pending Requests", "00", "Account inactive", "✉"),
                stat("Upcoming Events", "00", "Account inactive", "▦"),
                stat("Tasks Due Today", "00", "Account inactive", "▤")
        );

        // Lower Area
        HBox lower = new HBox(16);

        VBox upcoming = new VBox();
        upcoming.setStyle(card());
        HBox.setHgrow(upcoming, Priority.ALWAYS);

        Label upTitle = lbl("Upcoming Events", 19, Color.web(TEXT), true);
        upTitle.setPadding(new Insets(18, 20, 14, 20));

        Separator sep = new Separator();

        VBox empty = new VBox(10);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(25));

        Button proceedBtn = action("Activate Account to Proceed", true);
        proceedBtn.setOnAction(event -> openProfileView());

        empty.getChildren().addAll(
                lbl("▣", 35, Color.web("#C4CAD4"), true),
                lbl("No Upcoming Events", 24, Color.web(TEXT), true),
                centerText(
                        "Your calendar is empty because your vendor account is not active.\n" +
                        "Activate your account to start receiving bookings."
                ),
                proceedBtn
        );

        VBox.setVgrow(empty, Priority.ALWAYS);
        upcoming.getChildren().addAll(upTitle, sep, empty);

        // Right column
        VBox right = new VBox(16);
        right.setPrefWidth(320);

        VBox tasks = new VBox(12);
        tasks.setPadding(new Insets(18));
        tasks.setStyle(card());
        tasks.getChildren().addAll(
                lbl("Today's Tasks", 19, Color.web(TEXT), true),
                emptyBox("▧", "No tasks available")
        );

        VBox account = new VBox(13);
        account.setPadding(new Insets(18));
        account.setStyle(card());

        account.getChildren().addAll(
                lbl("Account Status", 19, Color.web(TEXT), true),
                progress("Profile Completion", "25%", 0.25),
                progress("Vendor Verification", "0%", 0),
                status("Current Status", "Inactive")
        );

        right.getChildren().addAll(tasks, account);
        lower.getChildren().addAll(upcoming, right);

        content.getChildren().addAll(welcome, inactive, stats, lower);

        // ScrollPane Setup
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color:" + BG + ";-fx-border-color:transparent;");

        root.setCenter(scroll);

        // Load Vendor Profile Data Asynchronously
        loadVendorDataAsync();

        inactiveVendorScene = new Scene(root, 1550, 750);
        return inactiveVendorScene;
    }

    private void loadVendorDataAsync() {
        if (uid == null || uid.isBlank()) return;

        Task<VendorProfile> task = new Task<>() {
            @Override
            protected VendorProfile call() {
                VendorProfileService service = new VendorProfileService();
                return service.getVendorProfile(uid);
            }
        };

        task.setOnSucceeded(e -> {
            profileV = task.getValue();
            if (profileV != null) {
                Platform.runLater(() -> {
                    if (vendorBusinessNameLabel != null) {
                        String bName = profileV.getBusinessName();
                        if (bName == null || bName.isBlank()) bName = profileV.getFullName();
                        if (bName == null || bName.isBlank()) bName = "Vendor";
                        vendorBusinessNameLabel.setText(bName);
                    }
                    if (vendorCategoryLabel != null && profileV.getCategory() != null) {
                        vendorCategoryLabel.setText(profileV.getCategory());
                    }
                });
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void openProfileView() {
        if (uid == null || uid.isBlank()) {
            showToast(LandingPage.landingPageStage, "User session not found.");
            return;
        }
        ProfileV profilePage = new ProfileV(uid);
        profilePage.setInactiveVendorDash(this);
        LandingPage.landingPageStage.setScene(profilePage.getprofileVScene());
    }

    // ================= HELPERS =================
    private void showToast(Stage owner, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.show();
    }

    private Label lbl(String text, double size, Color color, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        l.setTextFill(color);
        l.setWrapText(true);
        return l;
    }

    private Label centerText(String text) {
        Label l = lbl(text, 14, Color.web(TEXT), false);
        l.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private Button sideBtn(String text, boolean active) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(45);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(0, 8, 0, 8));
        b.setStyle(
                "-fx-background-color:" + (active ? "white" : "transparent") + ";" +
                "-fx-text-fill:" + (active ? NAVY : "#D5DCEC") + ";" +
                "-fx-font-size:15px;" +
                "-fx-background-radius:7px;"
        );
        return b;
    }

    private Label inactiveBadge() {
        Label l = lbl("ⓘ INACTIVE", 12, Color.web("#C62828"), true);
        l.setStyle(
                "-fx-background-color:#FFE4E4;" +
                "-fx-border-color:#FFAAAA;" +
                "-fx-border-radius:14px;" +
                "-fx-background-radius:14px;" +
                "-fx-padding:5 11 5 11;"
        );
        return l;
    }

    private Button action(String text, boolean primary) {
        Button b = new Button(text);
        b.setPrefHeight(40);
        b.setPadding(new Insets(0, 17, 0, 17));
        b.setStyle(primary ?
                "-fx-background-color:" + BLUE + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:8px;"
                :
                "-fx-background-color:white;" +
                "-fx-text-fill:#20242A;" +
                "-fx-border-color:#AEB5C0;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-font-size:14px;"
        );
        return b;
    }

    private VBox stat(String title, String value, String sub, String icon) {
        VBox box = new VBox(7);
        box.setPadding(new Insets(16));
        box.setPrefHeight(135);
        box.setStyle(card());
        HBox.setHgrow(box, Priority.ALWAYS);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label t = lbl(title, 14, Color.web(MUTED), false);
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);

        top.getChildren().addAll(t, r, lbl(icon, 19, Color.web("#C1C7D2"), false));

        box.getChildren().addAll(
                top,
                lbl(value, 38, Color.web("#9095A1"), true),
                lbl(sub, 12, Color.web("#C1C6D1"), false)
        );

        return box;
    }

    private String card() {
        return "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12px;" +
                "-fx-background-radius:12px;";
    }

    private VBox emptyBox(String icon, String text) {
        VBox b = new VBox(7);
        b.setAlignment(Pos.CENTER);
        b.setPrefHeight(100);
        b.setStyle(
                "-fx-background-color:#FAFBFC;" +
                "-fx-border-color:#D8DDE5;" +
                "-fx-border-style:dashed;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;"
        );

        b.getChildren().addAll(
                lbl(icon, 22, Color.web("#B9C0CC"), true),
                lbl(text, 13, Color.web("#B9C0CC"), false)
        );
        return b;
    }

    private VBox progress(String name, String value, double amount) {
        VBox box = new VBox(5);
        HBox row = new HBox();
        Label n = lbl(name, 13, Color.web(TEXT), false);
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);

        row.getChildren().addAll(n, r, lbl(value, 13, Color.web(TEXT), false));

        ProgressBar bar = new ProgressBar(amount);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(7);

        box.getChildren().addAll(row, bar);
        return box;
    }

    private HBox status(String name, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(11));

        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);

        row.getChildren().addAll(
                lbl(name, 13, Color.web(TEXT), false),
                r,
                lbl(value, 13, Color.web("#687080"), false)
        );

        row.setStyle(
                "-fx-background-color:#F7F8FA;" +
                "-fx-border-color:#E0E3E8;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;"
        );

        return row;
    }

    private Region gap(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        return r;
    }

    public void backToInactVendorDashboard() {
        if (inactiveVendorScene != null && LandingPage.landingPageStage != null) {
            LandingPage.landingPageStage.setScene(inactiveVendorScene);
        }
    }
}