package com.techtrio.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.techtrio.session.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

public class GuestManagementC {

    private Scene scene;
    private String uid;
    private String idToken;

    // ReviewPageC सोबत 100% मॅच असणारे High Contrast Theme Colors
    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String MAIN_BLUE = "#002A60";
    private static final String BG = "#F4F7FC";
    private static final String BORDER = "#CBD5E1";
    private static final String DARK_TEXT = "#0F172A";
    private static final String MUTED_TEXT = "#334155";

    // Mode state
    private String selectedMode = "MANUAL"; // "MANUAL" or "QR_ONLY"

    // UI Dynamic Controls
    private VBox cardManual;
    private VBox cardQr;
    private Button btnManualSelect;
    private Button btnQrSelect;
    private StackPane badgeCheckManual;
    private VBox addGuestsCard;

    // Inputs
    private TextField txtGuestName;
    private TextField txtMobile;
    private TextField txtEmail;
    private ComboBox<String> cbGuestType;
    private TableView<GuestRow> table;
    private ObservableList<GuestRow> guestMasterList = FXCollections.observableArrayList();

    // Guest Row Model
    public static class GuestRow {
        private String name;
        private String mobile;
        private String email;
        private String type;
        private String status;

        public GuestRow(String name, String mobile, String email, String type) {
            this.name = name;
            this.mobile = mobile;
            this.email = email;
            this.type = (type == null || type.isBlank()) ? "General" : type;
            this.status = "Pending QR";
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public GuestManagementC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
    }

    public GuestManagementC() {
        this.uid = UserSession.getUid();
        this.idToken = UserSession.getIdToken();
    }

    public Scene getGuestManagementScene() {
        return getGuestManagementScene(null, null);
    }

    public Scene getGuestManagementScene(Runnable backAction, Runnable nextAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // =========================================================
        // 1. SIDEBAR (Exact ReviewPageC Style)
        // =========================================================
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "#FFFFFF");
        Label logoSub = label("Plan. Organize. Celebrate.", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label createTitle = label("Create Event", 20, FontWeight.BOLD, "#FFFFFF");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("ⓘ", "Event Details", false),
                sidebarItem("▣", "Requirements", false),
                sidebarItem("⌖", "Location", false),
                sidebarItem("▣", "Budget", false),
                sidebarItem("👥", "Guest Management", true), // ACTIVE STEP
                sidebarItem("✦", "AI Planning", false),
                sidebarItem("✓", "Review", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brand, titleBox, menu, sideSpace);

        // =========================================================
        // 2. TOP BAR
        // =========================================================
        HBox topBar = new HBox(12);
        topBar.setPrefHeight(60);
        topBar.setMinHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E2E8F0;-fx-border-width:0 0 1 0;");

        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);

        Label notification = label("♧", 20, FontWeight.NORMAL, MAIN_BLUE);
        Label help = label("?", 13, FontWeight.BOLD, MAIN_BLUE);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + MAIN_BLUE + ";-fx-border-radius:50;-fx-background-radius:50;-fx-text-fill:" + MAIN_BLUE + ";");

        topBar.getChildren().addAll(topSpace, notification, help);

        // =========================================================
        // 3. SCROLLABLE MAIN CONTENT
        // =========================================================
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox page = new VBox(18);
        page.setPadding(new Insets(18, 35, 20, 35));

        // Title Header
        Label title = label("Guest Management", 24, FontWeight.BOLD, MAIN_BLUE);
        Label subtitle = label("Choose how you want to manage guest entry for your event.", 13, FontWeight.NORMAL, MUTED_TEXT);
        Label subInfo = label("ⓘ You can manage guests individually or use one event QR code for quick entry.", 11, FontWeight.NORMAL, MUTED_TEXT);
        VBox titleBoxPage = new VBox(3, title, subtitle, subInfo);

        // Event Summary Card
        HBox summaryCard = new HBox(20);
        summaryCard.setAlignment(Pos.CENTER_LEFT);
        summaryCard.setPadding(new Insets(14, 20, 14, 20));
        summaryCard.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");

