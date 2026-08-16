package com.superx.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class CustomerDetails extends Application {
final String PRIMARY="#002A60", SECONDARY="#8CAFC6",LIGHT="#C2E0F4", BG="#F6F9FC";

    Label label(String t,int s,boolean b){
        Label l=new Label(t);
        l.setFont(Font.font("Arial",s));
        l.setTextFill(Color.web(PRIMARY));
        if(b) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    Button btn(String t){
        Button b=new Button(t);
        b.setStyle("-fx-background-color:"+PRIMARY+";-fx-text-fill:white;"
                +"-fx-font-weight:bold;-fx-background-radius:6;");
        b.setPadding(new Insets(10,16,10,16));
        return b;
    }

    VBox card(){
        VBox v=new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;"
                +"-fx-border-radius:8;-fx-background-radius:8;");
        return v;

    }

    VBox sidebar(){
        VBox side=new VBox(7);
        side.setPadding(new Insets(24,12,15,12));
        side.setPrefWidth(285);
        side.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=label("EventXpert",30,true);
        logo.setTextFill(Color.WHITE);

        Label admin=new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head=new VBox(2,logo,admin);
        head.setPadding(new Insets(0,12,18,12));

        String[] items={"Dashboard","Event Requests","Events","Calendar","Customers",
                "Organizers","Vendors","Conflict Detection","Reports & Analytics"};

        VBox menu=new VBox(5);

        for(String x:items){
            Label l=new Label(x);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(9,14,9,14));
            l.setMaxWidth(Double.MAX_VALUE);

            if(x.equals("Customers"))
                l.setStyle("-fx-background-color:#1264A3;"
                        +"-fx-background-radius:6;-fx-font-weight:bold;");

        
                        menu.getChildren().add(l);
        }

        Region gap=new Region();
        VBox.setVgrow(gap,Priority.ALWAYS);

        Label notifications=new Label("🔔 Notifications");
        Label settings=new Label("⚙  Settings");
        Label profile=new Label("👤  Profile");
        Label support=new Label("?  Support");

        for(Label l:new Label[]{notifications,settings,profile,support}){
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(7,14,7,14));
        }

        side.getChildren().addAll(head,menu,gap,
                notifications,settings,profile,support);

        return side;
    }

    VBox profileCard(){
        VBox v=card();
        v.setAlignment(Pos.CENTER);

        Label avatar=label("C2W",27,true);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(95,95);
        avatar.setStyle("-fx-background-color:"+LIGHT+
                ";-fx-background-radius:60;-fx-font-weight:bold;");

        Label name=label("INCUBATORS Pvt.",22,true);
        Label id=label("ID: CUST-001",14,false);

        Label status=new Label("Active");
        status.setTextFill(Color.web(PRIMARY));
        status.setStyle("-fx-background-color:"+LIGHT+";-fx-background-radius:20;-fx-font-weight:bold;");
        status.setPadding(new Insets(8,20,8,20));

        Label reg=label("Registered: Aug 13, 2026",14,false);

        v.getChildren().addAll(avatar,name,id,status,reg);
        return v;
    }

    VBox contactCard(){
        VBox v=card();
        v.getChildren().addAll(
                label("Contact Information",20,true),
                new Separator(),
                label("✉  Email\n    incubators03@gmail.com",15,false),
                label("☎  Mobile\n    +91 98765 43210",15,false),
                label("⌖  Address\n    Pune, Maharashtra",15,false)
        );
        return v;
    }

    VBox stat(String t,String n){
        VBox v=card();
        v.setPrefHeight(105);
        v.getChildren().addAll(
                label(t.toUpperCase(),14,true),
                label(n,28,true)
        );
        return v;
    }

    VBox eventHistory(){
        VBox v=card();
        v.getChildren().add(label("Event History",21,true));

        GridPane g=new GridPane();
        g.setHgap(35);
        g.setVgap(20);

        String[] h={"EVENT NAME","TYPE","DATE","ORGANIZER","STATUS"};

        for(int i=0;i<h.length;i++)
            g.add(label(h[i],13,true),i,0);

        g.add(label("Coporate Seminar",15,false),0,1);
        g.add(label("Seminar",15,false),1,1);
        g.add(label("Aug 8, 2026",15,false),2,1);
        g.add(label("Dreams Events",15,false),3,1);
        g.add(status("Upcoming",false),4,1);

        g.add(label("Birthday Celebration",15,false),0,2);
        g.add(label("Birthday",15,false),1,2);
        g.add(label("July 27, 2026",15,false),2,2);
        g.add(label("Elite Events",15,false),3,2);
        g.add(status("Completed",true),4,2);

        v.getChildren().add(g);
        return v;
    }

    Label status(String t,boolean upcoming){
        Label l=new Label(t);
        l.setPadding(new Insets(7,14,7,14));
        l.setTextFill(Color.web(PRIMARY));
        l.setStyle("-fx-background-color:"+
                (upcoming?LIGHT:"#E2E6EA")+
                ";-fx-background-radius:20;");
        return l;
    }

    VBox requirements(){
        VBox v=card();

        v.getChildren().add(
                label("Event Requirements  (Coporate Seminar)",20,true)
        );

        GridPane g=new GridPane();
        g.setHgap(65);
        g.setVgap(15);

        g.add(label("Guests\n150",14,false),0,0);
        g.add(label("Venue\nConference Hall",14,false),1,0);
        g.add(label("Catering\nBuffet refreshments",14,false),0,1);
        g.add(label("Decoration\nStage + Basic",14,false),1,1);
        g.add(label("Photography\nRequired",14,false),0,2);
        g.add(label("Entertainment\nNot Required",14,false),1,2);
        g.add(label("Budget\n₹1,50,000",14,false),0,3);

        v.getChildren().add(g);

        Label special=label(
                "ⓘ Special Requirements\nProjector and professional sound system required.",
                14,false
        );

        special.setPadding(new Insets(10));
        special.setStyle("-fx-background-color:#EEF2F5;"+"-fx-background-radius:6;");

        v.getChildren().add(special);
        return v;
    }

    VBox activity(){
        VBox v=card();

        v.getChildren().add(label("Recent Activities",20,true));

        v.getChildren().addAll(
                label("✓  Payment completed\n    Today, 10:30 AM",14,true),
                label("□  Requirements submitted\n    Yesterday, 02:15 PM",14,true),
                label("✓  Booking confirmed\n    Aug 14, 2026, 11:00 AM",14,true),
                label("+  Event created\n    Aug 12, 2026, 09:45 AM",14,true)
        );

        return v;
    }

    @Override
    public void start(Stage stage){

        BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");

        root.setLeft(sidebar());

        VBox content=new VBox(16);
        content.setPadding(new Insets(20,25,25,25));

        Button back=btn("←  Back to Customers");

        Label title=label("Customer Details",34,true);

        Label sub=label(
                "View customer information, events and event requirements.",
                18,false
        );

        VBox heading=new VBox(8,back,title,sub);

        VBox left=new VBox(16,profileCard(),contactCard());
        left.setPrefWidth(330);

        HBox stats=new HBox(
                14,
                stat("Total Events","2"),
                stat("Upcoming","1"),
                stat("Completed","1"),
                stat("Cancelled","0")
        );

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        VBox right=new VBox(16);
        right.getChildren().addAll(stats,eventHistory());

        HBox bottom=new HBox(16,requirements(),activity());

        for(javafx.scene.Node n:bottom.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        right.getChildren().add(bottom);

        HBox main=new HBox(20,left,right);
        HBox.setHgrow(right,Priority.ALWAYS);

        content.getChildren().addAll(heading,main);

        ScrollPane scroll=new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:"+BG+";");

        root.setCenter(scroll);

        Scene scene=new Scene(root,1550,750);

        stage.setTitle("EventXpert - Customer Details");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.show();
    }
}