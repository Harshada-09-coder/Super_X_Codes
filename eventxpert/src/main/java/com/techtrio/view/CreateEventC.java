package com.techtrio.view;

import com.techtrio.controller.CustomerController;
import com.techtrio.controller.CustomerState;
import com.techtrio.session.UserSession;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CreateEventC {

    private Scene scene;
    private String uid;
    private String idToken;

    private static final String NAVY = "#061B41";
    private static final String BLUE = "#0D3979";
    private static final String BG = "#F5F8FC";
    private static final String BORDER = "#D0D5DD";
    private static final String TEXT = "#101828";
    private static final String GREY = "#667085";

    public CreateEventC(String uid, String idToken) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UserSession.getUid();
        this.idToken = (idToken != null && !idToken.isBlank()) ? idToken : UserSession.getIdToken();
        UserSession.setSession(this.uid, this.idToken, null, "Customer");
    }

    public Scene getCreateEventScene() {
        return getCreateEventScene(null);
    }

    public Scene getCreateEventScene(Runnable backAction) {
        ReviewPageC.EventData.currentStep = 1;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // SIDEBAR (Consistent with Dashboard)
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
                sidebarItem("[1]", "Event Details", "fas-info-circle", true),
                sidebarItem("[2]", "Requirements", "fas-list-ul", false),
                sidebarItem("[3]", "Venue & Location", "fas-map-marker-alt", false),
                sidebarItem("[4]", "Budget", "fas-wallet", false),
                sidebarItem("[5]", "Guest", "fas-users", false),
                sidebarItem("[6]", "AI Planning", "fas-magic", false),
                sidebarItem("[7]", "Review", "fas-check-circle", false)
        );

        Region sideSpace = new Region();
        VBox.setVgrow(sideSpace, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandBox, titleBox, menu, sideSpace);

        // CONTENT (TopBar Removed - Spacious & Clean)
        VBox content = new VBox(14);
        content.setPadding(new Insets(28, 45, 20, 45));
        content.setStyle("-fx-background-color:" + BG + ";");

        Label stepNumber = label("STEP 1 OF 7", 10, FontWeight.BOLD, BLUE);
        Label heading = label("Create Your Event", 26, FontWeight.BOLD, NAVY);
        Label subtitle = label("Tell us a little about the event you are planning.", 13, FontWeight.NORMAL, GREY);

        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-border-color:" + BORDER + ";-fx-border-radius:10;");

        Label categoryTitle = label("Event Category", 15, FontWeight.BOLD, TEXT);
        ToggleGroup group = new ToggleGroup();

        boolean isInformal = "Informal".equalsIgnoreCase(ReviewPageC.EventData.category);
        ToggleButton formal = categoryCard("🏢", "Formal", "Conferences, Corporate, Galas", !isInformal, group);
        ToggleButton informal = categoryCard("🎉", "Informal", "Parties, Retreats, Socials", isInformal, group);

        HBox categories = new HBox(14, formal, informal);
        HBox.setHgrow(formal, Priority.ALWAYS);
        HBox.setHgrow(informal, Priority.ALWAYS);

        Separator separator = new Separator();

        TextField eventName = new TextField(ReviewPageC.EventData.eventName != null ? ReviewPageC.EventData.eventName : "");
        eventName.setPromptText("e.g., Annual Tech Summit 2024");
        styleField(eventName);

        ComboBox<String> eventType = new ComboBox<>();
        eventType.getItems().addAll("Conference", "Corporate Event", "Birthday", "Family Event", "Educational Event", "Other");

        if (ReviewPageC.EventData.eventType != null && !ReviewPageC.EventData.eventType.isBlank()) {
            eventType.setValue(ReviewPageC.EventData.eventType);
        } else {
            eventType.setPromptText("Select a type...");
        }
        eventType.setMaxWidth(Double.MAX_VALUE);
        styleControl(eventType);

        // ⭐ Clean Dynamic Guest Field (No Hardcoded 100 or 0)
        TextField guests = new TextField(ReviewPageC.EventData.guests > 0 ? String.valueOf(ReviewPageC.EventData.guests) : "");
        guests.setPromptText("e.g., 100");
        styleField(guests);

        HBox row1 = new HBox(17, formField("Event Type", eventType), formField("Expected Guests", guests));
        HBox.setHgrow(row1.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row1.getChildren().get(1), Priority.ALWAYS);

        // START DATE
        DatePicker startDate = new DatePicker();
        startDate.setPromptText("dd-mm-yyyy");
        if (ReviewPageC.EventData.startDate != null && !ReviewPageC.EventData.startDate.isBlank()) {
            try {
                startDate.setValue(LocalDate.parse(ReviewPageC.EventData.startDate));
            } catch (Exception ignored) {}
        }
        startDate.setMaxWidth(Double.MAX_VALUE);
        styleControl(startDate);

        // END DATE
        DatePicker endDate = new DatePicker();
        endDate.setPromptText("dd-mm-yyyy");
        if (ReviewPageC.EventData.endDate != null && !ReviewPageC.EventData.endDate.isBlank()) {
            try {
                endDate.setValue(LocalDate.parse(ReviewPageC.EventData.endDate));
            } catch (Exception ignored) {}
        }
        endDate.setMaxWidth(Double.MAX_VALUE);
        styleControl(endDate);

        // START TIME
        TextField startTime = new TextField(ReviewPageC.EventData.startTime != null ? ReviewPageC.EventData.startTime : "");
        startTime.setPromptText("e.g., 10:00 AM");
        styleField(startTime);

        // END TIME
        TextField endTime = new TextField(ReviewPageC.EventData.endTime != null ? ReviewPageC.EventData.endTime : "");
        endTime.setPromptText("e.g., 06:00 PM");
        styleField(endTime);

        HBox row2 = new HBox(17, formField("Start Date", startDate), formField("End Date", endDate));
        HBox.setHgrow(row2.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row2.getChildren().get(1), Priority.ALWAYS);

        HBox row3 = new HBox(17, formField("Start Time", startTime), formField("End Time", endTime));
        HBox.setHgrow(row3.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row3.getChildren().get(1), Priority.ALWAYS);

        card.getChildren().addAll(categoryTitle, categories, separator, formField("Event Name", eventName), row1, row2, row3);
        content.getChildren().addAll(stepNumber, heading, subtitle, card);

        // FOOTER
        HBox footer = new HBox(15);
        footer.setPrefHeight(60);
        footer.setMinHeight(60);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 35, 0, 35));
        footer.setStyle("-fx-background-color:white;-fx-border-color:#E4E7EC;-fx-border-width:1 0 0 0;");

        Label copyright = label("(C) 2026 EventXpert", 11, FontWeight.BOLD, GREY);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);

        Button back = new Button("Back");
        back.setPrefSize(82, 38);
        back.setStyle("-fx-background-color:white;-fx-text-fill:" + BLUE + ";-fx-border-color:" + BLUE + ";-fx-border-radius:7;-fx-background-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");

        back.setOnAction(e -> {
            String enteredName = eventName.getText().trim();
            if (!enteredName.isEmpty()) {
                ReviewPageC.EventData.category = formal.isSelected() ? "Formal" : "Informal";
                ReviewPageC.EventData.eventName = enteredName;
                ReviewPageC.EventData.eventType = eventType.getValue() != null ? eventType.getValue() : "Other";
                try { ReviewPageC.EventData.guests = Integer.parseInt(guests.getText().trim()); } catch (Exception ignored) {}
                ReviewPageC.EventData.startDate = startDate.getValue() != null ? startDate.getValue().toString() : "";
                ReviewPageC.EventData.endDate = endDate.getValue() != null ? endDate.getValue().toString() : "";
                ReviewPageC.EventData.startTime = startTime.getText().trim();
                ReviewPageC.EventData.endTime = endTime.getText().trim();
                ReviewPageC.EventData.saveDraft(1);
            }

            if (backAction != null) {
                backAction.run();
            } else {
                ActiveCustomerDashboardC activeDash = new ActiveCustomerDashboardC(uid, idToken);
                LandingPage.landingPageStage.setScene(activeDash.getactiveCustomerScene(null));
            }
        });

        Button next = new Button("Next");
        next.setPrefSize(82, 38);
        next.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-background-radius:7;-fx-font-weight:bold;-fx-cursor:hand;");

        next.setOnAction(e -> {
            if (eventName.getText().trim().isEmpty()) {
                showWarningPopup("Event Name Required", "Please enter an Event Name.");
                return;
            }
            if (startDate.getValue() == null) {
                showWarningPopup("Start Date Required", "Please select the Start Date.");
                return;
            }
            if (endDate.getValue() == null) {
                showWarningPopup("End Date Required", "Please select the End Date.");
                return;
            }
            if (endDate.getValue().isBefore(startDate.getValue())) {
                showWarningPopup("Invalid Event Dates", "End Date cannot be before Start Date.");
                return;
            }
            if (startTime.getText().trim().isEmpty()) {
                showWarningPopup("Start Time Required", "Please enter the Start Time.");
                return;
            }
            if (endTime.getText().trim().isEmpty()) {
                showWarningPopup("End Time Required", "Please enter the End Time.");
                return;
            }
            if (startDate.getValue().isEqual(endDate.getValue())) {
                LocalTime sTime = parseTimeFlexible(startTime.getText().trim());
                LocalTime eTime = parseTimeFlexible(endTime.getText().trim());
                if (sTime != null && eTime != null && (eTime.isBefore(sTime) || eTime.equals(sTime))) {
                    showWarningPopup("Invalid Event Time", "End Time must be after Start Time on the same date.");
                    return;
                }
            }

            ReviewPageC.EventData.category = formal.isSelected() ? "Formal" : "Informal";
            ReviewPageC.EventData.eventName = eventName.getText().trim();
            ReviewPageC.EventData.eventType = eventType.getValue() != null ? eventType.getValue() : "Other";
            try {
                ReviewPageC.EventData.guests = Integer.parseInt(guests.getText().trim());
            } catch (Exception ex) {
                ReviewPageC.EventData.guests = 0;
            }

            ReviewPageC.EventData.startDate = startDate.getValue().toString();
            ReviewPageC.EventData.endDate = endDate.getValue().toString();
            ReviewPageC.EventData.startTime = startTime.getText().trim();
            ReviewPageC.EventData.endTime = endTime.getText().trim();
            ReviewPageC.EventData.saveDraft(2);

            RequirementsC reqView = new RequirementsC(uid, idToken);
            LandingPage.landingPageStage.setScene(reqView.getRequirementsScene());
        });

        footer.getChildren().addAll(copyright, footerSpace, back, next);

        VBox rightSide = new VBox();
        rightSide.getChildren().addAll(content, footer);
        VBox.setVgrow(content, Priority.ALWAYS);

        HBox fullPage = new HBox();
        fullPage.getChildren().addAll(sidebar, rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        root.setCenter(fullPage);
        scene = new Scene(root, 1550, 750);
        return scene;
    }

    private void showWarningPopup(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("EventXpert");
        alert.setHeaderText(header);
        alert.setContentText(message);
        if (LandingPage.landingPageStage != null) {
            alert.initOwner(LandingPage.landingPageStage);
        }
        alert.showAndWait();
    }

    private LocalTime parseTimeFlexible(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        String clean = timeStr.trim().toUpperCase();
        String[] patterns = {"h:mm a", "hh:mm a", "h:mma", "hh:mma", "H:mm", "HH:mm"};
        for (String pattern : patterns) {
            try {
                return LocalTime.parse(clean, DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            } catch (Exception ignored) {}
        }
        return null;
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

    private ToggleButton categoryCard(String icon, String title, String description, boolean selected, ToggleGroup group) {
        ToggleButton button = new ToggleButton();
        button.setToggleGroup(group);
        button.setSelected(selected);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(120);
        button.setMinHeight(120);

        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        Label iconLabel = label(icon, 22, FontWeight.NORMAL, BLUE);
        Label titleLabel = label(title, 14, FontWeight.BOLD, BLUE);
        Label descriptionLabel = label(description, 12, FontWeight.NORMAL, TEXT);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setAlignment(Pos.CENTER);

        Label selectedLabel = label("Selected", 11, FontWeight.BOLD, BLUE);
        selectedLabel.setStyle("-fx-background-color:#C6E9FF;-fx-background-radius:12;-fx-padding:4 8;");

        box.getChildren().addAll(iconLabel, titleLabel, descriptionLabel);
        if (selected) {
            box.getChildren().add(selectedLabel);
        }

        button.setGraphic(box);
        updateCategory(button, selected);

        button.selectedProperty().addListener((obs, oldValue, newValue) -> {
            updateCategory(button, newValue);
            if (newValue) {
                if (!box.getChildren().contains(selectedLabel)) {
                    box.getChildren().add(selectedLabel);
                }
            } else {
                box.getChildren().remove(selectedLabel);
            }
        });

        return button;
    }

    private void updateCategory(ToggleButton button, boolean selected) {
        button.setStyle(selected
                ? "-fx-background-color:#F8FAFC;-fx-border-color:" + BLUE + ";-fx-border-width:1.5;-fx-border-radius:7;-fx-background-radius:7;"
                : "-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:7;-fx-background-radius:7;");
    }

    private VBox formField(String name, Control control) {
        VBox box = new VBox(5);
        box.getChildren().addAll(label(name, 11, FontWeight.BOLD, TEXT), control);
        return box;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(36);
        field.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-padding:0 12;-fx-font-size:12px;-fx-text-fill:" + NAVY + ";");
    }

    private void styleControl(Control control) {
        control.setStyle("-fx-background-color:white;-fx-border-color:" + BORDER + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-size:12px;");
    }

    private Label label(String text, double size, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", weight, size));
        label.setStyle("-fx-text-fill: " + color + " !important;");
        return label;
    }
}