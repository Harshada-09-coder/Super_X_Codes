package com.techtrio.view;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.firebase.cloud.FirestoreClient;
import com.techtrio.model.Event;
import com.techtrio.session.UserSession;
import com.techtrio.util.QRCodeUtil;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Guest {

    private String uid;
    private String idToken;
    private Event event;
    private Runnable backAction;

    private static final String NAVY = "#061B41";
    private static final String PRIMARY_BLUE = "#0D3979";
    private static final String ACTIVE_NAV_BLUE = "#2563EB";
    private static final String BG = "#F4F7FC";
    private static final String TEXT_DARK = "#101828";
    private static final String TEXT_MUTED = "#64748B";
    private static final String BORDER = "#E2E8F0";

    // Dynamic UI Labels
    private Label lblTotalGuests;
    private Label lblCheckedIn;
    private Label lblRemaining;
    private Label lblCheckInMode;
    private Label lblEventCode;
    private Label lblRowMaxGuests;
    private Label lblRowCheckedIn;
    private Label lblRowRemaining;
    private Label lblCheckInStarted;
    private Label lblCheckInEnds;
    private Button btnViewGuestList;
    private VBox qrCard;

    private ListenerRegistration firestoreListener;

    public Guest() {
        this(UserSession.getUid(), UserSession.getIdToken(), null);
    }

    public Guest(String uid, String idToken, Event event) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        this.event = event;
    }

    public Scene getGuestScene(Runnable backAction) {
        this.backAction = backAction;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        root.setLeft(createCommonSidebar("Guest Management"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainContent = createGuestContent();
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        // Real-Time Firestore Listener
        initRealtimeEventListener();

        return new Scene(root, 1550, 785);
    }

    private VBox createCommonSidebar(String activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(4, 8, 12, 8));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(20);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        Label brandTitle = new Label("EventXpert");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        brandBox.getChildren().addAll(logoIcon, brandTitle);

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(6, 8, 14, 8));

        Circle userAvatar = new Circle(16, Color.web("#DBEAFE"));
        VBox userDetails = new VBox(1);
        Label userName = new Label(UserSession.getUserName() != null ? UserSession.getUserName() : "Customer");
        userName.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label userEmail = new Label(UserSession.getUserEmail() != null ? UserSession.getUserEmail() : "customer@email.com");
        userEmail.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px;");
        userDetails.getChildren().addAll(userName, userEmail);
        userBox.getChildren().addAll(userAvatar, userDetails);

        VBox mainNav = new VBox(3);
        mainNav.getChildren().addAll(
                createNavItem("Dashboard", "fas-tachometer-alt", false, this::goDashboard),
                createNavItem("Create Event", "fas-plus-circle", false, this::goCreateEvent),
                createNavItem("My Events", "fas-calendar-check", false, this::handleBack)
        );

        Label sectionLabel = new Label("EVENT DETAILS");
        sectionLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 12 8 4 8;");

        VBox eventNav = new VBox(3);
        eventNav.getChildren().addAll(
                createNavItem("Overview", "fas-th-large", "Overview".equals(activeTab), this::goOverview),
                createNavItem("Venue", "fas-map-marker-alt", "Venue".equals(activeTab), this::goVenue),
                createNavItem("Budget", "fas-wallet", "Budget".equals(activeTab), this::goBudget),
                createNavItem("Guest Management", "fas-users", "Guest Management".equals(activeTab), () -> {}),
                createNavItem("Payments", "fas-credit-card", "Payments".equals(activeTab), this::goPayment),
                createNavItem("Requirements", "fas-clipboard-list", "Requirements".equals(activeTab), this::goRequirement)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(3);
        bottomNav.getChildren().addAll(
                createNavItem("Support", "fas-life-ring", false, null),
                createNavItem("Settings", "fas-cog", false, this::goSettings)
        );

        sidebar.getChildren().addAll(brandBox, userBox, mainNav, sectionLabel, eventNav, spacer, bottomNav);
        return sidebar;
    }

    private Button createNavItem(String title, String iconLit, boolean active, Runnable action) {
        Button btn = new Button("  " + title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(34);
        btn.setPadding(new Insets(0, 10, 0, 10));

        FontIcon icon = new FontIcon(iconLit);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.WHITE : Color.web("#94A3B8"));
        btn.setGraphic(icon);

        if (active) {
            btn.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 6px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-cursor: hand;"));
        }

        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    // ================= GUEST CONTENT =================
    private VBox createGuestContent() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(24, 32, 32, 32));

        Hyperlink backLink = new Hyperlink("← Back to My Events");
        backLink.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-underline: false;");
        backLink.setOnAction(e -> handleBack());

        VBox titleBox = new VBox(3);
        Label heading = new Label("Guest Management");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label sub = new Label("Manage your guest list and real-time check-in details");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(heading, sub);

        // 3 SEPARATE DYNAMIC GUEST SUMMARY CARDS
        HBox summaryCardsRow = new HBox(16);
        lblTotalGuests = new Label("0");
        lblCheckedIn = new Label("0");
        lblRemaining = new Label("0");

        summaryCardsRow.getChildren().addAll(
                createGuestStatCard("Total Guests", lblTotalGuests, "fas-users", "#0284C7"),
                createGuestStatCard("Checked In", lblCheckedIn, "fas-user-check", "#16A34A"),
                createGuestStatCard("Remaining", lblRemaining, "fas-user-clock", "#D97706")
        );

        // QR Code + Details Split Row
        HBox bodyRow = new HBox(20);

        // Single Event QR Card (Only visible if Single QR mode is selected)
        qrCard = new VBox(12);
        qrCard.setPadding(new Insets(18));
        qrCard.setPrefWidth(320);
        qrCard.setAlignment(Pos.CENTER);
        qrCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        lblCheckInMode = new Label("Check-In Mode: Single Event QR");
        lblCheckInMode.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");

        StackPane qrGraphicHolder = new StackPane();
        qrGraphicHolder.setPrefSize(130, 130);
        qrGraphicHolder.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 8px;");
        FontIcon qrIcon = new FontIcon("fas-qrcode");
        qrIcon.setIconSize(80);
        qrIcon.setIconColor(Color.web(NAVY));
        qrGraphicHolder.getChildren().add(qrIcon);

        lblEventCode = new Label("Event: " + (event != null && event.getEventName() != null ? event.getEventName() : ""));
        lblEventCode.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        Button downloadBtn = new Button("Download QR");
        downloadBtn.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 14; -fx-border-radius: 6px; -fx-cursor: hand;");
        downloadBtn.setOnAction(e -> handleDownloadQrModal());

        qrCard.getChildren().addAll(lblCheckInMode, qrGraphicHolder, lblEventCode, downloadBtn);

        // Details Card
        VBox detailsCard = new VBox(10);
        detailsCard.setPadding(new Insets(18));
        detailsCard.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(detailsCard, Priority.ALWAYS);

        Label gDetTitle = new Label("Guest Details");
        gDetTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        lblCheckInStarted = new Label("10:00 AM (Event Date)");
        lblCheckInEnds = new Label("11:00 PM (Event Date)");
        lblRowMaxGuests = new Label("0");
        lblRowCheckedIn = new Label("0 (0%)");
        lblRowRemaining = new Label("0 (0%)");

        VBox gRows = new VBox(10);
        gRows.setPadding(new Insets(8, 0, 10, 0));
        gRows.getChildren().addAll(
                createKeyValueRow("Check-In Started", lblCheckInStarted),
                createKeyValueRow("Check-In Ends", lblCheckInEnds),
                createKeyValueRow("Maximum Guests", lblRowMaxGuests),
                createKeyValueRow("Checked In", lblRowCheckedIn),
                createKeyValueRow("Remaining", lblRowRemaining)
        );

        btnViewGuestList = new Button("👥 View Guest List");
        btnViewGuestList.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 0; -fx-cursor: hand;");
        btnViewGuestList.setOnAction(e -> handleViewGuestListModal());

        detailsCard.getChildren().addAll(gDetTitle, gRows, btnViewGuestList);
        bodyRow.getChildren().addAll(qrCard, detailsCard);

        page.getChildren().addAll(backLink, titleBox, summaryCardsRow, bodyRow);
        return page;
    }

    private HBox createGuestStatCard(String title, Label countLabel, String iconLit, String iconColorHex) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        HBox.setHgrow(card, Priority.ALWAYS);

        StackPane iconHolder = new StackPane();
        Circle c = new Circle(20, Color.web(iconColorHex).deriveColor(0, 1, 1, 0.15));
        FontIcon fontIcon = new FontIcon(iconLit);
        fontIcon.setIconSize(18);
        fontIcon.setIconColor(Color.web(iconColorHex));
        iconHolder.getChildren().addAll(c, fontIcon);

        VBox textVBox = new VBox(1);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        countLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        textVBox.getChildren().addAll(titleLbl, countLabel);
        card.getChildren().addAll(iconHolder, textVBox);
        return card;
    }

    private HBox createKeyValueRow(String key, Label valLabel) {
        HBox row = new HBox();
        Label k = new Label(key);
        k.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        valLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        row.getChildren().addAll(k, sp, valLabel);
        return row;
    }

    // ================= REAL-TIME FIRESTORE LISTENER =================
    private void initRealtimeEventListener() {
        if (event == null || event.getEventId() == null) return;

        new Thread(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                DocumentReference docRef = db.collection("events").document(event.getEventId());

                detachListener();

                firestoreListener = docRef.addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    Event updated = snapshot.toObject(Event.class);
                    if (updated != null) {
                        this.event = updated;
                        Platform.runLater(() -> updateUIWithRealtimeData(updated));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUIWithRealtimeData(Event ev) {
        int limit = ev.getGuestLimit() > 0 ? ev.getGuestLimit() : (ev.getGuests() > 0 ? ev.getGuests() : 250);
        int checked = ev.getCheckedInCount();
        int remaining = Math.max(0, limit - checked);

        lblTotalGuests.setText(String.valueOf(limit));
        lblCheckedIn.setText(String.valueOf(checked));
        lblRemaining.setText(String.valueOf(remaining));

        lblRowMaxGuests.setText(String.valueOf(limit));
        double checkPercent = limit > 0 ? ((double) checked / limit) * 100 : 0;
        double remPercent = limit > 0 ? ((double) remaining / limit) * 100 : 0;

        lblRowCheckedIn.setText(checked + " (" + String.format("%.1f", checkPercent) + "%)");
        lblRowRemaining.setText(remaining + " (" + String.format("%.1f", remPercent) + "%)");

        if (ev.getEventDate() != null && !ev.getEventDate().isBlank()) {
            lblCheckInStarted.setText(ev.getEventDate() + " (From Start)");
            lblCheckInEnds.setText(ev.getEventDate() + " (Until End)");
        }

        // ⭐ 1. DUAL MODE HANDLING: Manual Mode मध्ये Download QR कार्ड लपवणे
        if ("QR_ONLY".equalsIgnoreCase(ev.getGuestMode())) {
            qrCard.setVisible(true);
            qrCard.setManaged(true);
            lblCheckInMode.setText("Check-In Mode: Single Event QR Pass");
            btnViewGuestList.setText("Single QR Entry Active (No individual list)");
            btnViewGuestList.setDisable(true);
        } else {
            qrCard.setVisible(false);
            qrCard.setManaged(false);
            lblCheckInMode.setText("Check-In Mode: Individual Guest List");
            btnViewGuestList.setText("👥 View & Manage Guest List (" + (ev.getGuestList() != null ? ev.getGuestList().size() : 0) + ")");
            btnViewGuestList.setDisable(false);
        }

        lblEventCode.setText("Event: " + (ev.getEventName() != null ? ev.getEventName() : ""));
    }

    // ================= IN-SCENE QR DOWNLOAD DIALOG (NO EXTRA STAGE, NO PAYLOAD) =================
    private void handleDownloadQrModal() {
        if (event == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Event Entry QR");
        dialog.setHeaderText(null);
        if (LandingPage.landingPageStage != null) dialog.initOwner(LandingPage.landingPageStage);

        VBox content = new VBox(14);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Event Entry Pass");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        Label sub = new Label("Event: " + event.getEventName());
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        String payload = QRCodeUtil.formatPayload(event.getEventId(), event.getQrToken() != null ? event.getQrToken() : "PASS");
        javafx.scene.image.WritableImage qrImg = QRCodeUtil.generateQrImage(payload, 220, 220);
        javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(qrImg);
        imgView.setStyle("-fx-border-color: #0B4EA2; -fx-border-width: 1px; -fx-padding: 4;");

        Button btnSavePng = new Button("📥 Download PNG Image");
        btnSavePng.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        btnSavePng.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save QR Pass");
            chooser.setInitialFileName((event.getEventName() != null ? event.getEventName().replaceAll("\\s+", "_") : "Event") + "_QR.png");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
            File file = chooser.showSaveDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                try {
                    BufferedImage bImage = new BufferedImage(220, 220, BufferedImage.TYPE_INT_ARGB);
                    for (int x = 0; x < 220; x++) {
                        for (int y = 0; y < 220; y++) {
                            bImage.setRGB(x, y, qrImg.getPixelReader().getArgb(x, y));
                        }
                    }
                    ImageIO.write(bImage, "png", file);
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "QR Code successfully saved to: " + file.getName(), ButtonType.OK);
                    a.initOwner(dialog.getDialogPane().getScene().getWindow());
                    a.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        content.getChildren().addAll(title, sub, imgView, btnSavePng);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // ================= ⭐ 2. VIEW & COMPLETE ADD GUEST MODAL (Name, Mobile, Email, Type) =================
    private void handleViewGuestListModal() {
        if (event == null || event.getGuestList() == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Guest List - " + event.getEventName());
        dialog.setHeaderText(null);
        if (LandingPage.landingPageStage != null) dialog.initOwner(LandingPage.landingPageStage);

        VBox content = new VBox(14);
        content.setPadding(new Insets(18));
        content.setPrefWidth(680);

        HBox topH = new HBox();
        topH.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Registered Guests (" + event.getGuestList().size() + ")");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnAddNew = new Button("+ Add New Guest");
        btnAddNew.setStyle("-fx-background-color: " + ACTIVE_NAV_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
        topH.getChildren().addAll(title, sp, btnAddNew);

        VBox tableBox = new VBox(6);
        ScrollPane tableScroll = new ScrollPane(tableBox);
        tableScroll.setFitToWidth(true);
        tableScroll.setPrefHeight(300);

        Runnable renderRows = () -> {
            tableBox.getChildren().clear();
            title.setText("Registered Guests (" + event.getGuestList().size() + ")");
            for (com.techtrio.model.Guest g : event.getGuestList()) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 12, 8, 12));
                row.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

                VBox gInfo = new VBox(2);
                Label name = new Label(g.getName());
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DARK + ";");
                Label contact = new Label((g.getMobile() != null && !g.getMobile().isBlank() ? g.getMobile() : "No Mobile") + 
                                          (g.getEmail() != null && !g.getEmail().isBlank() ? " • " + g.getEmail() : ""));
                contact.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
                gInfo.getChildren().addAll(name, contact);

                Region rsp = new Region();
                HBox.setHgrow(rsp, Priority.ALWAYS);

                Label typeBadge = new Label(g.getType() != null ? g.getType() : "General");
                typeBadge.setStyle("-fx-background-color: #EAF3FF; -fx-text-fill: #0B4EA2; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");

                Label status = new Label(g.isCheckedIn() ? "✓ Checked In" : "● Not Checked In");
                status.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (g.isCheckedIn() ? "#16A34A" : TEXT_MUTED) + ";");

                row.getChildren().addAll(gInfo, typeBadge, rsp, status);
                tableBox.getChildren().add(row);
            }
        };

        renderRows.run();

        // FULL ADD GUEST FORM DIALOG
        btnAddNew.setOnAction(e -> {
            Dialog<com.techtrio.model.Guest> addDialog = new Dialog<>();
            addDialog.setTitle("Add New Guest");
            addDialog.setHeaderText("Enter guest details to add to this event:");
            addDialog.initOwner(dialog.getDialogPane().getScene().getWindow());

            ButtonType saveBtnType = new ButtonType("Add Guest", ButtonBar.ButtonData.OK_DONE);
            addDialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(16, 20, 10, 10));

            TextField txtName = new TextField(); txtName.setPromptText("Full Name *");
            TextField txtMobile = new TextField(); txtMobile.setPromptText("Mobile Number");
            TextField txtEmail = new TextField(); txtEmail.setPromptText("Email Address");

            ComboBox<String> cbType = new ComboBox<>();
            cbType.getItems().addAll("VIP", "Speaker", "General", "Sponsor", "Family");
            cbType.setValue("General");

            grid.add(new Label("Full Name:"), 0, 0); grid.add(txtName, 1, 0);
            grid.add(new Label("Mobile No:"), 0, 1); grid.add(txtMobile, 1, 1);
            grid.add(new Label("Email Address:"), 0, 2); grid.add(txtEmail, 1, 2);
            grid.add(new Label("Guest Type:"), 0, 3); grid.add(cbType, 1, 3);

            addDialog.getDialogPane().setContent(grid);

            addDialog.setResultConverter(btnType -> {
                if (btnType == saveBtnType) {
                    String name = txtName.getText().trim();
                    if (!name.isEmpty()) {
                        return new com.techtrio.model.Guest(name, txtMobile.getText().trim(), txtEmail.getText().trim(), cbType.getValue());
                    }
                }
                return null;
            });

            addDialog.showAndWait().ifPresent(newGuest -> {
                event.getGuestList().add(newGuest);
                int newLimit = event.getGuestList().size();
                event.setGuestLimit(newLimit);

                // Save to Firestore in real-time
                new Thread(() -> {
                    try {
                        FirestoreClient.getFirestore().collection("events")
                                .document(event.getEventId())
                                .update("guestList", event.getGuestList(), "guestLimit", newLimit);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();

                renderRows.run();
                updateUIWithRealtimeData(event);
            });
        });

        content.getChildren().addAll(topH, tableScroll);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void detachListener() {
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
    }

    private void handleBack() {
        detachListener();
        if (backAction != null) backAction.run();
        else goOverview();
    }

    private void goDashboard() {
        detachListener();
        ActiveCustomerDashboardC dash = new ActiveCustomerDashboardC(uid, idToken);
        LandingPage.landingPageStage.setScene(dash.getactiveCustomerScene(null));
    }

    private void goCreateEvent() {
        detachListener();
        CreateEventC create = new CreateEventC(uid, idToken);
        LandingPage.landingPageStage.setScene(create.getCreateEventScene(this::goDashboard));
    }

    private void goOverview() {
        detachListener();
        MyEventDetails view = new MyEventDetails(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getMyEventDetailsScene(this::handleBack));
    }

    private void goVenue() {
        detachListener();
        Venue view = new Venue(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getVenueScene(this::handleBack));
    }

    private void goBudget() {
        detachListener();
        Budget view = new Budget(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getBudgetScene(this::handleBack));
    }

    private void goPayment() {
        detachListener();
        Payment view = new Payment(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getPaymentScene(this::handleBack));
    }

    private void goRequirement() {
        detachListener();
        Requirement view = new Requirement(uid, idToken, event);
        LandingPage.landingPageStage.setScene(view.getRequirementScene(this::handleBack));
    }

    private void goSettings() {
        detachListener();
        SettingC s = new SettingC();
        LandingPage.landingPageStage.setScene(s.getSettingScene(this::handleBack));
    }
}