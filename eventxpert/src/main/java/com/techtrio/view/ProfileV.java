package com.techtrio.view;

import java.util.HashMap;
import java.util.Map;

import com.techtrio.model.VendorProfile;
import com.techtrio.services.VendorProfileService;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

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

public class ProfileV {

    private Scene profileVScene;
    private VendorDashboardV vendorDashboard;
    private InactiveVendorDash inactVendorDashboard;
    private final String vendorId;
    private String idToken;

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

    private Label busNameLabel;
    private Label busCategoryLabel;
    private Label busLocationLabel;
    private Label busPhoneLabel;
    private Label busEmailLabel;
    private Label busBioLabel;
    private Label businessNameInfoLabel;
    private Label businessTypeInfoLabel;
    private Label vendorIdInfoLabel;
    private Label primaryLocationInfoLabel;
    private Label availabilityInfoLabel;

    private StackPane modalOverlayPane;

    public ProfileV(String vendorId) {
        this(vendorId, UserSession.getIdToken());
    }

    public ProfileV(String vendorId, String idToken) {
        this.vendorId = (vendorId != null && !vendorId.isBlank()) ? vendorId : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public void setVendorDashboard(VendorDashboardV vendorDashboard) {
        this.vendorDashboard = vendorDashboard;
    }

    public void setInactiveVendorDash(InactiveVendorDash inactVendorDashboard) {
        this.inactVendorDashboard = inactVendorDashboard;
    }

    public Scene getprofileVScene() {
        StackPane rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        BorderPane mainLayout = new BorderPane();
        VendorDashboardV dashHelper = new VendorDashboardV(vendorId, idToken);
        mainLayout.setLeft(dashHelper.createCommonVendorSidebar("Profile"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BACKGROUND + "; -fx-border-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + BACKGROUND + ";");

        mainContent.getChildren().addAll(
                createPageHeader(),
                createHeroProfileCard(),
                createTwoColumnMainLayout()
        );

        scrollPane.setContent(mainContent);
        mainLayout.setCenter(scrollPane);

        modalOverlayPane = new StackPane();
        modalOverlayPane.setStyle("-fx-background-color: rgba(6, 27, 65, 0.45);");
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);

        rootStack.getChildren().addAll(mainLayout, modalOverlayPane);

        if (vendorId != null && !vendorId.isBlank()) {
            loadVendorProfile(vendorId);
        }

        profileVScene = new Scene(rootStack, 1550, 785);
        return profileVScene;
    }

    private VBox createPageHeader() {
        VBox header = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Vendor Business Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit & Save Profile");
        FontIcon editIcon = new FontIcon("fas-edit");
        editIcon.setIconSize(11);
        editIcon.setIconColor(Color.WHITE);
        editBtn.setGraphic(editIcon);
        editBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6px; -fx-cursor: hand;");
        editBtn.setOnAction(e -> showInAppEditProfileModal());

        titleRow.getChildren().addAll(title, spacer, editBtn);

        Label subtitle = new Label("Manage your verified business info, category listings and contact channels.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(titleRow, subtitle);
        return header;
    }

    private VBox createHeroProfileCard() {
        VBox card = createCardContainer();
        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(36, Color.web(LIGHT_BLUE));
        FontIcon userIcon = new FontIcon("fas-user-tie");
        userIcon.setIconSize(24);
        userIcon.setIconColor(Color.web(PRIMARY_BLUE));
        StackPane avatarPane = new StackPane(avatarCircle, userIcon);

        VBox infoBox = new VBox(4);
        busNameLabel = new Label("Loading Vendor...");
        busNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        busCategoryLabel = new Label("Loading Category...");
        busCategoryLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        HBox metaRow = new HBox(12);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        busLocationLabel = new Label("📍 Location loading...");
        busLocationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label verifiedBadge = new Label("✓ Verified Vendor");
        verifiedBadge.setStyle("-fx-background-color: " + GREEN_BG + "; -fx-text-fill: " + GREEN_TEXT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 10px;");

        metaRow.getChildren().addAll(busLocationLabel, verifiedBadge);
        infoBox.getChildren().addAll(busNameLabel, busCategoryLabel, metaRow);

        heroRow.getChildren().addAll(avatarPane, infoBox);
        card.getChildren().add(heroRow);
        return card;
    }

    private HBox createTwoColumnMainLayout() {
        HBox layout = new HBox(20);

        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        leftCol.getChildren().addAll(
                createBusinessInformationCard(),
                createAboutBusinessCard()
        );

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(360);
        rightCol.getChildren().add(createVerificationStatusCard());

        layout.getChildren().addAll(leftCol, rightCol);
        return layout;
    }

    private VBox createBusinessInformationCard() {
        VBox card = createCardContainer();
        Label title = new Label("Business Information");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(10);

        businessNameInfoLabel = new Label("Loading...");
        businessTypeInfoLabel = new Label("Loading...");
        vendorIdInfoLabel = new Label("Loading...");
        primaryLocationInfoLabel = new Label("Loading...");
        availabilityInfoLabel = new Label("Loading...");

        grid.add(createSpecItem("BUSINESS NAME", businessNameInfoLabel), 0, 0);
        grid.add(createSpecItem("SERVICE CATEGORY", businessTypeInfoLabel), 1, 0);
        grid.add(createSpecItem("VENDOR ID", vendorIdInfoLabel), 0, 1);
        grid.add(createSpecItem("PRIMARY LOCATION", primaryLocationInfoLabel), 0, 2);
        grid.add(createSpecItem("AVAILABILITY", availabilityInfoLabel), 1, 2);

        HBox contactGrid = new HBox(20);
        busEmailLabel = new Label("✉ Loading..."); busEmailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + PRIMARY_BLUE + ";");
        busPhoneLabel = new Label("📞 Loading..."); busPhoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        contactGrid.getChildren().addAll(busEmailLabel, busPhoneLabel);

        card.getChildren().addAll(title, new Separator(), grid, new Separator(), contactGrid);
        return card;
    }

    private VBox createAboutBusinessCard() {
        VBox card = createCardContainer();
        Label title = new Label("About Business & Portfolio");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        busBioLabel = new Label("Vendor information dynamically synced from Firestore.");
        busBioLabel.setWrapText(true);
        busBioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(title, new Separator(), busBioLabel);
        return card;
    }

    private VBox createVerificationStatusCard() {
        VBox card = createCardContainer();
        Label title = new Label("Verification Status");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");

        VBox list = new VBox(8);
        list.getChildren().addAll(
                createVerificationRow("Business Registration", "Verified ✓", true),
                createVerificationRow("Contact Information", "Phone & Email Active ✓", true),
                createVerificationRow("Escrow Payment Status", "Verified & Ready ✓", true)
        );

        card.getChildren().addAll(title, new Separator(), list);
        return card;
    }

    private HBox createVerificationRow(String title, String desc, boolean isDone) {
        HBox r = new HBox(8);
        Label icon = new Label(isDone ? "✓" : "⏳");
        icon.setStyle("-fx-text-fill: " + GREEN_TEXT + "; -fx-font-weight: bold;");

        VBox t = new VBox(1);
        Label head = new Label(title); head.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MAIN + ";");
        Label sub = new Label(desc); sub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        t.getChildren().addAll(head, sub);

        r.getChildren().addAll(icon, t);
        return r;
    }

    private VBox createSpecItem(String label, Label valueLabel) {
        VBox box = new VBox(2);
        Label title = new Label(label);
        title.setStyle("-fx-font-size: 9px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MAIN + ";");
        box.getChildren().addAll(title, valueLabel);
        return box;
    }

// ⭐ Compact Horizontal Rectangle Pop-up for Editing Profile
    private void showInAppEditProfileModal() {
        modalOverlayPane.getChildren().clear();
        modalOverlayPane.setAlignment(Pos.CENTER);

        VBox card = new VBox(12);
        card.setMaxWidth(460);
        card.setMaxHeight(Region.USE_PREF_SIZE); // 🎯 Height 
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.22), 16, 0, 0, 4);");

        StackPane.setAlignment(card, Pos.CENTER);

        Label title = new Label("Edit Vendor Profile");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SIDEBAR_NAVY + ";");

        String currentBusName = busNameLabel != null ? busNameLabel.getText() : "";
        String currentPhone = busPhoneLabel != null ? busPhoneLabel.getText().replace("📞 ", "") : "";
        String currentEmail = busEmailLabel != null ? busEmailLabel.getText().replace("✉ ", "") : "";
        String currentBio = busBioLabel != null ? busBioLabel.getText() : "";

        TextField nameIn = new TextField(currentBusName);
        TextField phoneIn = new TextField(currentPhone);
        TextField emailIn = new TextField(currentEmail);
        TextArea bioIn = new TextArea(currentBio);
        bioIn.setPrefRowCount(2);

        HBox btns = new HBox(8);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setPadding(new Insets(6, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 6 14; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeInAppModal());

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 6px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("businessName", nameIn.getText().trim());
            updates.put("phone", phoneIn.getText().trim());
            updates.put("email", emailIn.getText().trim());
            updates.put("bio", bioIn.getText().trim());
            updates.put("profileCompleted", true);

            Task<Boolean> updateTask = new Task<>() {
                @Override
                protected Boolean call() {
                    VendorProfileService service = new VendorProfileService();
                    return service.updateVendorProfileFields(vendorId, updates);
                }
            };

            updateTask.setOnSucceeded(evt -> {
                loadVendorProfile(vendorId);
                closeInAppModal();
            });

            Thread thread = new Thread(updateTask);
            thread.setDaemon(true);
            thread.start();
        });

