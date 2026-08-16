package com.superx.view;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

  

public class ConflictDetectionReview extends Application {

    final String NAVY = "#002A60";
    final String BLUE = "#1976B8";
    final String BG = "#F6F9FC";

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        /* ================= SIDEBAR ================= */

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(300);
        sidebar.setMinWidth(300);
        sidebar.setMaxWidth(300);
        sidebar.setStyle("-fx-background-color:" + NAVY + ";");

        VBox top = new VBox(10);
        top.setPadding(new Insets(18, 15, 10, 15));

        Label logo = new Label("EventXpert");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label admin = new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        top.getChildren().addAll(logo, admin);

        VBox menu = new VBox(7);
        menu.setPadding(new Insets(10, 6, 0, 6));

        String[] items = {
            "▦   Dashboard",
            "▫   Event Requests",
            "▫   Events",
            "▦   Calendar",
            "♙   Customers",
            "♙   Organizers",
            "▱   Vendors"
        };

        for (String s : items)
            menu.getChildren().add(navButton(s, false));

        menu.getChildren().add(navButton("⚠   Conflict Detection", true));
        menu.getChildren().add(navButton("▫   Reports & Analytics", false));

        VBox bottom = new VBox(8);
        bottom.setPadding(new Insets(0, 6, 15, 6));

        bottom.getChildren().addAll(
            navButton("♧   Notifications", false),
            navButton("⚙   Settings", false),
            navButton("♙   Profile", false),
            navButton("?   Support", false)
        );

        VBox.setVgrow(menu, Priority.NEVER);
        Region gap = new Region();
        VBox.setVgrow(gap, Priority.ALWAYS);

        sidebar.getChildren().addAll(top, menu, gap, bottom);

