package com.techtrio.view;

import java.util.List;
import java.util.Optional;
import com.techtrio.model.ConflictResult;

import com.techtrio.context.AdminContext;
import com.techtrio.controller.AdminController;
import com.techtrio.model.CustomerProfile;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.util.StringConverter;

public class EventRequesReviewA {

    private Scene eventRequestReviewScene;

    private final String PRIMARY = "#002A60";
    private final String SECONDARY = "#8CAFC6";
    private final String LIGHT = "#C2E0F4";

    private Label eventTypeSummary;
    private Label guestsSummary;
    private Label venueSummary;
    private Label locationSummary;
    private Label budgetSummary;
    private Label dateSummary;

    private Label eventNameBox;
    private Label durationBox;
    private Label startTimeBox;
    private Label dateBox;

    private Label customerAvatar;
    private Label customerName;
    private Label customerEmail;
    private Label customerMobile;

    private Label themeReq;
    private Label cateringReq;
    private Label decorReq;
    private Label notesReq;

    private ComboBox<String> statusCombo;
    private TextArea notesArea;

    Label lbl(String t, int s, boolean b) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", b ? FontWeight.BOLD : FontWeight.NORMAL, s));
        return l;
    }

    VBox box(String t, Label valueLabel) {
        Label a = lbl(t.toUpperCase(), 11, true);
        a.setTextFill(Color.web("#596273"));
        valueLabel.setFont(Font.font("Arial", 15));
        VBox x = new VBox(3, a, valueLabel);
        x.setPadding(new Insets(8));
        x.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;-fx-border-radius:7;-fx-background-radius:7;");
        return x;
    }

    VBox section(String icon, String title) {
        Label i = lbl(icon, 18, true), t = lbl(title, 19, true);
        i.setTextFill(Color.web(PRIMARY)); t.setTextFill(Color.web(PRIMARY));
        HBox h = new HBox(9, i, t);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(10, 18, 8, 18));
        return new VBox(h);
    }

    Button btn(String text, boolean primary) {
        Button b = new Button(text);
        b.setPrefHeight(38);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        b.setStyle(primary
                ? "-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;-fx-background-radius:7;-fx-cursor:hand;"
                : "-fx-background-color:white;-fx-text-fill:#C62828;-fx-border-color:#C62828;-fx-border-radius:7;-fx-cursor:hand;");
        return b;
    }

    public Scene geteventRequestReviewScene() {

        VBox sidebar = new VBox(7);
        sidebar.setPrefWidth(260); sidebar.setMinWidth(260); sidebar.setPadding(new Insets(25, 18, 18, 18));
        sidebar.setStyle("-fx-background-color:" + PRIMARY + ";");

        Label logo = lbl("EventXpert", 26, true); Label admin = lbl("ADMIN DASHBOARD", 12, true);
        logo.setTextFill(Color.WHITE); admin.setTextFill(Color.WHITE);

        VBox logoBox = new VBox(1, logo, admin);
        VBox menu = new VBox(7);

        String[] items = {"Dashboard", "Event Requests", "Events", "Calendar", "Customers", "Organizers", "Vendors", "Conflict Detection", "Reports & Analytics"};

        for (String s : items) {
            Label m = lbl(s, 14, true); m.setTextFill(Color.WHITE); m.setMaxWidth(Double.MAX_VALUE); m.setPadding(new Insets(9, 13, 9, 13));
            if (s.equals("Event Requests")) m.setStyle("-fx-background-color:#07539A;-fx-background-radius:5;");
            menu.getChildren().add(m);
        }

        Region space = new Region(); VBox.setVgrow(space, Priority.ALWAYS);
        Label n = lbl("🔔  Notifications", 13, true); Label se = lbl("⚙  Settings", 13, true);
        Label pr = lbl("👤  Profile", 13, true); Label sp = lbl("?  Support", 13, true);

        for (Label x : new Label[]{n, se, pr, sp}) {
            x.setTextFill(Color.WHITE); x.setPadding(new Insets(7, 10, 7, 10));
        }

        sidebar.getChildren().addAll(logoBox, menu, space, n, se, pr, sp);

        Region hs = new Region(); HBox.setHgrow(hs, Priority.ALWAYS);
        Label bell = lbl("🔔", 18, false); Label question = lbl("?", 18, true);
        Label gear = lbl("⚙", 18, false); Label user = lbl("👤", 18, false);

        HBox header = new HBox(20, hs, bell, question, gear, user);
        header.setAlignment(Pos.CENTER_RIGHT); header.setPadding(new Insets(8, 25, 8, 20)); header.setPrefHeight(55);
        header.setStyle("-fx-background-color:white;-fx-border-color:#E5EAF0;");

        Label title = lbl("Review Event Request", 28, true); title.setTextFill(Color.web(PRIMARY));
        Label sub = lbl("Review customer requirements before assigning a suitable organizer.", 14, false);
        VBox titleBox = new VBox(2, title, sub);

        Label pending = lbl("● Pending Review", 12, true);
        pending.setTextFill(Color.web(PRIMARY)); pending.setPadding(new Insets(6, 13, 6, 13));
        pending.setStyle("-fx-background-color:" + LIGHT + ";-fx-border-color:" + SECONDARY + ";-fx-border-radius:15;-fx-background-radius:15;");

        Button back = new Button("←  Back to Requests");
        back.setPrefSize(145, 36); back.setStyle("-fx-background-color:white;-fx-border-color:#8B949E;-fx-border-radius:7;-fx-cursor:hand;");
        back.setOnAction(e -> {
            EventRequestA requestsView = new EventRequestA();
            LandingPage.landingPageStage.setScene(requestsView.geteventRequestScene());
        });

        Region ts = new Region(); HBox.setHgrow(ts, Priority.ALWAYS);
        HBox titleRow = new HBox(12, titleBox, pending, ts, back); titleRow.setAlignment(Pos.CENTER_LEFT);

        eventTypeSummary = lbl("Loading...", 16, false);
        guestsSummary = lbl("Loading...", 16, false);
        venueSummary = lbl("Loading...", 16, false);
        locationSummary = lbl("Loading...", 16, false);
        budgetSummary = lbl("Loading...", 16, false);
        dateSummary = lbl("Loading...", 16, false);

        HBox summary = new HBox(
                summaryItem("EVENT TYPE", eventTypeSummary),
                summaryItem("GUESTS", guestsSummary),
                summaryItem("VENUE", venueSummary),
                summaryItem("LOCATION", locationSummary),
                summaryItem("BUDGET", budgetSummary),
                summaryItem("DATE", dateSummary)
        );
        summary.setPadding(new Insets(6, 12, 6, 12));
        summary.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;-fx-border-radius:9;-fx-background-radius:9;");

        VBox eventCard = new VBox(section("", "Event Details"));
        GridPane eg = new GridPane(); eg.setHgap(15); eg.setVgap(10); eg.setPadding(new Insets(5, 18, 14, 18));

        eventNameBox = lbl("Loading...", 15, false);
        durationBox = lbl("Flexible", 15, false);
        startTimeBox = lbl("Flexible", 15, false);
        dateBox = lbl("Loading...", 15, false);

        eg.add(box("Event Name", eventNameBox), 0, 0);
        eg.add(box("Duration", durationBox), 1, 0);
        eg.add(box("Start Time", startTimeBox), 0, 1);
        eg.add(box("Date", dateBox), 1, 1);

        eventCard.getChildren().add(eg);
        eventCard.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;-fx-border-radius:9;-fx-background-radius:9;");

        VBox customerCard = new VBox(section("", "Customer Information"));
        customerAvatar = lbl("C", 23, false);
        customerAvatar.setAlignment(Pos.CENTER); customerAvatar.setPrefSize(48, 48);
        customerAvatar.setStyle("-fx-background-color:#D7E1FF;-fx-background-radius:50;");

        customerName = lbl("Loading...", 18, true);
        Label clientType = lbl("Registered Client", 13, false);
        VBox ct = new VBox(1, customerName, clientType);

        HBox topCustomer = new HBox(12, customerAvatar, ct); topCustomer.setAlignment(Pos.CENTER_LEFT);
        customerEmail = lbl("✉  Loading...", 13, false);
        customerMobile = lbl("☎  Loading...", 13, false);

        customerCard.getChildren().addAll(topCustomer, customerEmail, customerMobile);
        customerCard.setSpacing(9); customerCard.setPadding(new Insets(0, 20, 15, 20));
        customerCard.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;-fx-border-radius:9;-fx-background-radius:9;");

        VBox reqCard = new VBox(section("☷", "Event Requirements"));
        GridPane rg = new GridPane(); rg.setHgap(12); rg.setVgap(10); rg.setPadding(new Insets(5, 18, 14, 18));

        themeReq = lbl("Standard", 15, false);
        cateringReq = lbl("Required", 15, false);
        decorReq = lbl("Required", 15, false);
        notesReq = lbl("No additional notes provided.", 14, false);

        rg.add(box("Category", themeReq), 0, 0);
        rg.add(box("Requirements", cateringReq), 1, 0);

        VBox special = box("Special Notes", notesReq);
        special.setStyle("-fx-background-color:" + LIGHT + ";-fx-border-color:#9BD2F2;-fx-border-radius:7;-fx-background-radius:7;");
        rg.add(special, 0, 1, 2, 1);
        reqCard.getChildren().add(rg);
        reqCard.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;-fx-border-radius:9;-fx-background-radius:9;");

        VBox reviewCard = new VBox(section("◉", "Admin Review"));
        statusCombo = new ComboBox<>(FXCollections.observableArrayList("Pending Review", "Under Review", "Approved", "Rejected"));
        statusCombo.setValue("Pending Review"); statusCombo.setMaxWidth(Double.MAX_VALUE); statusCombo.setPrefHeight(36);

        notesArea = new TextArea(); notesArea.setPromptText("Add internal admin notes regarding organizer assignment..."); notesArea.setWrapText(true); notesArea.setPrefRowCount(3);

        Button assign = btn("Assign Organizer", true);
        assign.setOnAction(e -> handleAssignOrganizerModal());

        Button reject = btn("Reject Request", false);
        reject.setOnAction(e -> {
            String selectedEvtId = AdminContext.getSelectedEventId();
            if (selectedEvtId == null || selectedEvtId.isBlank()) return;

            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to reject this event request?", ButtonType.YES, ButtonType.NO);
            a.setTitle("Reject Request");
            a.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    AdminController controller = new AdminController();
                    controller.updateEventStatus(selectedEvtId, "REJECTED");
                    EventRequestA requestsView = new EventRequestA();
                    LandingPage.landingPageStage.setScene(requestsView.geteventRequestScene());
                }
            });
        });

        Button checkConflict = btn("🔍 Check Conflict", true);
        checkConflict.setOnAction(e -> checkEventConflict());

        Button approve = btn("Approve Event", true);
        approve.setOnAction(e -> handleApproveEvent());
      Button assignButton = btn("Assign Organizer", true);
        assignButton.setOnAction(e -> {
            Stage stage = (Stage) assignButton.getScene().getWindow();
            if (stage == null) {
                stage = LandingPage.landingPageStage;
            }
            
            // Organizer Assign Screen उघडा
            OrganizerAssignA assignPage = new OrganizerAssignA();
            stage.setScene(assignPage.getorganizerAssignScene());
        });

       

        Button approve1 = btn("Approve Event", true);