        String evName = (ReviewPageC.EventData.eventName != null && !ReviewPageC.EventData.eventName.isBlank())
                ? ReviewPageC.EventData.eventName : "TechVision Annual Summit";
        int guestsCount = (ReviewPageC.EventData.guests > 0) ? ReviewPageC.EventData.guests : 250;
        String evDate = (ReviewPageC.EventData.eventDate != null && !ReviewPageC.EventData.eventDate.isBlank())
                ? ReviewPageC.EventData.eventDate : "18 May 2024";
        String evLocation = (!ReviewPageC.EventData.city.isBlank() ? ReviewPageC.EventData.city : "Pune") + 
                            (!ReviewPageC.EventData.area.isBlank() ? ", " + ReviewPageC.EventData.area : ", Maharashtra");

        summaryCard.getChildren().addAll(
                summaryDetail("Event", evName, 220),
                new Separator(javafx.geometry.Orientation.VERTICAL),
                summaryDetail("Expected Guests", String.valueOf(guestsCount), 130),
                new Separator(javafx.geometry.Orientation.VERTICAL),
                summaryDetail("Date", evDate, 130),
                new Separator(javafx.geometry.Orientation.VERTICAL),
                summaryDetail("Location", "📍 " + evLocation, 200)
        );

        // Helper Information Banner
        HBox helperBanner = new HBox(12);
        helperBanner.setAlignment(Pos.CENTER_LEFT);
        helperBanner.setPadding(new Insets(12, 16, 12, 16));
        helperBanner.setStyle("-fx-background-color:#EBF2FE;-fx-border-color:#D3E2FC;-fx-border-radius:8;-fx-background-radius:8;");

        StackPane qCircle = new StackPane();
        Circle c = new Circle(10, Color.web("#3B82F6"));
        Label qMark = label("?", 11, FontWeight.BOLD, "#FFFFFF");
        qCircle.getChildren().addAll(c, qMark);

        VBox helperText = new VBox(2,
                label("How guest entry works", 12, FontWeight.BOLD, MAIN_BLUE),
                label("Decide whether you need strict control over who enters (Manual List) or if you prefer a seamless, bulk entry approach (Event QR).", 11, FontWeight.NORMAL, "#3B5998")
        );
        helperBanner.getChildren().addAll(qCircle, helperText);

        Label chooseTitle = label("How would you like to manage your guests?", 15, FontWeight.BOLD, DARK_TEXT);

        // 2 Selection Cards
        cardManual = createCard();
        cardManual.setPrefWidth(450);
        HBox.setHgrow(cardManual, Priority.ALWAYS);
        cardManual.setCursor(javafx.scene.Cursor.HAND);

        HBox topManual = new HBox();
        topManual.setAlignment(Pos.CENTER_LEFT);
        Label iconManual = label("👥", 16, FontWeight.NORMAL, MAIN_BLUE);
        iconManual.setStyle("-fx-background-color:#E5F0FF;-fx-padding:8 10;-fx-background-radius:20;");

        Region spaceManual = new Region();
        HBox.setHgrow(spaceManual, Priority.ALWAYS);

        badgeCheckManual = new StackPane();
        Circle checkBg = new Circle(11, Color.web(MAIN_BLUE));
        Label checkIco = label("✓", 11, FontWeight.BOLD, "#FFFFFF");
        badgeCheckManual.getChildren().addAll(checkBg, checkIco);
        topManual.getChildren().addAll(iconManual, spaceManual, badgeCheckManual);

