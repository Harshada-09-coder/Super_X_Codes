package com.techtrio.view;

import com.techtrio.controller.NavigationManager;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class OrganizerDetailsA  {

    private Scene organizerDetailsScene;

    final String PRIMARY="#002A60", SECONDARY="#8CAFC6",
                 LIGHT="#C2E0F4", BG="#F6F9FC";

    Label label(String t,int s,boolean b){
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

    Button button(String t){
        Button b=new Button(t);
        b.setStyle("-fx-background-color:"+PRIMARY+";-fx-text-fill:white;"+
                   "-fx-font-weight:bold;-fx-background-radius:6;");
        b.setPadding(new Insets(9,15,9,15));
        return b;
    }

    /* ================= SIDEBAR ================= */

    VBox sidebar(){
        VBox side=new VBox(7);
        side.setPrefWidth(285);
        side.setPadding(new Insets(24,12,15,12));
        side.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=label("EventXpert",30,true);
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

        for(String x:items){
            Label l=new Label(x);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(9,14,9,14));
            l.setMaxWidth(Double.MAX_VALUE);

            if(x.equals("Organizers"))
                l.setStyle("-fx-background-color:#1264A3;"+
                           "-fx-background-radius:6;-fx-font-weight:bold;");

            menu.getChildren().add(l);
        }

        Region gap=new Region();
        VBox.setVgrow(gap,Priority.ALWAYS);

        Label notifications=new Label("🔔  Notifications");
        Label settings=new Label("⚙  Settings");
        Label profile=new Label("👤  Profile");
        Label support=new Label("?  Support");

        for(Label l:new Label[]{notifications,settings,profile,support}){
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(7,14,7,14));
        }

        side.getChildren().addAll(
            head,menu,gap,notifications,settings,profile,support
        );

        return side;
    }

    /* ================= ORGANIZER CARD ================= */

    VBox organizerCard(){
        VBox v=card();
        v.setPrefWidth(320);
        v.setAlignment(Pos.CENTER);

        Label avatar=label("EE",30,true);
        avatar.setTextFill(Color.WHITE);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(95,95);
        avatar.setStyle("-fx-background-color:"+PRIMARY+
                        ";-fx-background-radius:60;"+
                        "-fx-font-weight:bold;");

        Label name=label("Elite Event Planners",20,true);
        Label id=label("ID: ORG-001",14,false);

        Separator line=new Separator();

        Label status=new Label("● Active");
        status.setTextFill(Color.web(PRIMARY));
        status.setStyle("-fx-background-color:"+LIGHT+
                        ";-fx-background-radius:20;-fx-font-weight:bold;");
        status.setPadding(new Insets(7,16,7,16));

        Label registered=label("Registered: Aug 12, 2026",14,false);

        v.getChildren().addAll(
            avatar,name,id,line,status,registered
        );

        return v;
    }

    /* ================= CONTACT ================= */

    VBox contactCard(){
        VBox v=card();
        v.setPrefWidth(320);

        v.getChildren().addAll(
            label("Contact Information",20,true),
            new Separator(),
            label("✉  Email\n    elite@example.com",14,false),
            label("☎  Mobile\n    +91 98765 43210",14,false),
            label("⌖  Address\n    Pune, Maharashtra",14,false)
           
        );

        return v;
    }

    /* ================= STAT ================= */

    VBox stat(String title,String value){
        VBox v=card();
        v.setPrefHeight(92);
        v.getChildren().addAll(
            label(title.toUpperCase(),13,true),
            label(value,28,true)
        );
        return v;
    }

    /* ================= INFO ROW ================= */

    HBox info(String left,String right){
        Label a=label(left,14,false);
        Label b=label(right,14,true);

        Region r=new Region();
        HBox.setHgrow(r,Priority.ALWAYS);

        HBox h=new HBox(a,r,b);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    /* ================= EXPERIENCE ================= */

    VBox experience(){
        VBox v=card();

        v.getChildren().addAll(
            label("Experience & Performance",20,true),
            new Separator(),
            info("Experience","6 Years"),
            info("Rating","4.8 ☆"),
            label("Specialization",14,false)
        );

        HBox tags=new HBox(6);

        for(String s:new String[]{"Corporate","Educational","Birthday"}){
            Label l=new Label(s);
            l.setPadding(new Insets(5,9,5,9));
            l.setStyle("-fx-background-color:#E2E6EA;"+
                       "-fx-background-radius:15;");
            tags.getChildren().add(l);
        }

        v.getChildren().add(tags);
        return v;
    }

    /* ================= AVAILABILITY ================= */

    VBox availability(){
        VBox v=card();

        v.getChildren().addAll(
            label("Availability & Workload",20,true),
            new Separator(),
            info("Current Status","● Available"),
            info("Workload","2 Active Events"),
            info("Upcoming Assignments","1"),
            new Separator(),
            info("Next Available","Aug 30, 2026")
        );

        return v;
    }

    /* ================= STATUS ================= */

    Label status(String text,boolean active){
        Label l=new Label(text);
        l.setPadding(new Insets(6,12,6,12));
        l.setTextFill(Color.web(PRIMARY));
        l.setStyle("-fx-background-color:"+
                   (active?LIGHT:"#E2E6EA")+
                   ";-fx-background-radius:20;");
        return l;
    }

    /* ================= EVENT HISTORY ================= */

    VBox eventHistory(){
        VBox v=card();

        v.getChildren().add(label("Event History",20,true));

        GridPane g=new GridPane();
        g.setHgap(20);
        g.setVgap(18);

        String[] h={
            "EVENT NAME","TYPE","DATE",
            "CUSTOMER","LOCATION","STATUS"
        };

        for(int i=0;i<h.length;i++)
            g.add(label(h[i],12,true),i,0);

        g.add(label("Coporate\nSeminar",14,false),0,1);
        g.add(label("Coporate",14,false),1,1);
        g.add(label("Aug 18,\n2026",14,false),2,1);
        g.add(label("INCUBATORS",14,false),3,1);
        g.add(label("Pune",14,false),4,1);
        g.add(status("Upcoming",true),5,1);

        g.add(label("College\nWorkshop",14,false),0,2);
        g.add(label("Educational",14,false),1,2);
        g.add(label("Aug 25,\n2026",14,false),2,2);
        g.add(label("SKNSCOE\nEcell",14,false),3,2);
        g.add(label("Mumbai",14,false),4,2);
        g.add(status("Upcoming",true),5,2);

        g.add(label("Birthday\nCelebration",14,false),0,3);
        g.add(label("Birthday",14,false),1,3);
        g.add(label("Jul 27,\n2026",14,false),2,3);
        g.add(label("Rahul K.",14,false),3,3);
        g.add(label("Nashik",14,false),4,3);
        g.add(status("Completed",false),5,3);

        v.getChildren().add(g);
        return v;
    }

    /* ================= ACTIVITY ================= */

    VBox activity(){
        VBox v=card();

        v.getChildren().addAll(
            label("Recent Activity",20,true),
            new Separator(),
            label("•  Assigned to \"Coporate Seminar\"\n    2 days ago",14,false),
            label("•  Completed \"Birthday Celebration\"\n    Jul 28, 2026",14,false),
            label("•  Updated availability status\n    Jul 20, 2026",14,false)
        );

        return v;
    }

    /* ================= MAIN ================= */

    public Scene getorganizerDetailsScene(){

          BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");
        root.setLeft(sidebar());

        VBox content=new VBox(16);
        content.setPadding(new Insets(20,25,25,25));

        Button back=button("←  Back to Organizers");
        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();

            NavigationManager.navigate("Organizers", stage);
        });

        Label title=label("Organizer Details",35,true);
        Label sub=label(
            "View organizer information, experience, events and availability.",
            18,false
        );

        VBox heading=new VBox(8,back,title,sub);

        VBox left=new VBox(
            16,organizerCard(),contactCard()
        );
        left.setPrefWidth(320);

        HBox stats=new HBox(
            14,
            stat("Total Events","45"),
            stat("Completed","38"),
            stat("Upcoming","5"),
            stat("Cancelled","2")
        );
        stats.setCursor(javafx.scene.Cursor.HAND);

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        HBox details=new HBox(
            16,experience(),availability()
        );

        VBox right=new VBox(
            16,stats,details,eventHistory(),activity()
        );

        HBox.setHgrow(right,Priority.ALWAYS);

        HBox main=new HBox(20,left,right);
        HBox.setHgrow(right,Priority.ALWAYS);

        content.getChildren().addAll(heading,main);

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
        organizerDetailsScene = scene;
        return organizerDetailsScene;

    }

}


    

   

      
    

      