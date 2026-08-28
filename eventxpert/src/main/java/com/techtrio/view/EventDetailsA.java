package com.techtrio.view;

import com.techtrio.controller.NavigationManager;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class EventDetailsA {

    private Scene eventDetailsScene;


    private final String P = "#002A60";
    private final String BG = "#F7F9FC";
    private final String BORDER = "#CBD5E1";

    private Label label(String text, int size, boolean bold) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial",
                bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        return l;
    }

    private VBox info(String title, String value) {
        Label t = label(title, 12, false);
        t.setTextFill(Color.web("#596273"));
        return new VBox(4, t, label(value, 15, false));
    }

    private VBox card(String icon, String title, Node content) {

        Label i = label(icon, 19, true);
        i.setTextFill(Color.web(P));

        Label t = label(title, 19, true);
        t.setTextFill(Color.web(P));

        HBox head = new HBox(9, i, t);
        head.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(12, head, content);
        box.setPadding(new Insets(18));

        box.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-radius:9;" +
            "-fx-background-radius:9;"
        );

        return box;
    }

    private VBox requirement(String icon, String title, String value) {

        Label i = label(icon, 20, true);
        i.setTextFill(Color.web(P));

        Label t = label(title, 15, true);
        Label v = label(value, 14, false);
        v.setTextFill(Color.web("#596273"));

        VBox text = new VBox(4, t, v);
        HBox row = new HBox(12, i, text);

        VBox box = new VBox(row);
        box.setPadding(new Insets(16));
        box.setMinHeight(95);

        box.setStyle(
            "-fx-background-color:" + BG + ";" +
            "-fx-border-color:#DDE3E9;" +
            "-fx-border-radius:5;" +
            "-fx-background-radius:5;"
        );

        return box;
    }

    private VBox createSidebar() {

        VBox sidebar = new VBox(7);

        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);

        sidebar.setPadding(
            new Insets(25,18,18,18)
        );

        sidebar.setStyle(
            "-fx-background-color:" + P + ";"
        );

        Label logo = label("EventXpert",26,true);
        Label admin = label("ADMIN DASHBOARD",12,true);

        logo.setTextFill(Color.WHITE);
        admin.setTextFill(Color.WHITE);

        VBox logoBox = new VBox(1,logo,admin);
        VBox menu = new VBox(7);

        String[] items = {
            "Dashboard",
            "Event Requests",
            "Events",
            "Calendar",
            "Customers",
            "Organizers",
            "Vendors",
            "Conflict Detection",
            "Reports & Analytics"
        };

        for(String item : items) {

            Label m = label(item,14,true);

            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9,13,9,13));

            if(item.equals("Events")) {
                m.setStyle(
                    "-fx-background-color:#07539A;" +
                    "-fx-background-radius:5;"
                );
            }

            menu.getChildren().add(m);
        }

        Region space = new Region();
        VBox.setVgrow(space,Priority.ALWAYS);

        Label n = label("🔔  Notifications",13,true);
        Label s = label("⚙  Settings",13,true);
        Label p = label("👤  Profile",13,true);
        Label sp = label("?  Support",13,true);

        for(Label x : new Label[]{n,s,p,sp}) {
            x.setTextFill(Color.WHITE);
            x.setPadding(new Insets(7,10,7,10));
        }

        sidebar.getChildren().addAll(
            logoBox,menu,space,n,s,p,sp
        );

        return sidebar;
    }

    public  Scene geteventDetailsScene(){

         Region headerSpace = new Region();
        HBox.setHgrow(headerSpace,Priority.ALWAYS);

        HBox header = new HBox(
            20,
            headerSpace,
            label("🔔",18,false),
            label("?",18,true),
            label("⚙",18,false),
            label("👤",18,false)
        );

        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(8,25,8,20));
        header.setPrefHeight(55);

        header.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:#E5EAF0;"
        );

        // ================= TITLE =================

        Label title = label(
            "Event Details",30,true
        );

        title.setTextFill(Color.web(P));

        Label subtitle = label(
            "View complete information about the selected event.",
            15,false
        );

        VBox titleBox = new VBox(
            4,title,subtitle
        );

        Label status = label(
            "Upcoming",12,true
        );

        status.setTextFill(
            Color.web("#237A45")
        );

        status.setPadding(
            new Insets(6,14,6,14)
        );

        status.setStyle(
            "-fx-background-color:#DDF7E7;" +
            "-fx-background-radius:15;"
        );

        Button back = new Button("←  Back to Events");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Events", stage);
        });

        Button edit = new Button("Edit Event");

        back.setPrefHeight(38);
        edit.setPrefHeight(38);

        back.setStyle(
            "-fx-background-color:white;" +
            "-fx-border-color:" + P + ";" +
            "-fx-text-fill:" + P + ";" +
            "-fx-border-radius:7;" +
            "-fx-font-weight:bold;"
        );

        edit.setStyle(
            "-fx-background-color:" + P + ";" +
            "-fx-text-fill:white;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:7;"
        );

        Region titleSpace = new Region();
        HBox.setHgrow(
            titleSpace,Priority.ALWAYS
        );

        HBox titleRow = new HBox(
            12,
            titleBox,
            status,
            titleSpace,
            back,
            edit
        );

        titleRow.setAlignment(
            Pos.CENTER_LEFT
        );

        // ================= EVENT INFORMATION =================

        GridPane eventGrid = new GridPane();

        eventGrid.setHgap(40);
        eventGrid.setVgap(18);

        String[][] eventData = {
            {"Customer","SKNSCOE"},
            {"Event Name","College Seminar"},
            {"Event Type","Educational"},
            {"Event Date","18 Aug 2026"},
            {"Guests","200"},
            {"Location","Pune"},
            {"Organizer","Elite Event Planners"},
            {"Budget","₹40,000"},
            {"Status","Upcoming"}
        };

        for(int i=0;i<eventData.length;i++) {

            eventGrid.add(
                info(
                    eventData[i][0],
                    eventData[i][1]
                ),
                i % 3,
                i / 3
            );
        }

        VBox eventCard = card(
            "▣",
            "Event Information",
            eventGrid
        );

        // ================= LOCATION =================

        VBox locationContent = new VBox(
            10,
            info("Venue","College Seminar Hall"),
            info("City","Pune"),
            info("Address","Pune, Maharashtra")
        );

        VBox locationCard = card(
            "⌖",
            "Location",
            locationContent
        );

        HBox row1 = new HBox(
            25,
            eventCard,
            locationCard
        );

        HBox.setHgrow(
            eventCard,
            Priority.ALWAYS
        );

        // ================= CUSTOMER =================

        GridPane customerGrid = new GridPane();

          customerGrid.setOnMouseClicked(e -> {
            Stage stage = (Stage) customerGrid.getScene().getWindow();
            NavigationManager.navigate("Customer Details", stage);
        });

        customerGrid.setHgap(35);
        customerGrid.setVgap(14);

        customerGrid.add(
            info("Customer Name","SKNSCOE"),0,0
        );

        customerGrid.add(
            info(
                "Customer Type",
                "Educational Institution"
            ),1,0
        );

        customerGrid.add(
            info(
                "Email",
                "sknscoe@example.com"
            ),0,1
        );

        customerGrid.add(
            info(
                "Phone",
                "+91 98765 43210"
            ),1,1
        );

        VBox customerCard = card(
            "♙",
            "Customer Information",
            customerGrid
        );

        // ================= ORGANIZER =================

        GridPane organizerGrid = new GridPane();

        organizerGrid.setOnMouseClicked(e -> {
            Stage stage = (Stage) organizerGrid.getScene().getWindow();
            NavigationManager.navigate("Organizer Details", stage);
        });



        organizerGrid.setHgap(35);
        organizerGrid.setVgap(14);

        organizerGrid.add(
            info(
                "Organizer",
                "Elite Event Planners"
            ),0,0
        );

        organizerGrid.add(
            info("Status","Assigned"),1,0
        );

        organizerGrid.add(
            info(
                "Email",
                "eliteevents@example.com"
            ),0,1
        );

        organizerGrid.add(
            info(
                "Contact",
                "+91 98765 12345"
            ),1,1
        );

        VBox organizerCard = card(
            "♙",
            "Organizer Information",
            organizerGrid
        );

        HBox row2 = new HBox(
            25,
            customerCard,
            organizerCard
        );

        HBox.setHgrow(
            customerCard,
            Priority.ALWAYS
        );

        HBox.setHgrow(
            organizerCard,
            Priority.ALWAYS
        );

        // ================= REQUIREMENTS =================

        GridPane reqGrid = new GridPane();

        reqGrid.setHgap(25);
        reqGrid.setVgap(20);

        String[][] req = {
            {"▱","Seating Arrangement","Theatre Style"},
            {"♜","Catering","Standard Lunch Buffet"},
            {"✣","Decoration",
                "Educational / Professional Theme"},
            {"▣","Audio Visual",
                "Projector and Sound System"},
            {"▧","Photography","Event Coverage"},
            {"✪","Special Requirement",
                "High-speed Wi-Fi and presentation setup."}
        };

        for(int i=0;i<req.length;i++) {

            reqGrid.add(
                requirement(
                    req[i][0],
                    req[i][1],
                    req[i][2]
                ),
                i % 3,
                i / 3
            );
        }

        VBox requirementsCard = card(
            "▣",
            "Event Requirements",
            reqGrid
        );
        requirementsCard.setCursor(javafx.scene.Cursor.HAND);

        // ================= CONTENT =================

        VBox content = new VBox(
            18,
            titleRow,
            row1,
            row2,
            requirementsCard
        );

        content.setPadding(
            new Insets(18,25,25,25)
        );

        content.setStyle(
            "-fx-background-color:" + BG + ";"
        );

        // ================= SCROLL =================

        ScrollPane scroll = new ScrollPane(
            content
        );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
            "-fx-background-color:" + BG + ";"
        );

        // ================= ROOT =================

        VBox center = new VBox(
            header,
            scroll
        );

        VBox.setVgrow(
            scroll,
            Priority.ALWAYS
        );

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setCenter(center);

        Scene scene = new Scene(root,1550,750 );
        eventDetailsScene = scene;

         return eventDetailsScene;

       
    }

}

       

    

       