        btnManualSelect = new Button("Selected");
        btnManualSelect.setMaxWidth(Double.MAX_VALUE);
        btnManualSelect.setPrefHeight(38);
        btnManualSelect.setStyle("-fx-background-color:" + MAIN_BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;");

        cardManual.getChildren().addAll(
                topManual,
                label("Manual Guest List", 15, FontWeight.BOLD, DARK_TEXT),
                label("Add and manage individual guests.", 11, FontWeight.NORMAL, MUTED_TEXT),
                new VBox(4,
                        label("✔  Add name/contact", 11, FontWeight.NORMAL, DARK_TEXT),
                        label("✔  Maintain list", 11, FontWeight.NORMAL, DARK_TEXT),
                        label("✔  Track check-in", 11, FontWeight.NORMAL, DARK_TEXT),
                        label("✔  Individual QR codes", 11, FontWeight.NORMAL, DARK_TEXT)
                ),
                btnManualSelect
        );

        // Single Event QR Card
        cardQr = createCard();
        cardQr.setPrefWidth(450);
        HBox.setHgrow(cardQr, Priority.ALWAYS);
        cardQr.setCursor(javafx.scene.Cursor.HAND);

        Label iconQr = label("▦", 16, FontWeight.NORMAL, MUTED_TEXT);
        iconQr.setStyle("-fx-background-color:#F1F5F9;-fx-padding:8 10;-fx-background-radius:8;");

        HBox maxEntriesRow = new HBox();
        maxEntriesRow.setAlignment(Pos.CENTER_LEFT);
        maxEntriesRow.setPadding(new Insets(6, 10, 6, 10));
        maxEntriesRow.setStyle("-fx-background-color:#F8FAFC;-fx-background-radius:6;");
        Label lblMax = label("Maximum Entries:", 11, FontWeight.NORMAL, MUTED_TEXT);
        Region maxSpace = new Region();
        HBox.setHgrow(maxSpace, Priority.ALWAYS);
        Label lblMaxCount = label(guestsCount + " Guests", 11, FontWeight.BOLD, DARK_TEXT);
        maxEntriesRow.getChildren().addAll(lblMax, maxSpace, lblMaxCount);

        btnQrSelect = new Button("Choose Event QR");
        btnQrSelect.setMaxWidth(Double.MAX_VALUE);
        btnQrSelect.setPrefHeight(38);
        btnQrSelect.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + DARK_TEXT + ";-fx-font-weight:bold;-fx-background-radius:6;-fx-border-radius:6;");

        cardQr.getChildren().addAll(
                iconQr,
                label("Single Event QR", 15, FontWeight.BOLD, DARK_TEXT),
                label("Use one QR code for your entire event.", 11, FontWeight.NORMAL, MUTED_TEXT),
                new VBox(4,
                        label("✔  One QR for event", 11, FontWeight.NORMAL, DARK_TEXT),
                        label("✔  Fast check-in", 11, FontWeight.NORMAL, DARK_TEXT),
                        label("✔  No individual registration", 11, FontWeight.NORMAL, DARK_TEXT),
                        label("✔  Automatic stop at limit", 11, FontWeight.NORMAL, DARK_TEXT)
                ),
                maxEntriesRow,
                btnQrSelect
        );

        HBox cardsHolder = new HBox(18, cardManual, cardQr);

        // Click actions
        cardManual.setOnMouseClicked(e -> setModeManual());
        btnManualSelect.setOnAction(e -> setModeManual());
        cardQr.setOnMouseClicked(e -> setModeQr());
        btnQrSelect.setOnAction(e -> setModeQr());

        // =========================================================
        // 4. ADD GUESTS CARD (Image 2)
        // =========================================================
        addGuestsCard = createCard();