        /* ================= MAIN CONTENT ================= */

        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 28, 30, 28));
        content.setMaxWidth(Double.MAX_VALUE);

        /* Header */

        HBox backRow = new HBox();
        Button back = new Button("← Back to Conflicts");
        back.setStyle("-fx-background-color:NAVY");
        back.setFont(Font.font("Arial", 18));
        back.setTextFill(Color.web("#F6F9FC"));

        backRow.getChildren().add(back);

        
        Label title = new Label("Review Conflict");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 34));
        title.setTextFill(Color.web(NAVY));

        Label subtitle = new Label(
            "Review the conflict details and take appropriate action."
        );
        subtitle.setFont(Font.font("Arial", 16));

        HBox titleRow = new HBox();
        VBox heading = new VBox(5, title, subtitle);
        HBox.setHgrow(heading, Priority.ALWAYS);

        Label critical = badge("⚠ Critical", "#FFD6D6", "#C62828");
        Label open = badge("Open", "#D9EEFF", NAVY);

        HBox badges = new HBox(8, critical, open);
        badges.setAlignment(Pos.TOP_RIGHT);

        titleRow.getChildren().addAll(heading, badges);

        /* Main two-column layout */

        GridPane grid = new GridPane();
        grid.setHgap(22);
        grid.setVgap(18);
        grid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(68);

        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(32);

        grid.getColumnConstraints().addAll(left, right);

        /* Conflict Overview */

        VBox overview = card();
        overview.getChildren().add(sectionTitle("Conflict Overview"));

        HBox info = new HBox(35);
        info.getChildren().addAll(
            infoBox("TYPE", "Organizer\nDouble Booking"),
            infoBox("SEVERITY", "⚠ Critical"),
            infoBox("STATUS", "Open"),
            infoBox("DETECTED", "Aug 14, 2026,\n10:20 AM")
        );

        VBox resource = new VBox(7);
        resource.setPadding(new Insets(14));
        resource.setStyle(
            "-fx-background-color:#FFF7F7;" +
            "-fx-border-color:#FFBABA;" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        Label rt = new Label("♟  Conflicting Resource: Elite Event Planners");
        rt.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        rt.setTextFill(Color.web("#C62828"));

        Label rd = new Label(
            "Type: Organizer   |   Availability: Already assigned   |   Workload: 2 Active Events"
        );
        rd.setFont(Font.font("Arial", 14));

        Label note = new Label(
            "Conflict Note: Elite Event Planners cannot manage two major corporate events simultaneously in different cities."
        );
        note.setWrapText(true);
        note.setPadding(new Insets(10));
        note.setStyle(
            "-fx-border-color:#FFCCCC;" +
            "-fx-background-color:white;" +
            "-fx-border-radius:6;"
        );

        resource.getChildren().addAll(rt, rd, note);
        overview.getChildren().addAll(info, resource);

        /* Suggested Resolution */

        VBox suggested = card();
        suggested.setStyle(
            "-fx-background-color:#DFF1FC;" +
            "-fx-border-color:#A9D9F5;" +
            "-fx-border-radius:10;" +
            "-fx-background-radius:10;"
        );

        suggested.getChildren().add(sectionTitle("💡  Suggested Resolution"));

        Label stext = new Label(
            "Based on historical data and available resources, the system recommends:"
        );
        stext.setWrapText(true);

        VBox recommendation = new VBox(8);
        recommendation.setAlignment(Pos.CENTER);
        recommendation.setPadding(new Insets(15));
        recommendation.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:#B7DDF5;" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        Label rtitle = new Label("Reassign Organizer");
        rtitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        rtitle.setTextFill(Color.web(NAVY));

        Label rtext = new Label(
            "Reassign 'Corporate Workshop' (Mumbai) to 'Prime Event Mgmt' (Available)."
        );
        rtext.setWrapText(true);
        rtext.setAlignment(Pos.CENTER);

        recommendation.getChildren().addAll(rtitle, rtext);
        suggested.getChildren().addAll(stext, recommendation);

        grid.add(overview, 0, 0);
        grid.add(suggested, 1, 0);

        /* Involved Events */

        Label involved = sectionTitle("Involved Events");

        HBox events = new HBox(18);
        events.setMaxWidth(Double.MAX_VALUE);

        VBox event1 = eventCard(
            "Corporate\nSeminar",
            "18 Aug 2026",
            "10:00 AM – 02:00 PM",
            "Pune",
            "Incubators Pvt."
        );

        VBox event2 = eventCard(
            "Corporate\nWorkshop",
            "18 Aug 2026",
            "10:00 AM – 01:00 PM",
            "Mumbai",
            "ABC Corporation"
        );

        HBox.setHgrow(event1, Priority.ALWAYS);
        HBox.setHgrow(event2, Priority.ALWAYS);

        events.getChildren().addAll(event1, event2);

        VBox impact = card();
        impact.getChildren().add(sectionTitle("⌁  Potential Impact"));

        impact.getChildren().addAll(
            bullet("Logistical delays in material setup at Mumbai location."),
            bullet("Quality of service degradation for VIP client (Incubators Pvt.)."),
            bullet("Travel time between Pune and Mumbai makes physical oversight impossible.")
        );

        /* Activity Log */

        VBox activity = card();
        activity.getChildren().add(sectionTitle("Activity Log"));

        activity.getChildren().addAll(
            activityItem("Just Now", "Review Started",
                "Admin User opened conflict details."),
            activityItem("Aug 14, 2026, 10:20 AM",
                "Conflict Detected",
                "System identified schedule and location overlap during event creation."),
            activityItem("Aug 14, 2026, 10:35 AM",
                "Resolution Pending",
                "Waiting for admin action.")
        );

        GridPane lowerGrid = new GridPane();
        lowerGrid.setHgap(22);
        lowerGrid.setVgap(18);

        ColumnConstraints lc1 = new ColumnConstraints();
        lc1.setPercentWidth(68);

        ColumnConstraints lc2 = new ColumnConstraints();
        lc2.setPercentWidth(32);

        lowerGrid.getColumnConstraints().addAll(lc1, lc2);

        lowerGrid.add(events, 0, 0);
        lowerGrid.add(impact, 1, 0);

        /* Take Action */

        VBox action = card();
        action.getChildren().add(sectionTitle("Take Action"));

        Label actionLabel = new Label("Select Resolution Action");
        actionLabel.setFont(Font.font(13));

        ComboBox<String> actionBox = new ComboBox<>();
        actionBox.getItems().addAll(
            "Reassign Organizer",
            "Change Event Time",
            "Change Venue",
            "Change Vendor",
            "Keep As Is"
        );
        actionBox.setPromptText("Select Action...");
        actionBox.setMaxWidth(Double.MAX_VALUE);
        actionBox.setPrefHeight(40);

        Label notesLabel = new Label("Resolution Notes");

        TextArea notes = new TextArea();
        notes.setPromptText("Provide details about the action taken...");
        notes.setPrefRowCount(4);
        notes.setWrapText(true);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button backBtn = new Button("Back");
        Button resolveBtn = new Button("Resolve Conflict");

        backBtn.setPrefWidth(90);
        resolveBtn.setPrefWidth(155);

        backBtn.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:" + NAVY + ";" +
            "-fx-border-radius:6;" +
            "-fx-font-size:14;"
        );

        resolveBtn.setStyle(
            "-fx-background-color:" + NAVY + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:6;" +
            "-fx-font-size:14;"
        );

        buttons.getChildren().addAll(backBtn, resolveBtn);

        action.getChildren().addAll(
            actionLabel,
            actionBox,
            notesLabel,
            notes,
            buttons
        );

        GridPane finalGrid = new GridPane();
        finalGrid.setHgap(22);

        ColumnConstraints fc1 = new ColumnConstraints();
        fc1.setPercentWidth(68);

        ColumnConstraints fc2 = new ColumnConstraints();
        fc2.setPercentWidth(32);

        finalGrid.getColumnConstraints().addAll(fc1, fc2);

        finalGrid.add(action, 0, 0);
        finalGrid.add(activity, 1, 0);

        content.getChildren().addAll(
            backRow,
            titleRow,
            grid,
            involved,
            lowerGrid,
            finalGrid
        );

        /* ================= SCROLL PANE ================= */

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color:" + BG + ";");

        root.setLeft(sidebar);
        root.setCenter(scroll);

        Scene scene = new Scene(root, 1550, 750);

        stage.setTitle("EventXpert - Review Conflict");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.show();
    }

    /* ================= SIDEBAR BUTTON ================= */

    private Button navButton(String text, boolean active) {

        Button b = new Button(text);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(45);

        if (active) {
            b.setStyle(
                "-fx-background-color:#1976B8;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:6;"
            );
        } else {
            b.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:18px;"
            );
        }

        return b;
    }

    /* ================= CARD ================= */

    private VBox card() {

        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setMaxWidth(Double.MAX_VALUE);

        box.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:#D9E2EA;" +
            "-fx-border-radius:10;" +
            "-fx-background-radius:10;"
        );

        return box;
    }

    private Label sectionTitle(String text) {

        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        l.setTextFill(Color.web("#002A60"));

        return l;
    }

    private Label badge(String text, String bg, String fg) {

        Label l = new Label(text);
        l.setPadding(new Insets(6, 13, 6, 13));
        l.setTextFill(Color.web(fg));
        l.setStyle(
            "-fx-background-color:" + bg + ";" +
            "-fx-background-radius:20;"
        );

        return l;
    }

    private VBox infoBox(String title, String value) {

        VBox v = new VBox(5);

        Label t = new Label(title);
        t.setFont(Font.font("Arial", 12));

        Label val = new Label(value);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        val.setWrapText(true);

        v.getChildren().addAll(t, val);

        return v;
    }

    /* ================= EVENT CARD ================= */

    private VBox eventCard(
        String name,
        String date,
        String time,
        String location,
        String customer
    ) {

        VBox box = new VBox(13);
        box.setPadding(new Insets(18));
        box.setMinHeight(230);
        box.setMaxWidth(Double.MAX_VALUE);

        box.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:#FF3B3B;" +
            "-fx-border-width:2;" +
            "-fx-border-radius:10;" +
            "-fx-background-radius:10;"
        );

        HBox top = new HBox();

        Label title = new Label(name);
        title.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        title.setTextFill(Color.web(NAVY));

        Label type = new Label("Corporate");
        type.setPadding(new Insets(5, 8, 5, 8));
        type.setStyle(
            "-fx-background-color:#E8EDF1;" +
            "-fx-background-radius:5;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(title, spacer, type);

        Label d = new Label("▣  " + date);
        Label t = new Label("◷  " + time);
        Label loc = new Label("⌖  " + location);
        Label cus = new Label("▦  " + customer);

        t.setTextFill(Color.RED);

        Separator sep = new Separator();

        HBox view = new HBox();
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);

        Label viewText = new Label("View Event ↗");
        viewText.setTextFill(Color.web(NAVY));

        view.getChildren().addAll(s, viewText);

        box.getChildren().addAll(
            top, d, t, loc, cus, sep, view
        );

        return box;
    }

    private Label bullet(String text) {

        Label l = new Label("○  " + text);
        l.setWrapText(true);
        l.setFont(Font.font("Arial", 14));

        return l;
    }

    private VBox activityItem(
        String time,
        String title,
        String description
    ) {

        VBox v = new VBox(3);

        Label t = new Label(time);
        t.setFont(Font.font("Arial", 12));

        Label h = new Label(title);
        h.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        h.setTextFill(Color.web(NAVY));

        Label d = new Label(description);
        d.setWrapText(true);
        d.setFont(Font.font("Arial", 12));

        v.getChildren().addAll(t, h, d);

        return v;
    }

    
}