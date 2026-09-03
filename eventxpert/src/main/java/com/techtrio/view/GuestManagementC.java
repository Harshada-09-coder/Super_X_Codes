package com.techtrio.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

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

    // High Contrast Force NAVY Colors
    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#CBD5E1";

    private String selectedMode = "MANUAL";

    private VBox cardManual;
    private VBox cardQr;
    private Button btnManualSelect;
    private Button btnQrSelect;
    private StackPane badgeCheckManual;
    private VBox addGuestsCard;

    private TextField txtGuestName;
    private TextField txtMobile;
    private TextField txtEmail;
    private ComboBox<String> cbGuestType;
    private TableView<GuestRow> table;
    private ObservableList<GuestRow> guestMasterList = FXCollections.observableArrayList();

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

        // 1. SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = label("EventXpert", 22, FontWeight.BOLD, "#FFFFFF");
        Label logoSub = label("EVENT MANAGEMENT", 10, FontWeight.NORMAL, "#BDE3FF");
        VBox brand = new VBox(3, logo, logoSub);
        brand.setPadding(new Insets(24, 20, 28, 25));

        Label createTitle = label("Create Event", 20, FontWeight.BOLD, "#FFFFFF");
        Label step = label("Step-by-step setup", 12, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", false),
                sidebarItem("[2]", "Requirements", false),
                sidebarItem("[3]", "Venue", false),
                sidebarItem("[4]", "Budget", false),
                sidebarItem("[5]", "Guest", true),
                sidebarItem("[6]", "AI Planning", false),
                sidebarItem("[7]", "Review", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brand, titleBox, menu, sideSpace);

        // 2. TOP BAR
        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(0, 30, 0, 30));
        topBar.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:0 0 1 0;");

        Label topLogo = label("EventXpert", 14, FontWeight.BOLD, NAVY);
        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        Label notification = label("Notifications", 11, FontWeight.BOLD, NAVY);
        Label help = label("?", 13, FontWeight.BOLD, NAVY);
        help.setMinSize(28, 28);
        help.setAlignment(Pos.CENTER);
        help.setStyle("-fx-border-color:" + NAVY + ";-fx-border-radius:50;-fx-background-radius:50;-fx-text-fill:" + NAVY + ";");

        topBar.getChildren().addAll(topLogo, topSpace, notification, help);

        // 3. SCROLLABLE BODY
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox page = new VBox(16);
        page.setPadding(new Insets(20, 35, 20, 35));

        Label stepText = label("STEP 5 OF 7", 11, FontWeight.BOLD, BLUE);
        Label title = label("Guest Management", 26, FontWeight.BOLD, NAVY);
        Label subtitle = label("Choose how you want to manage guest entry for your event.", 13, FontWeight.BOLD, NAVY);
        VBox titleBoxPage = new VBox(4, stepText, title, subtitle);

        // CARD 1: MANUAL GUEST LIST (Ikonli FontIcon)
        cardManual = createCard();
        cardManual.setPrefWidth(450);
        HBox.setHgrow(cardManual, Priority.ALWAYS);
        cardManual.setCursor(javafx.scene.Cursor.HAND);

        HBox topManual = new HBox();
        topManual.setAlignment(Pos.CENTER_LEFT);

        // Ikonli Users Icon
        StackPane iconCircleManual = new StackPane();
        Circle cManual = new Circle(18, Color.web("#E0EDFF"));
        FontIcon iconUsers = new FontIcon("fas-users");
        iconUsers.setIconSize(18);
        iconUsers.setIconColor(Color.web(BLUE));
        iconCircleManual.getChildren().addAll(cManual, iconUsers);

        Region spaceManual = new Region();
        HBox.setHgrow(spaceManual, Priority.ALWAYS);

        badgeCheckManual = new StackPane();
        Circle checkBg = new Circle(10, Color.web(BLUE));
        Label checkIco = label("✓", 10, FontWeight.BOLD, "#FFFFFF");
        badgeCheckManual.getChildren().addAll(checkBg, checkIco);
        topManual.getChildren().addAll(iconCircleManual, spaceManual, badgeCheckManual);

        btnManualSelect = new Button("Selected");
        btnManualSelect.setMaxWidth(Double.MAX_VALUE);
        btnManualSelect.setPrefHeight(36);
        btnManualSelect.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;");

        cardManual.getChildren().addAll(
                topManual,
                label("Manual Guest List", 16, FontWeight.BOLD, NAVY),
                label("Add and manage individual guests.", 12, FontWeight.BOLD, NAVY),
                new VBox(4,
                        label("✔  Add name/contact", 12, FontWeight.BOLD, NAVY),
                        label("✔  Maintain list", 12, FontWeight.BOLD, NAVY),
                        label("✔  Track check-in", 12, FontWeight.BOLD, NAVY)
                ),
                btnManualSelect
        );

        // CARD 2: SINGLE EVENT QR (Ikonli FontIcon)
        cardQr = createCard();
        cardQr.setPrefWidth(450);
        HBox.setHgrow(cardQr, Priority.ALWAYS);
        cardQr.setCursor(javafx.scene.Cursor.HAND);

        // Ikonli QR Code Icon
        StackPane iconCircleQr = new StackPane();
        Circle cQr = new Circle(18, Color.web("#F1F5F9"));
        FontIcon iconQr = new FontIcon("fas-qrcode");
        iconQr.setIconSize(18);
        iconQr.setIconColor(Color.web(NAVY));
        iconCircleQr.getChildren().addAll(cQr, iconQr);

        btnQrSelect = new Button("Choose Event QR");
        btnQrSelect.setMaxWidth(Double.MAX_VALUE);
        btnQrSelect.setPrefHeight(36);
        btnQrSelect.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-background-radius:6;-fx-border-radius:6;");

        cardQr.getChildren().addAll(
                iconCircleQr,
                label("Single Event QR", 16, FontWeight.BOLD, NAVY),
                label("Use one QR code for your entire event.", 12, FontWeight.BOLD, NAVY),
                new VBox(4,
                        label("✔  One QR for event", 12, FontWeight.BOLD, NAVY),
                        label("✔  Fast check-in", 12, FontWeight.BOLD, NAVY),
                        label("✔  Automatic limit tracking", 12, FontWeight.BOLD, NAVY)
                ),
                btnQrSelect
        );

        HBox cardsHolder = new HBox(16, cardManual, cardQr);

        cardManual.setOnMouseClicked(e -> setModeManual());
        btnManualSelect.setOnAction(e -> setModeManual());
        cardQr.setOnMouseClicked(e -> setModeQr());
        btnQrSelect.setOnAction(e -> setModeQr());

        // ADD GUESTS TABLE CARD
        addGuestsCard = createCard();

        HBox addHeader = new HBox(8);
        addHeader.setAlignment(Pos.CENTER_LEFT);
        Label addTitle = label("Add Guests", 16, FontWeight.BOLD, NAVY);
        Region addSpace = new Region();
        HBox.setHgrow(addSpace, Priority.ALWAYS);

        Button btnImportCsv = new Button(" Import CSV");
        FontIcon csvIcon = new FontIcon("fas-file-csv");
        csvIcon.setIconSize(13);
        csvIcon.setIconColor(Color.web(NAVY));
        btnImportCsv.setGraphic(csvIcon);
        btnImportCsv.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + NAVY + ";-fx-font-size:11px;-fx-font-weight:bold;-fx-padding:5 12;-fx-background-radius:6;-fx-border-radius:6;-fx-cursor:hand;");
        btnImportCsv.setOnAction(e -> handleImportCsv());
        addHeader.getChildren().addAll(addTitle, addSpace, btnImportCsv);

        txtGuestName = new TextField();
        txtGuestName.setPromptText("e.g. John Doe");
        styleInput(txtGuestName);

        txtMobile = new TextField();
        txtMobile.setPromptText("+91");
        txtMobile.setPrefWidth(140);
        styleInput(txtMobile);

        txtEmail = new TextField();
        txtEmail.setPromptText("john@example.com");
        styleInput(txtEmail);

        cbGuestType = new ComboBox<>(FXCollections.observableArrayList("VIP", "Speaker", "General", "Sponsor", "Family"));
        cbGuestType.setValue("VIP");
        cbGuestType.setPrefHeight(34);
        cbGuestType.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-weight:bold;-fx-text-fill:" + NAVY + ";");

        Button btnAdd = new Button("+");
        btnAdd.setPrefSize(40, 34);
        btnAdd.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-size:16px;-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;");
        btnAdd.setOnAction(e -> handleAddGuest());

        HBox formRow = new HBox(10,
                formField("Guest Name", txtGuestName, true),
                formField("Mobile Number", txtMobile, false),
                formField("Email Address", txtEmail, true),
                formField("Guest Type", cbGuestType, false),
                new VBox(22, new Region(), btnAdd)
        );

        table = new TableView<>();
        table.setPrefHeight(200);
        table.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;");

        TableColumn<GuestRow, String> colName = new TableColumn<>("Guest");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);
        styleColumnCell(colName);

        TableColumn<GuestRow, String> colContact = new TableColumn<>("Contact");
        colContact.setCellValueFactory(cellData -> {
            GuestRow g = cellData.getValue();
            String cText = (g.getEmail() != null && !g.getEmail().isBlank()) ? g.getEmail() : g.getMobile();
            return new SimpleStringProperty(cText);
        });
        colContact.setPrefWidth(220);
        styleColumnCell(colContact);

        TableColumn<GuestRow, String> colType = new TableColumn<>("Type");
        colType.setPrefWidth(110);
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    GuestRow g = getTableRow().getItem();
                    Label badge = new Label(g.getType());
                    badge.setStyle("-fx-background-color:#E0F2FE;-fx-text-fill:" + BLUE + ";-fx-padding:3 10;-fx-background-radius:10;-fx-font-size:11px;-fx-font-weight:bold;");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<GuestRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);
        styleColumnCell(colStatus);

        // Real-Time Delete Action Column (with Ikonli Trash Icon)
        TableColumn<GuestRow, Void> colAction = new TableColumn<>("Action");
        colAction.setPrefWidth(100);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button(" Delete");
            {
                FontIcon trashIcon = new FontIcon("fas-trash-alt");
                trashIcon.setIconSize(12);
                trashIcon.setIconColor(Color.web("#DC2626"));
                btnDelete.setGraphic(trashIcon);
                btnDelete.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#DC2626;-fx-font-weight:bold;-fx-font-size:11px;-fx-background-radius:6;-fx-cursor:hand;");
                btnDelete.setOnAction(e -> {
                    GuestRow g = getTableView().getItems().get(getIndex());
                    guestMasterList.remove(g);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        table.getColumns().addAll(colName, colContact, colType, colStatus, colAction);
        table.setItems(guestMasterList);

        if (ReviewPageC.EventData.guestList != null && !ReviewPageC.EventData.guestList.isEmpty()) {
            guestMasterList.setAll(ReviewPageC.EventData.guestList);
        }

        addGuestsCard.getChildren().addAll(addHeader, formRow, table);
        page.getChildren().addAll(titleBoxPage, cardsHolder, addGuestsCard);
        scrollPane.setContent(page);

        // FOOTER
        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, NAVY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setPrefSize(82, 38);
        next.setPrefSize(82, 38);

        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-size:12px;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            } else {
                BudgetC budgetView = new BudgetC(uid, idToken);
                LandingPage.landingPageStage.setScene(budgetView.getBudgetScene());
            }
        });

        next.setOnAction(e -> {
            ReviewPageC.EventData.guestMode = selectedMode;
            ReviewPageC.EventData.guestList = new ArrayList<>(guestMasterList);

            if (nextAction != null) {
                nextAction.run();
            } else {
                AiPlanningC aiView = new AiPlanningC(uid, idToken);
                LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
            }
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox(topBar, scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private void styleColumnCell(TableColumn<GuestRow, String> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + NAVY + " !important; -fx-font-weight: bold; -fx-alignment: CENTER-LEFT;");
                }
            }
        });
    }

    private void setModeManual() {
        selectedMode = "MANUAL";
        cardManual.setStyle("-fx-background-color:white;-fx-border-color:" + BLUE + ";-fx-border-width:2;-fx-border-radius:9;-fx-background-radius:9;-fx-padding:16;");
        btnManualSelect.setText("Selected");
        btnManualSelect.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;");
        badgeCheckManual.setVisible(true);

        cardQr.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:9;-fx-background-radius:9;-fx-padding:16;");
        btnQrSelect.setText("Choose Event QR");
        btnQrSelect.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-background-radius:6;-fx-border-radius:6;");

        addGuestsCard.setVisible(true);
        addGuestsCard.setManaged(true);
    }

    private void setModeQr() {
        selectedMode = "QR_ONLY";
        cardQr.setStyle("-fx-background-color:white;-fx-border-color:" + BLUE + ";-fx-border-width:2;-fx-border-radius:9;-fx-background-radius:9;-fx-padding:16;");
        btnQrSelect.setText("Selected");
        btnQrSelect.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;");

        cardManual.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:9;-fx-background-radius:9;-fx-padding:16;");
        btnManualSelect.setText("Choose Manual List");
        btnManualSelect.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-text-fill:" + NAVY + ";-fx-font-weight:bold;-fx-background-radius:6;-fx-border-radius:6;");
        badgeCheckManual.setVisible(false);

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

    private VBox formField(String title, Control field, boolean grow) {
        VBox v = new VBox(4, label(title, 11, FontWeight.BOLD, NAVY), field);
        if (grow) HBox.setHgrow(v, Priority.ALWAYS);
        return v;
    }

    private void styleInput(TextField tf) {
        tf.setPrefHeight(34);
        tf.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:6 10;-fx-font-size:11px;-fx-text-fill:" + NAVY + " !important;-fx-font-weight:bold;");
    }

    private VBox createCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:9;-fx-background-radius:9;");
        return card;
    }

    private HBox sidebarItem(String icon, String text, boolean active) {
        HBox item = new HBox(12);
        item.setPrefHeight(44);
        item.setMinHeight(44);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 18, 0, 25));
        item.setStyle(active ? "-fx-background-color:" + BLUE + ";-fx-border-color:#9DD8FF;-fx-border-width:0 0 0 3;" : "-fx-background-color:transparent;");
        item.getChildren().addAll(label(icon, 13, FontWeight.NORMAL, "white"), label(text, 13, FontWeight.BOLD, active ? "white" : "#B7C4D8"));
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setTextFill(Color.web(color));
        l.setStyle("-fx-text-fill: " + color + " !important; -fx-opacity: 1.0;");
        return l;
    }
}