approve1.setOnAction(
        e -> handleApproveEvent()
);

        VBox rb = new VBox(
                7,
                lbl("Request Status", 12, true),
                statusCombo,
                lbl("Review Notes (Internal)", 12, true),
                notesArea,
                checkConflict,
                approve1,
                assign,
                reject
        );
        rb.setPadding(new Insets(0, 20, 15, 20));
        reviewCard.getChildren().add(rb);
        reviewCard.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;-fx-border-radius:9;-fx-background-radius:9;");

        VBox leftCol = new VBox(12, eventCard, reqCard);
        VBox rightCol = new VBox(12, customerCard, reviewCard);
        leftCol.setPrefWidth(700); rightCol.setPrefWidth(350);

        HBox body = new HBox(15, leftCol, rightCol); HBox.setHgrow(leftCol, Priority.ALWAYS);
        VBox contentBox = new VBox(10, titleRow, summary, body); contentBox.setPadding(new Insets(10, 25, 15, 25)); contentBox.setStyle("-fx-background-color:#F7F9FC;");

        VBox center = new VBox(header, contentBox); VBox.setVgrow(contentBox, Priority.ALWAYS);
        BorderPane root = new BorderPane(); root.setLeft(sidebar); root.setCenter(center);

        loadEventReviewDetailsAsync();

        eventRequestReviewScene = new Scene(root, 1550, 750);
        return eventRequestReviewScene;
    }

    // ==========================================
    // ASSIGN ORGANIZER MODAL (Organization Name Display)
    // ==========================================
    private void handleAssignOrganizerModal() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "No event selected for review.");
            a.showAndWait();
            return;
        }

        AdminController controller = new AdminController();
        List<OrganizerProfile> activeOrganizers = controller.getAllActiveOrganizers();

        if (activeOrganizers == null || activeOrganizers.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "No active organizers found in Firestore collection 'Organizers'.");
            a.showAndWait();
            return;
        }

        // Custom Dialog for Beautiful Organization Selection
        Dialog<OrganizerProfile> dialog = new Dialog<>();
        dialog.setTitle("Assign Event to Organizer");
        dialog.setHeaderText("Select an Organization to assign this event:");

        ButtonType assignBtnType = new ButtonType("Assign Organizer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignBtnType, ButtonType.CANCEL);

        VBox contentPane = new VBox(12);
        contentPane.setPadding(new Insets(16, 24, 16, 24));

        Label chooseLbl = new Label("Choose Organization:");
        chooseLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");

        ComboBox<OrganizerProfile> orgComboBox = new ComboBox<>(FXCollections.observableArrayList(activeOrganizers));
        orgComboBox.setMaxWidth(Double.MAX_VALUE);
        orgComboBox.setPrefHeight(40);
        orgComboBox.setValue(activeOrganizers.get(0));

        // Display Organization Name instead of ID
        orgComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(OrganizerProfile org) {
                if (org == null) return "";
                String orgName = (org.getOrganizationName() != null && !org.getOrganizationName().isBlank())
                        ? org.getOrganizationName()
                        : (org.getName() != null ? org.getName() : "Organizer");

                String contact = org.getEmail() != null ? " (" + org.getEmail() + ")" : "";
                return "🏢 " + orgName + contact;
            }

            @Override
            public OrganizerProfile fromString(String string) {
                return null;
            }
        });

        contentPane.getChildren().addAll(chooseLbl, orgComboBox);
        dialog.getDialogPane().setContent(contentPane);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == assignBtnType) {
                return orgComboBox.getValue();
            }
            return null;
        });

        Optional<OrganizerProfile> result = dialog.showAndWait();
        result.ifPresent(selectedOrganizer -> {
            String orgUid = selectedOrganizer.getOrganizerId();
            String orgName = (selectedOrganizer.getOrganizationName() != null && !selectedOrganizer.getOrganizationName().isBlank())
                    ? selectedOrganizer.getOrganizationName()
                    : selectedOrganizer.getName();

            String notes = notesArea != null ? notesArea.getText().trim() : "";

            boolean updated = controller.assignOrganizerToEvent(selectedEvtId, orgUid, orgName, "ADMIN_UID", notes);
            if (updated) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Event successfully assigned to Organization [" + orgName + "] in Firestore!");
                a.setHeaderText("Assignment Successful");
                a.showAndWait();

                EventRequestA requestsView = new EventRequestA();
                LandingPage.landingPageStage.setScene(requestsView.geteventRequestScene());
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to update organizer assignment in Firestore.");
                a.showAndWait();
            }
        });
    }

    private void loadEventReviewDetailsAsync() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                AdminController controller = new AdminController();
                Event event = controller.getEventById(selectedEvtId);
                if (event != null) {
                    CustomerProfile cust = controller.getCustomerProfile(event.getCustomerId());

                    Platform.runLater(() -> {
                        eventTypeSummary.setText(event.getEventType() != null ? event.getEventType() : "General");
                        guestsSummary.setText(event.getGuests() + " expected");
                        venueSummary.setText(event.getVenueType() != null ? event.getVenueType() : "Banquet Hall");
                        locationSummary.setText(event.getCity() != null ? event.getCity() : "N/A");
                        budgetSummary.setText(event.getBudget() != null ? event.getBudget() : "N/A");
                        dateSummary.setText(event.getEventDate() != null ? event.getEventDate() : "TBD");

                        eventNameBox.setText(event.getEventName() != null ? event.getEventName() : "Unnamed Event");
                        dateBox.setText(event.getEventDate() != null ? event.getEventDate() : "TBD");
                        if (event.getTime() != null && !event.getTime().isBlank()) startTimeBox.setText(event.getTime());

                        themeReq.setText(event.getCategory() != null ? event.getCategory() : "Formal");
                        cateringReq.setText(event.getRequirements() != null ? event.getRequirements() : "General");
                        if (event.getNotes() != null && !event.getNotes().isBlank()) notesReq.setText(event.getNotes());

                        if (cust != null) {
                            String name = cust.getName() != null && !cust.getName().isBlank() ? cust.getName() : "Customer";
                            customerName.setText(name);
                            customerAvatar.setText(name.substring(0, 1).toUpperCase());
                            customerEmail.setText("✉  " + (cust.getEmail() != null ? cust.getEmail() : "N/A"));
                            customerMobile.setText("☎  " + (cust.getMobileNo() != null ? cust.getMobileNo() : "N/A"));
                        }
                    });
                }
                return null;
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void checkEventConflict() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No event selected for conflict checking.");
            alert.setTitle("Conflict Detection");
            alert.showAndWait();
            return;
        }

        AdminController controller = new AdminController();
        Event event = controller.getEventById(selectedEvtId);
        if (event == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Event details could not be loaded.");
            alert.setTitle("Conflict Detection");
            alert.showAndWait();
            return;
        }

        ConflictResult result = controller.checkEventConflict(event);
        boolean conflict = result.isConflict();

        if (conflict) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "⚠ CONFLICT DETECTED!\n\nAnother event is already scheduled at the same venue/date/time.");
            alert.setTitle("Conflict Detection");
            alert.setHeaderText("Event Scheduling Conflict");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ No Conflict Found!\n\nThis event can be scheduled without a detected venue/time conflict.");
            alert.setTitle("Conflict Detection");
            alert.setHeaderText("No Conflict");
            alert.showAndWait();
        }
    }

    private VBox summaryItem(String t, Label valLabel) {
        Label a = lbl(t, 11, true);
        a.setTextFill(Color.web("#596273"));
        valLabel.setFont(Font.font("Arial", 16));
        VBox x = new VBox(2, a, valLabel);
        x.setPadding(new Insets(5, 22, 5, 10));
        HBox.setHgrow(x, Priority.ALWAYS);
        return x;
    }

    private void handleApproveEvent() {
        String selectedEvtId = AdminContext.getSelectedEventId();
        if (selectedEvtId == null || selectedEvtId.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No event selected.");
            alert.setTitle("Approve Event");
            alert.showAndWait();
            return;
        }

        AdminController controller = new AdminController();
        Event event = controller.getEventById(selectedEvtId);
        if (event == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Event details could not be loaded.");
            alert.setTitle("Approve Event");
            alert.showAndWait();
            return;
        }

        ConflictResult result = controller.checkEventConflict(event);
        if (result != null && result.isConflict()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Conflict Detected");
            alert.setHeaderText("⚠ Event cannot be approved");
            alert.setContentText("Another event already exists with the same venue, date and overlapping time.");
            alert.showAndWait();
            return;
        }

        boolean updated = controller.updateEventStatus(selectedEvtId, "APPROVED");
        if (updated) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event approved successfully.");
            alert.setTitle("Event Approval");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to approve event.");
            alert.setTitle("Event Approval");
            alert.showAndWait();
        }
    }
}