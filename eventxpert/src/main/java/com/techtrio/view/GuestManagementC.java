package com.techtrio.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        ReviewPageC.EventData.currentStep = 5;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // 1. SIDEBAR
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(245);
        sidebar.setMinWidth(245);
        sidebar.setMaxWidth(245);
        sidebar.setFillWidth(true);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(24, 20, 20, 22));

        FontIcon logoIcon = new FontIcon("fas-calendar-alt");
        logoIcon.setIconSize(24);
        logoIcon.setIconColor(Color.web("#38BDF8"));

        VBox brandTexts = new VBox(1);
        Label logo = label("EventXpert", 20, FontWeight.BOLD, "white");
        Label logoSub = label("EVENT MANAGEMENT", 9, FontWeight.NORMAL, "#BDE3FF");
        brandTexts.getChildren().addAll(logo, logoSub);
        brandBox.getChildren().addAll(logoIcon, brandTexts);

        Label createTitle = label("Create Event", 18, FontWeight.BOLD, "white");
        Label step = label("Step-by-step setup", 11, FontWeight.NORMAL, "#BDE3FF");
        VBox titleBox = new VBox(2, createTitle, step);
        titleBox.setPadding(new Insets(0, 20, 20, 25));

        VBox menu = new VBox();
        menu.getChildren().addAll(
                sidebarItem("[1]", "Event Details", "fas-info-circle", false),
                sidebarItem("[2]", "Requirements", "fas-list-ul", false),
                sidebarItem("[3]", "Venue & Location", "fas-map-marker-alt", false),
                sidebarItem("[4]", "Budget", "fas-wallet", false),
                sidebarItem("[5]", "Guest", "fas-users", true),
                sidebarItem("[6]", "AI Planning", "fas-magic", false),
                sidebarItem("[7]", "Review", "fas-check-circle", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandBox, titleBox, menu, sideSpace);

        // 2. MAIN CONTENT (TopBar Removed)
        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 45, 20, 45));

        Label stepText = label("STEP 5 OF 7", 10, FontWeight.BOLD, BLUE);
        Label title = label("Guest Management", 26, FontWeight.BOLD, NAVY);
        Label subtitle = label("Choose how you want to manage guest entry for your event.", 12, FontWeight.NORMAL, "#64748B");
        VBox titleBoxPage = new VBox(3, stepText, title, subtitle);

        // Cards Row
        cardManual = createCard();
        cardManual.setPrefWidth(450);
        HBox.setHgrow(cardManual, Priority.ALWAYS);
        cardManual.setCursor(javafx.scene.Cursor.HAND);

        HBox topManual = new HBox();
        topManual.setAlignment(Pos.CENTER_LEFT);

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

        cardQr = createCard();
        cardQr.setPrefWidth(450);
        HBox.setHgrow(cardQr, Priority.ALWAYS);
        cardQr.setCursor(javafx.scene.Cursor.HAND);

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

        // Header Labels in NAVY Color with Bold weight
        TableColumn<GuestRow, String> colName = new TableColumn<>();
        colName.setGraphic(createHeaderLabel("Guest"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);
        styleColumnCell(colName);

        TableColumn<GuestRow, String> colContact = new TableColumn<>();
        colContact.setGraphic(createHeaderLabel("Contact"));
        colContact.setCellValueFactory(cellData -> {
            GuestRow g = cellData.getValue();
            String cText = (g.getEmail() != null && !g.getEmail().isBlank()) ? g.getEmail() : g.getMobile();
            return new SimpleStringProperty(cText);
        });
        colContact.setPrefWidth(220);
        styleColumnCell(colContact);

        TableColumn<GuestRow, String> colType = new TableColumn<>();
        colType.setGraphic(createHeaderLabel("Type"));
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

        TableColumn<GuestRow, String> colStatus = new TableColumn<>();
        colStatus.setGraphic(createHeaderLabel("Status"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);
        styleColumnCell(colStatus);

        TableColumn<GuestRow, Void> colAction = new TableColumn<>();
        colAction.setGraphic(createHeaderLabel("Action"));
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
        content.getChildren().addAll(titleBoxPage, cardsHolder, addGuestsCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

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
            ReviewPageC.EventData.saveDraft(4);
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
            ReviewPageC.EventData.saveDraft(6);

            if (nextAction != null) {
                nextAction.run();
            } else {
                AiPlanningC aiView = new AiPlanningC(uid, idToken);
                LandingPage.landingPageStage.setScene(aiView.getAiPlanningScene());
            }
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox(scrollPane, footer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox fullPage = new HBox(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        root.setCenter(fullPage);

        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private Label createHeaderLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + NAVY + " !important; -fx-font-weight: bold; -fx-font-size: 12px; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        return l;
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

    // ⭐ डायनॅमिक CSV Import: फाईलमधील सर्व पाहुणे आपोआप वाचून टेबलमध्ये लोड होतात
    private void handleImportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select CSV File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        File file = chooser.showOpenDialog(LandingPage.landingPageStage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                int count = 0;

                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    // जर पहिली ओळ Header असेल (जसे Name, Mobile, Email) तर ती वगळा
                    if (isFirstLine) {
                        isFirstLine = false;
                        String lower = line.toLowerCase();
                        if (lower.contains("name") || lower.contains("email") || lower.contains("mobile") || lower.contains("contact")) {
                            continue;
                        }
                    }

                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String name = parts[0].trim();
                        String col1 = parts.length > 1 ? parts[1].trim() : "";
                        String col2 = parts.length > 2 ? parts[2].trim() : "";
                        String type = parts.length > 3 ? parts[3].trim() : "General";

                        String mobile = "";
                        String email = "";

                        // स्मार्ट चेकिंग: ईमेल आणि मोबाईल मागे-पुढे असले तरी ओळखणे
                        if (col1.contains("@")) {
                            email = col1;
                            mobile = col2;
                        } else {
                            mobile = col1;
                            email = col2;
                        }

                        if (type.isBlank()) type = "General";

                        if (!name.isEmpty()) {
                            guestMasterList.add(new GuestRow(name, mobile, email, type));
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Total " + count + " guests successfully imported from CSV!");
                    alert.setHeaderText("CSV Import Complete");
                    alert.showAndWait();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not read CSV file: " + ex.getMessage());
                alert.showAndWait();
            }
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

    private HBox sidebarItem(String stepNum, String text, String iconLiteral, boolean active) {
        HBox item = new HBox(10);
        item.setPrefHeight(42);
        item.setMinHeight(42);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(0, 16, 0, 22));

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(13);
        icon.setIconColor(active ? Color.web(NAVY) : Color.web("#B7C4D8"));

        Label textLbl = label(stepNum + "  " + text, 12, active ? FontWeight.BOLD : FontWeight.NORMAL, active ? NAVY : "#B7C4D8");

        if (active) {
            item.setStyle("-fx-background-color: #E0F2FE; -fx-background-radius: 7px;");
        } else {
            item.setStyle("-fx-background-color: transparent;");
        }

        item.getChildren().addAll(icon, textLbl);
        return item;
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", weight, size));
        l.setStyle("-fx-text-fill: " + color + " !important;");
        return l;
    }
}