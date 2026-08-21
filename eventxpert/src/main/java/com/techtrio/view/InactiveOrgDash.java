package com.techtrio.view;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;


import javafx.application.Application;

public class InactiveOrgDash extends Application{


    private static final String NAVY = "#061F49";
    private static final String BLUE = "#082F70";
    private static final String BG = "#F6F8FC";
    private static final String BORDER = "#DDE2EA";
    private static final String TEXT = "#17191D";
    private static final String MUTED = "#737985";


    @Override
    public void start(Stage stage) throws Exception {


        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // ================= SIDEBAR =================
        VBox side = new VBox(7);
        side.setPrefWidth(235);
        side.setPadding(new Insets(20, 9, 16, 9));
        side.setStyle("-fx-background-color:" + NAVY + ";");

        HBox logo = new HBox(11);
        logo.setAlignment(Pos.CENTER_LEFT);

        Label ex = new Label("EX");
        ex.setMinSize(42,42);
        ex.setAlignment(Pos.CENTER);
        ex.setStyle("-fx-background-color:white;-fx-text-fill:" + NAVY +
                ";-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8px;");

        VBox brand = new VBox(1);
        brand.getChildren().addAll(
                txt("EventXpert",19,Color.WHITE,true),
                txt("ORGANIZER PORTAL",10,Color.web("#A9B7D0"),false)
        );
        logo.getChildren().addAll(ex,brand);

        side.getChildren().addAll(
                logo,
                gap(22),

                nav("▦   Dashboard",true),
                nav("▣   Event Requests",false),
                nav("▣   My Events",false),
                nav("⌖   Venue",false),
                nav("♧   Vendors",false),
                nav("▤   Schedule",false),

                gap(8),
                section("ACCOUNT"),

                nav("♧   Notifications",false),
                nav("♙   Profile & Settings",false),
                nav("?   Help & Support",false)
        );

        Region sideGrow = new Region();
        VBox.setVgrow(sideGrow,Priority.ALWAYS);

        Button newEvent = new Button("＋   New Event");
        newEvent.setMaxWidth(Double.MAX_VALUE);
        newEvent.setPrefHeight(47);
        newEvent.setStyle(
                "-fx-background-color:#7894B5;" +
                "-fx-text-fill:#071F45;" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:8px;"
        );

        side.getChildren().addAll(sideGrow,newEvent);
        root.setLeft(side);

        // ================= TOP BAR =================
        VBox main = new VBox();

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(8,20,8,22));
        top.setPrefHeight(58);
        top.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-width:0 0 1 0;"
        );

        TextField search = new TextField();
        search.setPromptText("Search events, vendors, guests...");
        search.setPrefWidth(500);
        search.setPrefHeight(40);
        search.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#D9DEE7;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-font-size:14px;"
        );

        Button calendar = topBtn("▣  View Calendar");
        calendar.setPrefWidth(145);

        Region topGrow = new Region();
        HBox.setHgrow(topGrow,Priority.ALWAYS);

        Button bell = topBtn("♧");
        Button help = topBtn("?");
        Button settings = topBtn("⚙");

        Label person = txt("♙  Profile",14,Color.web(TEXT),true);

        top.getChildren().addAll(
                search,calendar,topGrow,bell,help,settings,person
        );

        main.getChildren().add(top);

        // ================= CONTENT =================
        VBox content = new VBox(16);
        content.setPadding(new Insets(20,22,22,22));

        // Welcome
        HBox welcome = new HBox(12);
        welcome.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(3);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(
                txt("Good Morning, Sarah 👋",31,Color.web(TEXT),true),
                badge("●  INACTIVE")
        );

        welcomeText.getChildren().addAll(
                titleRow,
                txt("Your organizer account is currently inactive. Activate your account to start creating and managing events.",
                        14,Color.web("#343841"),false)
        );

        welcome.getChildren().add(welcomeText);

        // ================= INACTIVE CARD =================
        HBox inactive = new HBox(22);
        inactive.setAlignment(Pos.CENTER_LEFT);
        inactive.setPadding(new Insets(22));
        inactive.setMinHeight(140);
        inactive.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:#FFD0CC;" +
                "-fx-border-radius:11px;" +
                "-fx-background-radius:11px;"
        );

        StackPane warning = new StackPane(
                txt("⚠",27,Color.web("#B51D1D"),true)
        );
        warning.setMinSize(62,62);
        warning.setStyle(
                "-fx-background-color:#FFE1DE;" +
                "-fx-background-radius:50%;"
        );

        VBox inactiveInfo = new VBox(7);

        inactiveInfo.getChildren().addAll(
                txt("Organizer Account Inactive",24,Color.web(TEXT),true),
                txt("Your organizer account is currently inactive. Activate your account to create events, manage vendors, handle event requests, and access organizer services.",
                        14,Color.web("#343841"),false)
        );

        HBox inBtns = new HBox(10);
        inBtns.setPadding(new Insets(5,0,0,0));
        inBtns.getChildren().addAll(
                action("Activate Organizer Account  →",true),
                action("View Profile",false)
        );

        inactiveInfo.getChildren().add(inBtns);
        inactive.getChildren().addAll(warning,inactiveInfo);

        // ================= STATS =================
        HBox stats = new HBox(14);

        stats.getChildren().addAll(
                stat("Event Requests","00","No pending requests","▣"),
                stat("My Events","00","No active events","▣"),
                stat("Upcoming Events","00","No upcoming events","⌄"),
                stat("Vendors","00","No active vendors","♧"),
                stat("Tasks","00","No tasks due","✓")
        );

        // ================= LOWER =================
        HBox lower = new HBox(16);

        VBox upcoming = new VBox();
        upcoming.setStyle(card());
        HBox.setHgrow(upcoming,Priority.ALWAYS);

        Label uTitle = txt("Upcoming Events",19,Color.web(TEXT),true);
        uTitle.setPadding(new Insets(17,20,14,20));

        Separator separator = new Separator();

        VBox empty = new VBox(10);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(25));

        empty.getChildren().addAll(
                txt("▣",33,Color.web("#C1C6D0"),true),
                txt("No upcoming events",21,Color.web(TEXT),true),
                center(
                        "Your upcoming events will appear here after your organizer account is\n" +
                        "activated and events are created."
                ),
                action("Activate Account",false)
        );

        VBox.setVgrow(empty,Priority.ALWAYS);
        upcoming.getChildren().addAll(uTitle,separator,empty);

        // Right
        VBox right = new VBox(16);
        right.setPrefWidth(305);

        VBox tasks = new VBox(10);
        tasks.setPadding(new Insets(18));
        tasks.setStyle(card());

        tasks.getChildren().addAll(
                txt("Today's Tasks",19,Color.web(TEXT),true),
                taskEmpty()
        );

        VBox health = new VBox(13);
        health.setPadding(new Insets(18));
        health.setStyle(card());

        health.getChildren().addAll(
                txt("Global Event Health",19,Color.web(TEXT),true),
                progress("Budget Utilization","0%",0),
                progress("Vendor Status","Inactive",0),
                progress("Guest Check-In","0%",0)
        );

        right.getChildren().addAll(tasks,health);
        lower.getChildren().addAll(upcoming,right);

        content.getChildren().addAll(
                welcome,inactive,stats,lower
        );

        // ================= SCROLL =================
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle(
                "-fx-background-color:" + BG + ";" +
                "-fx-border-color:transparent;"
        );

        VBox.setVgrow(scroll,Priority.ALWAYS);
        main.getChildren().add(scroll);

        root.setCenter(main);

        // ================= STAGE =================
        Scene scene = new Scene(root,1550,750);

        stage.setTitle("EventXpert - Inactive Organizer Dashboard");
        stage.setScene(scene);
        stage.setWidth(1550);
        stage.setHeight(750);
        stage.setMaximized(true);
        stage.show();
    }

    // ================= HELPERS =================

    private Label txt(String s,double size,Color c,boolean bold){
        Label l=new Label(s);
        l.setFont(Font.font("Arial",
                bold ? FontWeight.BOLD : FontWeight.NORMAL,size));
        l.setTextFill(c);
        l.setWrapText(true);
        return l;
    }

    private Label center(String s){
        Label l=txt(s,14,Color.web("#30343B"),false);
        l.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private Label section(String s){
        Label l=txt(s,11,Color.web("#8396B7"),true);
        l.setPadding(new Insets(10,12,3,12));
        return l;
    }

    private Button nav(String s,boolean active){
        Button b=new Button(s);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(43);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(0,12,0,12));

        b.setStyle(
                "-fx-background-color:"+
                (active ? "#0A2C5D" : "transparent")+";"+
                "-fx-text-fill:"+
                (active ? "white" : "#AAB6CC")+";"+
                "-fx-font-size:14px;" +
                "-fx-background-radius:5px;"
        );
        return b;
    }

    private Button topBtn(String s){
        Button b=new Button(s);
        b.setPrefHeight(40);
        b.setStyle(
                "-fx-background-color:#F8F9FB;" +
                "-fx-border-color:#DDE2EA;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-font-size:14px;"
        );
        return b;
    }

    private Label badge(String s){
        Label l=txt(s,11,Color.web("#626872"),true);
        l.setStyle(
                "-fx-background-color:#E2E5E8;" +
                "-fx-background-radius:14px;" +
                "-fx-padding:5 12 5 12;"
        );
        return l;
    }

    private Button action(String s,boolean primary){
        Button b=new Button(s);
        b.setPrefHeight(40);
        b.setPadding(new Insets(0,17,0,17));

        b.setStyle(primary ?
                "-fx-background-color:"+BLUE+";" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:bold;" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:7px;"
                :
                "-fx-background-color:white;" +
                "-fx-text-fill:"+NAVY+";" +
                "-fx-border-color:#AAB2C0;" +
                "-fx-border-radius:7px;" +
                "-fx-background-radius:7px;" +
                "-fx-font-size:14px;"
        );
        return b;
    }

    private VBox stat(String title,String value,String sub,String icon){
        VBox b=new VBox(6);
        b.setPadding(new Insets(16));
        b.setPrefHeight(135);
        b.setStyle(card());
        HBox.setHgrow(b,Priority.ALWAYS);

        HBox top=new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label t=txt(title,14,Color.web(MUTED),false);
        Region r=new Region();
        HBox.setHgrow(r,Priority.ALWAYS);

        top.getChildren().addAll(
                t,r,txt(icon,18,Color.web("#9DA4B0"),false)
        );

        b.getChildren().addAll(
                top,
                txt(value,42,Color.web("#6F7378"),true),
                txt(sub,12,Color.web("#777D87"),false)
        );

        return b;
    }

    private VBox taskEmpty(){
        VBox b=new VBox(8);
        b.setAlignment(Pos.CENTER);
        b.setPadding(new Insets(25,15,28,15));

        b.getChildren().addAll(
                txt("☷",30,Color.web("#707784"),true),
                center(
                        "No tasks available. Tasks will appear\n" +
                        "here once your organizer account\n" +
                        "becomes active."
                )
        );
        return b;
    }

    private VBox progress(String name,String value,double amount){
        VBox b=new VBox(6);

        HBox row=new HBox();
        row.getChildren().add(txt(name,13,Color.web("#30343B"),false));

        Region r=new Region();
        HBox.setHgrow(r,Priority.ALWAYS);

        row.getChildren().addAll(r,
                txt(value,13,Color.web(TEXT),false));

        ProgressBar p=new ProgressBar(amount);
        p.setMaxWidth(Double.MAX_VALUE);
        p.setPrefHeight(7);

        b.getChildren().addAll(row,p);
        return b;
    }

    private String card(){
        return "-fx-background-color:white;" +
                "-fx-border-color:"+BORDER+";" +
                "-fx-border-radius:11px;" +
                "-fx-background-radius:11px;";
    }

    private Region gap(double h){
        Region r=new Region();
        r.setMinHeight(h);
        return r;
    }
}
       

