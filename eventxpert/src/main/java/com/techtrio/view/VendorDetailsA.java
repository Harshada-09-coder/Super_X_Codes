package com.techtrio.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VendorDetailsA extends Application {

    final String PRIMARY="#002A60", SECONDARY="#8CAFC6",LIGHT="#C2E0F4", BG="#F6F9FC";

    Label lbl(String t,int s,boolean b){
        Label l=new Label(t);
        l.setFont(Font.font("Arial",s));
        l.setTextFill(Color.web(PRIMARY));
        if(b) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    VBox card(){
        VBox v=new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;"+
                "-fx-border-radius:8;-fx-background-radius:8;");
        return v;
    }

    Button btn(String t){
        Button b=new Button(t);
        b.setPadding(new Insets(9,15,9,15));
        b.setStyle("-fx-background-color:"+PRIMARY+
                ";-fx-text-fill:white;-fx-font-weight:bold;"+
                "-fx-background-radius:6;");
        return b;
    }

    /* ================= SIDEBAR ================= */

    VBox sidebar(){
        VBox side=new VBox(7);
        side.setPrefWidth(285);
        side.setPadding(new Insets(24,12,15,12));
        side.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=lbl("EventXpert",30,true);
        logo.setTextFill(Color.WHITE);

        Label admin=new Label("ADMIN DASHBOARD");
        admin.setTextFill(Color.WHITE);
        admin.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        VBox head=new VBox(2,logo,admin);
        head.setPadding(new Insets(0,12,18,12));

        String[] items={
            "Dashboard","Event Requests","Events","Calendar","Customers",
            "Organizers","Vendors","Conflict Detection","Reports & Analytics"
        };

        VBox menu=new VBox(5);

        for(String s:items){
            Label l=new Label(s);
            l.setMaxWidth(Double.MAX_VALUE);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(9,14,9,14));

            if(s.equals("Vendors"))
                l.setStyle("-fx-background-color:#1264A3;"+
                        "-fx-background-radius:6;-fx-font-weight:bold;");

            menu.getChildren().add(l);
        }

        Region gap=new Region();
        VBox.setVgrow(gap,Priority.ALWAYS);

        Label n=new Label("🔔  Notifications");
        Label se=new Label("⚙  Settings");
        Label p=new Label("👤  Profile");
        Label sp=new Label("?  Support");

        for(Label l:new Label[]{n,se,p,sp}){
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(7,14,7,14));
        }

        side.getChildren().addAll(
                head,menu,gap,n,se,p,sp
        );

        return side;
    }

    /* ================= VENDOR PROFILE ================= */

    VBox vendorProfile(){
        VBox v=card();
        v.setPrefWidth(320);
        v.setAlignment(Pos.CENTER);

        Label avatar=lbl("VA",28,false);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(95,95);
        avatar.setStyle("-fx-background-color:#E2E6EA;"+
                "-fx-background-radius:60;");

        Label name=lbl("Vision AV Solutions",20,true);
        Label id=lbl("ID: VEN-002",14,false);

        Label type=new Label("Audio-Visual");
        type.setPadding(new Insets(6,12,6,12));
        type.setStyle("-fx-background-color:#E2E6EA;-fx-background-radius:15;");

        Label active=new Label("● Active");
        active.setTextFill(Color.web(PRIMARY));
        active.setPadding(new Insets(6,12,6,12));
        active.setStyle("-fx-background-color:"+LIGHT+
                ";-fx-background-radius:15;-fx-font-weight:bold;");

        HBox tags=new HBox(8,type,active);
        tags.setAlignment(Pos.CENTER);

        v.getChildren().addAll(
                avatar,name,id,tags,new Separator(),
                lbl("REGISTERED ON",12,true),
                lbl("Aug 13, 2026",14,false)
        );

        return v;
    }

    /* ================= CONTACT ================= */

    VBox contact(){
        VBox v=card();
        v.setPrefWidth(320);

        v.getChildren().addAll(
                lbl("Contact Information",20,true),
                new Separator(),
                lbl("✉  Email\n    visionav@example.com",14,false),
                lbl("☎  Mobile\n    +91 98234 56789",14,false),
                lbl("⌖  Address\n    Pune, Maharashtra",14,false),
                lbl("◉  Website\n    www.visionavsolutions.com",14,false)
        );

        return v;
    }

    /* ================= STAT ================= */

    VBox stat(String title,String value){
        VBox v=card();
        v.setPrefHeight(92);
        v.getChildren().addAll(
                lbl(title,12,true),
                lbl(value,29,true)
        );
        return v;
    }

    /* ================= SERVICE DETAILS ================= */

    VBox service(){
        VBox v=card();

        v.getChildren().addAll(
                lbl("Service Details",20,true),
                new Separator()
        );

        GridPane g=new GridPane();
        g.setHgap(55);
        g.setVgap(13);

        addInfo(g,"Type","Audio-Visual",0,0);
        addInfo(g,"Category","Corporate Event AV",1,0);
        addInfo(g,"Price Range","₹25k – ₹80k / event",0,1);
        addInfo(g,"Capacity","Up to 500 Guests",1,1);
        addInfo(g,"Service Area","Pune, Mumbai, Nashik",0,2);

        VBox services=new VBox(
                5,
                lbl("Services Provided",12,false),
                new HBox(6,tag("Projector"),
                        tag("LED Display"),
                        tag("Sound System")),
                new HBox(6,tag("Wireless Microphones"))
        );

        g.add(services,1,2);
        v.getChildren().add(g);

        return v;
    }

    void addInfo(GridPane g,String a,String b,int c,int r){
        g.add(new VBox(2,
                lbl(a,12,false),
                lbl(b,14,true)),c,r);
    }

    Label tag(String t){
        Label l=new Label(t);
        l.setPadding(new Insets(5,8,5,8));
        l.setStyle("-fx-background-color:#E9EDF0;"+
                "-fx-background-radius:6;");
        return l;
    }

    /* ================= AVAILABILITY ================= */

    VBox availability(){
        VBox v=card();

        v.getChildren().addAll(
                lbl("Availability & Workload",20,true),
                new Separator(),
                infoRow("Current Status","● Available"),
                infoRow("Next Available","Aug 30, 2026")
        );

        HBox stats=new HBox(
                10,
                smallStat("2","Active Events"),
                smallStat("1","Upcoming Bookings")
        );

        v.getChildren().add(stats);

        Label rating=new Label(
                "Vendor Rating     4.6 ★   (28 reviews)"
        );
        rating.setPadding(new Insets(8));
        rating.setStyle("-fx-background-color:#EEF1F3;"+
                "-fx-background-radius:6;");

        v.getChildren().add(rating);
        return v;
    }

    HBox infoRow(String a,String b){
        Label x=lbl(a,13,false);
        Label y=lbl(b,14,true);

        Region gap=new Region();
        HBox.setHgrow(gap,Priority.ALWAYS);

        HBox h=new HBox(x,gap,y);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    VBox smallStat(String value,String title){
        VBox v=card();
        v.setAlignment(Pos.CENTER);
        v.setPrefHeight(80);
        v.getChildren().addAll(
                lbl(value,25,true),
                lbl(title,13,false)
        );
        return v;
    }

    /* ================= EVENT HISTORY ================= */

    VBox history(){
        VBox v=card();

        HBox head=new HBox();
        Label title=lbl("Event History",20,true);
       
        //all.setTextFill(Color.web(PRIMARY));

        Region gap=new Region();
        HBox.setHgrow(gap,Priority.ALWAYS);

        head.getChildren().addAll(title,gap);

        v.getChildren().add(head);

        GridPane g=new GridPane();
        g.setHgap(25);
        g.setVgap(16);

        String[] h={
                "EVENT NAME","DATE","CUSTOMER","LOCATION","STATUS"
        };

        for(int i=0;i<h.length;i++)
            g.add(lbl(h[i],12,true),i,0);

        addEvent(g,1,"Corporate Seminar","Aug 18, 2026",
                "Incubators Pvt.","Pune","Upcoming");

        addEvent(g,2,"Corporate Workshop","Aug 25, 2026",
                "ABC Corporation","Mumbai","Upcoming");

        addEvent(g,3,"Annual Business Meet","Jul 27, 2026",
                "XYZ Corporation","Pune","Completed");

        v.getChildren().add(g);
        return v;
    }

    void addEvent(GridPane g,int r,String event,String date,
                  String customer,String location,String status){

        g.add(lbl(event,14,false),0,r);
        g.add(lbl(date,14,false),1,r);
        g.add(lbl(customer,14,false),2,r);
        g.add(lbl(location,14,false),3,r);

        Label s=new Label(status);
        s.setPadding(new Insets(5,10,5,10));
        s.setStyle("-fx-background-color:"+
                (status.equals("Upcoming")?LIGHT:"#E2E6EA")+
                ";-fx-background-radius:15;");

        g.add(s,4,r);
    }

    /* ================= MAIN ================= */

    @Override
    public void start(Stage stage){

        BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");

        // Sidebar starts directly from top
        root.setLeft(sidebar());

        VBox content=new VBox(16);
        content.setPadding(new Insets(20,25,25,25));

        Button back=btn("←  Back to Vendors");

        Label title=lbl("Vendor Details",32,true);
        Label sub=lbl(
                "View vendor information, services, performance and event history.",
                16,false
        );

        VBox heading=new VBox(8,back,title,sub);

        VBox left=new VBox(
                16,
                vendorProfile(),
                contact()
        );
        left.setPrefWidth(320);

        HBox stats=new HBox(
                14,
                stat("TOTAL EVENTS SERVED","36"),
                stat("COMPLETED","29"),
                stat("UPCOMING","5"),
                stat("CANCELLED","2")
        );

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        HBox middle=new HBox(
                16,
                service(),
                availability()
        );

        VBox right=new VBox(
                16,
                stats,
                middle,
                history()
        );

        HBox.setHgrow(right,Priority.ALWAYS);

        HBox main=new HBox(20,left,right);
        HBox.setHgrow(right,Priority.ALWAYS);

        content.getChildren().addAll(
                heading,
                main
        );

        ScrollPane scroll=new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scroll.setStyle(
                "-fx-background-color:"+BG+
                ";-fx-border-color:transparent;"
        );

        root.setCenter(scroll);

        Scene scene=new Scene(root,1550,750);

        stage.setTitle("EventXpert - Vendor Details");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}