        HBox addHeader = new HBox();
        addHeader.setAlignment(Pos.CENTER_LEFT);
        Label addTitle = label("Add Guests", 16, FontWeight.BOLD, DARK_TEXT);
        Region addSpace = new Region();
        HBox.setHgrow(addSpace, Priority.ALWAYS);
        Button btnImportCsv = new Button("📄 Import CSV");
        btnImportCsv.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + DARK_TEXT + ";-fx-font-size:11px;-fx-padding:5 12;-fx-background-radius:6;-fx-border-radius:6;");
        btnImportCsv.setOnAction(e -> handleImportCsv());
        addHeader.getChildren().addAll(addTitle, addSpace, btnImportCsv);

        // Inputs
        txtGuestName = new TextField();
        txtGuestName.setPromptText("e.g. John Doe");
        txtGuestName.setPrefHeight(36);
        styleInput(txtGuestName);

        txtMobile = new TextField();
        txtMobile.setPromptText("+91");
        txtMobile.setPrefHeight(36);
        txtMobile.setPrefWidth(160);
        styleInput(txtMobile);

        txtEmail = new TextField();
        txtEmail.setPromptText("john@example.com");
        txtEmail.setPrefHeight(36);
        styleInput(txtEmail);

        cbGuestType = new ComboBox<>(FXCollections.observableArrayList("VIP", "Speaker", "General", "Sponsor", "Family"));
        cbGuestType.setValue("VIP");
        cbGuestType.setPrefHeight(36);
        cbGuestType.setPrefWidth(130);
        cbGuestType.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;");

        Button btnAdd = new Button("+");
        btnAdd.setPrefSize(42, 36);
        btnAdd.setStyle("-fx-background-color:" + MAIN_BLUE + ";-fx-text-fill:white;-fx-font-size:18px;-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;");
        btnAdd.setOnAction(e -> handleAddGuest());

        HBox formRow = new HBox(10,
                formField("Guest Name", txtGuestName, true),
                formField("Mobile Number", txtMobile, false),
                formField("Email Address", txtEmail, true),
                formField("Guest Type", cbGuestType, false),
                new VBox(22, new Region(), btnAdd)
        );

        // Table
        table = new TableView<>();
        table.setPrefHeight(220);
        table.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;");

        TableColumn<GuestRow, String> colName = new TableColumn<>("Guest");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(220);

        TableColumn<GuestRow, String> colContact = new TableColumn<>("Contact");
        colContact.setCellValueFactory(cellData -> {
            GuestRow g = cellData.getValue();
            String cText = (g.getEmail() != null && !g.getEmail().isBlank()) ? g.getEmail() : g.getMobile();
            return new SimpleStringProperty(cText);
        });
        colContact.setPrefWidth(240);

        TableColumn<GuestRow, String> colType = new TableColumn<>("Type");
        colType.setPrefWidth(130);
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    GuestRow g = getTableRow().getItem();
                    Label badge = new Label(g.getType());
                    if ("Speaker".equalsIgnoreCase(g.getType())) {
                        badge.setStyle("-fx-background-color:#E0F2FE;-fx-text-fill:#0369A1;-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;-fx-font-weight:bold;");
                    } else {
                        badge.setStyle("-fx-background-color:#F1F5F9;-fx-text-fill:#475569;-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;-fx-font-weight:bold;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<GuestRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(140);

        TableColumn<GuestRow, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(90);
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnMenu = new Button("⋮");
            private final ContextMenu menu = new ContextMenu();
            private final MenuItem del = new MenuItem("Delete");
            {
                btnMenu.setStyle("-fx-background-color:transparent;-fx-font-size:14px;-fx-font-weight:bold;-fx-cursor:hand;");
                menu.getItems().add(del);
                del.setOnAction(e -> {
                    GuestRow g = getTableView().getItems().get(getIndex());
                    guestMasterList.remove(g);
                });
                btnMenu.setOnAction(e -> menu.show(btnMenu, javafx.geometry.Side.BOTTOM, 0, 0));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnMenu);
            }
        });

        table.getColumns().addAll(colName, colContact, colType, colStatus, colActions);
        table.setItems(guestMasterList);

        // Load existing guests if available or seed initial
        if (ReviewPageC.EventData.guestList != null && !ReviewPageC.EventData.guestList.isEmpty()) {
            guestMasterList.setAll(ReviewPageC.EventData.guestList);
        } else {
            guestMasterList.addAll(
                    new GuestRow("Sarah Jenkins", "+91 9876543210", "sarah.j@email.com", "Speaker"),
                    new GuestRow("Michael Chen", "+91 98765 43210", "", "VIP")
            );
        }

        addGuestsCard.getChildren().addAll(addHeader, formRow, table);

        page.getChildren().addAll(titleBoxPage, summaryCard, helperBanner, chooseTitle, cardsHolder, addGuestsCard);
        scrollPane.setContent(page);

        // =========================================================
        // 5. FOOTER (Exact Navigation Flow)
        // =========================================================
        HBox footer = new HBox(14);
        footer.setPrefHeight(65);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E2E8F0;-fx-border-width:1 0 0 0;");

        Button back = new Button("← Back to Budget");
        back.setPrefSize(145, 40);
        back.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        back.setStyle("-fx-background-color:white;-fx-border-color:" + BLUE + ";-fx-border-radius:6;-fx-background-radius:6;-fx-text-fill:" + BLUE + ";-fx-cursor:hand;");
        back.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            } else {
                BudgetC budgetView = new BudgetC(uid, idToken);
                LandingPage.landingPageStage.setScene(budgetView.getBudgetScene());
            }
        });

        Button saveDraft = new Button("Save Draft");
        saveDraft.setStyle("-fx-background-color:transparent;-fx-text-fill:" + MUTED_TEXT + ";-fx-font-weight:bold;-fx-cursor:hand;");

        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);

        Button next = new Button("Next: AI Planning →");
        next.setPrefSize(165, 40);
        next.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        next.setStyle("-fx-background-color:" + MAIN_BLUE + ";-fx-text-fill:white;-fx-background-radius:6;-fx-cursor:hand;");
        next.setOnAction(e -> {
            // Save to ReviewPageC.EventData
            ReviewPageC.EventData.guestMode = selectedMode;
            ReviewPageC.EventData.guestList = new ArrayList<>(guestMasterList);

            if (nextAction != null) {
                nextAction.run();
            } else {
                AiPlanningC aiView = new AiPlanningC(uid, idToken);
                LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
            }
        });

        footer.getChildren().addAll(back, saveDraft, fSpacer, next);

        VBox rightSide = new VBox(topBar, scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1365, 850);
        return scene;
    }

    // =============================================================
    // HELPER UI METHODS
    // =============================================================

    private void setModeManual() {
        selectedMode = "MANUAL";
        cardManual.setStyle("-fx-background-color:white;-fx-border-color:" + MAIN_BLUE + ";-fx-border-width:2;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:18;");
        btnManualSelect.setText("Selected");
        btnManualSelect.setStyle("-fx-background-color:" + MAIN_BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;");
        badgeCheckManual.setVisible(true);

        cardQr.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:18;");
        btnQrSelect.setText("Choose Event QR");
        btnQrSelect.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + DARK_TEXT + ";-fx-font-weight:bold;-fx-background-radius:6;-fx-border-radius:6;");

        addGuestsCard.setVisible(true);
        addGuestsCard.setManaged(true);
    }

    private void setModeQr() {
        selectedMode = "QR_ONLY";
        cardQr.setStyle("-fx-background-color:white;-fx-border-color:" + MAIN_BLUE + ";-fx-border-width:2;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:18;");
        btnQrSelect.setText("Selected");
        btnQrSelect.setStyle("-fx-background-color:" + MAIN_BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;");

        cardManual.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:18;");
        btnManualSelect.setText("Choose Manual List");
        btnManualSelect.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + DARK_TEXT + ";-fx-font-weight:bold;-fx-background-radius:6;-fx-border-radius:6;");
        badgeCheckManual.setVisible(false);

        // Hide table in QR mode
        addGuestsCard.setVisible(false);
        addGuestsCard.setManaged(false);
    }

    private void handleAddGuest() {
        String name = txtGuestName.getText().trim();
        String mobile = txtMobile.getText().trim();
        String email = txtEmail.getText().trim();
        String type = cbGuestType.getValue();

        if (name.isEmpty() || (mobile.isEmpty() && email.isEmpty())) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Please enter Guest Name and at least Mobile Number or Email.");
            a.showAndWait();
            return;
        }

        guestMasterList.add(new GuestRow(name, mobile, email, type));
        txtGuestName.clear();
        txtMobile.clear();
        txtEmail.clear();
    }

    private void handleImportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select CSV File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        File file = chooser.showOpenDialog(LandingPage.landingPageStage);
        if (file != null) {
            guestMasterList.add(new GuestRow("Imported Guest", "+91 9988776655", "imported@event.com", "General"));
        }
    }

    private VBox summaryDetail(String title, String val, double width) {
        VBox v = new VBox(2, label(title, 10, FontWeight.NORMAL, MUTED_TEXT), label(val, 13, FontWeight.BOLD, DARK_TEXT));
        v.setPrefWidth(width);
        return v;
    }

    private VBox formField(String title, Control field, boolean grow) {
        VBox v = new VBox(4, label(title, 11, FontWeight.BOLD, MUTED_TEXT), field);
        if (grow) HBox.setHgrow(v, Priority.ALWAYS);
        return v;
    }

    private void styleInput(TextField tf) {
        tf.setStyle("-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:6 10;");
    }

    private VBox createCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:10;-fx-background-radius:10;");
        return card;
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 16, FontWeight.NORMAL, "white"), label(text, 13, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + ";");
        l.setTextFill(Color.web(color));
        return l;
    }
}