        btns.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(
                title, new Separator(),
                new Label("Business Name:"), nameIn,
                new Label("Phone:"), phoneIn,
                new Label("Email:"), emailIn,
                new Label("About Business:"), bioIn,
                btns
        );

        modalOverlayPane.getChildren().add(card);
        modalOverlayPane.setVisible(true);
        modalOverlayPane.setManaged(true);
    }

    private void closeInAppModal() {
        modalOverlayPane.setVisible(false);
        modalOverlayPane.setManaged(false);
        modalOverlayPane.getChildren().clear();
    }

    public void loadVendorProfile(String vId) {
        Task<VendorProfile> task = new Task<>() {
            @Override
            protected VendorProfile call() {
                VendorProfileService service = new VendorProfileService();
                return service.getVendorProfile(vId);
            }
        };

        task.setOnSucceeded(e -> {
            VendorProfile vendor = task.getValue();
            if (vendor != null) {
                Platform.runLater(() -> {
                    String bName = vendor.getBusinessName() != null ? vendor.getBusinessName() : "Vendor Name";
                    String cat = vendor.getCategory() != null ? vendor.getCategory() : "Service Category";
                    String addr = vendor.getAddress() != null ? vendor.getAddress() : "Pune, Maharashtra";
                    String ph = vendor.getPhone() != null ? vendor.getPhone() : (vendor.getMobileNo() != null ? vendor.getMobileNo() : "N/A");
                    String em = vendor.getEmail() != null ? vendor.getEmail() : "N/A";

                    if (busNameLabel != null) busNameLabel.setText(bName);
                    if (busCategoryLabel != null) busCategoryLabel.setText(cat);
                    if (busLocationLabel != null) busLocationLabel.setText("📍 " + addr);
                    if (busPhoneLabel != null) busPhoneLabel.setText("📞 " + ph);
                    if (busEmailLabel != null) busEmailLabel.setText("✉ " + em);
                    if (businessNameInfoLabel != null) businessNameInfoLabel.setText(bName);
                    if (businessTypeInfoLabel != null) businessTypeInfoLabel.setText(cat);
                    if (vendorIdInfoLabel != null) vendorIdInfoLabel.setText(vId);
                    if (primaryLocationInfoLabel != null) primaryLocationInfoLabel.setText(addr);
                    if (availabilityInfoLabel != null) availabilityInfoLabel.setText("Active & Available");
                    if (busBioLabel != null) busBioLabel.setText(vendor.getBio() != null ? vendor.getBio() : bName + " provides verified " + cat + " services across " + addr);
                });
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox createCardContainer() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10px;");
        return card;
    }
}