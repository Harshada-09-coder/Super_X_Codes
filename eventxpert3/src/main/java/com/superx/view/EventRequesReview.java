package com.superx.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EventRequesReview extends Application {

    private final String PRIMARY = "#002A60";
    private final String SECONDARY = "#8CAFC6";
    private final String LIGHT = "#C2E0F4";
   

    Label lbl(String t,int s,boolean b){
        Label l=new Label(t);
        l.setFont(Font.font("Arial",b?FontWeight.BOLD:FontWeight.NORMAL,s));
        return l;
    }

    VBox box(String t,String v){
        Label a=lbl(t.toUpperCase(),11,true),b=lbl(v,15,false);
        a.setTextFill(Color.web("#596273"));
        VBox x=new VBox(3,a,b);
        x.setPadding(new Insets(8));
        x.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"+
                   "-fx-border-radius:7;-fx-background-radius:7;");
        return x;
    }

    VBox section(String icon,String title){
        Label i=lbl(icon,18,true),t=lbl(title,19,true);
        i.setTextFill(Color.web(PRIMARY));t.setTextFill(Color.web(PRIMARY));
        HBox h=new HBox(9,i,t);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(10,18,8,18));
        return new VBox(h);
    }

    Button btn(String text,boolean primary){
        Button b=new Button(text);
        b.setPrefHeight(38);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setFont(Font.font("Arial",FontWeight.BOLD,13));
        b.setStyle(primary?
            "-fx-background-color:"+PRIMARY+";-fx-text-fill:white;-fx-background-radius:7;":
            "-fx-background-color:white;-fx-text-fill:#C62828;"+
            "-fx-border-color:#C62828;-fx-border-radius:7;");
        return b;
    }


    @Override
    public void start(Stage stage) throws Exception {

        VBox sidebar=new VBox(7);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25,18,18,18));
        sidebar.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=lbl("EventXpert",26,true);
        Label admin=lbl("ADMIN DASHBOARD",12,true);
        logo.setTextFill(Color.WHITE);
        admin.setTextFill(Color.WHITE);

        VBox logoBox=new VBox(1,logo,admin);
        VBox menu=new VBox(7);

        String[] items={"Dashboard","Event Requests","Events","Calendar","Customers","Organizers","Vendors","Conflict Detection", "Reports & Analytics"};

        for(String s:items){
            Label m=lbl(s,14,true);
            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9,13,9,13));
            if(s.equals("Event Requests"))
                m.setStyle("-fx-background-color:#07539A;-fx-background-radius:5;");
            menu.getChildren().add(m);
        }

        Region space=new Region();
        VBox.setVgrow(space,Priority.ALWAYS);

        Label n=lbl("🔔  Notifications",13,true);
        Label se=lbl("⚙  Settings",13,true);
        Label pr=lbl("👤  Profile",13,true);
        Label sp=lbl("?  Support",13,true);

        for(Label x:new Label[]{n,se,pr,sp}){
            x.setTextFill(Color.WHITE);
            x.setPadding(new Insets(7,10,7,10));
        }

        sidebar.getChildren().addAll(logoBox,menu,space,n,se,pr,sp);

        
        Region hs=new Region();
        HBox.setHgrow(hs,Priority.ALWAYS);

        Label bell=lbl("🔔",18,false);
        Label question=lbl("?",18,true);
        Label gear=lbl("⚙",18,false);
        Label user=lbl("👤",18,false);

        HBox header=new HBox(20,hs,bell,question,gear,user);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(8,25,8,20));
        header.setPrefHeight(55);
        header.setStyle("-fx-background-color:white;-fx-border-color:#E5EAF0;");

        
        Label title=lbl("Review Event Request",28,true);
        title.setTextFill(Color.web(PRIMARY));

        Label sub=lbl(
            "Review customer requirements before assigning a suitable organizer.",
            14,false);

        VBox titleBox=new VBox(2,title,sub);

        Label pending=lbl("● Pending Review",12,true);
        pending.setTextFill(Color.web(PRIMARY));
        pending.setPadding(new Insets(6,13,6,13));
        pending.setStyle("-fx-background-color:"+LIGHT+";-fx-border-color:"+SECONDARY+
                ";-fx-border-radius:15;-fx-background-radius:15;");

        Button back=new Button("←  Back to Requests");
        back.setPrefSize(145,36);
        back.setStyle("-fx-background-color:white;-fx-border-color:#8B949E;"+
                "-fx-border-radius:7;");

        back.setOnAction(e->alert("Navigation",
                "Back to Requests",
                "Return to Event Requests screen."));

        Region ts=new Region();
        HBox.setHgrow(ts,Priority.ALWAYS);

        HBox titleRow=new HBox(12,titleBox,pending,ts,back);
        titleRow.setAlignment(Pos.CENTER_LEFT);

       
        HBox summary=new HBox(
            summary("EVENT TYPE","Coporate Seminar"),
            summary("GUESTS","200 expected"),
            summary("VENUE","Hotel Tip Top"),
            summary("LOCATION","Pune"),
            summary("BUDGET","₹30,000"),
            summary("DATE","18 Aug 2026"));

        summary.setPadding(new Insets(6,12,6,12));
        summary.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"+"-fx-border-radius:9;-fx-background-radius:9;");

        VBox event=new VBox(section("","Event Details"));

        GridPane eg=new GridPane();
        eg.setHgap(15);eg.setVgap(10);
        eg.setPadding(new Insets(5,18,14,18));

        eg.add(box("Event Name","Coporate Seminar"),0,0);
        eg.add(box("Duration","4 Hours"),1,0);
        eg.add(box("Start Time","10:00 AM"),0,1);
        eg.add(box("Date","18 August 2026"),1,1);

        event.getChildren().add(eg);
        event.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"+
                "-fx-border-radius:9;-fx-background-radius:9;");

      
        VBox customer=new VBox(section("","Customer Information"));

        Label avatar=lbl("I",23,false);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(48,48);
        avatar.setStyle("-fx-background-color:#D7E1FF;-fx-background-radius:50;");

        VBox ct=new VBox(1,
            lbl("Incubators",18,true),
            lbl("Registered Client",13,false));

        HBox top=new HBox(12,avatar,ct);
        top.setAlignment(Pos.CENTER_LEFT);

        customer.getChildren().addAll(
            top,
            lbl("✉  incubators03@gmail.com",13,false),
            lbl("☎  +91 98765 43210",13,false));

        customer.setSpacing(9);
        customer.setPadding(new Insets(0,20,15,20));
        customer.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"+
                "-fx-border-radius:9;-fx-background-radius:9;");

        
        VBox req=new VBox(section("☷","Event Requirements"));

        GridPane rg=new GridPane();
        rg.setHgap(12);rg.setVgap(10);
        rg.setPadding(new Insets(5,18,14,18));

        rg.add(box("Theme Preference","Professional"),0,0);
        rg.add(box("Catering","Standard Lunch Buffet"),1,0);
        rg.add(box("Decoration","Corporate Stage Setup,"),0,1);
        rg.add(box("Photography","Event Coverage"),1,1);
        rg.add(box("Audio Visual", "Keynote Speaker System"),0,2,2,1);

        VBox special=box("Special Requirements",
            "Requires projector and high-speed Wi-Fi setup in the main hall. "+
            "Presenter requires a lapel microphone.");

        special.setStyle("-fx-background-color:"+LIGHT+";-fx-border-color:#9BD2F2;"+
                "-fx-border-radius:7;-fx-background-radius:7;");

        rg.add(special,0,3,2,1);
        req.getChildren().add(rg);

        req.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"+
                "-fx-border-radius:9;-fx-background-radius:9;");

        
        VBox review=new VBox(section("◉","Admin Review"));

        ComboBox<String> status=new ComboBox<>(
            FXCollections.observableArrayList(
                "Pending Review","Under Review","Approved","Rejected"));

        status.setValue("Review Status");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setPrefHeight(36);

        TextArea notes=new TextArea();
        notes.setPromptText(
            "Add notes regarding feasibility, budget concerns, "+
            "or specific organizer requirements...");
        notes.setWrapText(true);
        notes.setPrefRowCount(3);

        Button assign=btn("Assign Organizer",true);
        Button reject=btn("Reject Request",false);

        assign.setOnAction(e->alert(
            "Organizer Assignment",
            "Request Ready for Assignment",
            "Proceed to Organizer Assignment screen."));

        reject.setOnAction(e->{
            Alert a=new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Reject Request");
            a.setHeaderText("Reject Event Request?");
            a.setContentText("Are you sure you want to reject this request?");
            a.showAndWait().ifPresent(r->{
                if(r==ButtonType.OK)status.setValue("Rejected");
            });
        });

        VBox rb=new VBox(7,
            lbl("Request Status",12,true),status,
            lbl("Review Notes (Internal)",12,true),notes,
            assign,reject);

        rb.setPadding(new Insets(0,20,15,20));
        review.getChildren().add(rb);
        review.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"+
                "-fx-border-radius:9;-fx-background-radius:9;");

  
        VBox left=new VBox(12,event,req);
        VBox right=new VBox(12,customer,review);

        left.setPrefWidth(700);
        right.setPrefWidth(350);

        HBox body=new HBox(15,left,right);
        HBox.setHgrow(left,Priority.ALWAYS);

        VBox content=new VBox(10,titleRow,summary,body);
        content.setPadding(new Insets(10,25,15,25));
        content.setStyle("-fx-background-color:#F7F9FC;");

        // ================= CENTER =================
        VBox center=new VBox(header,content);
        VBox.setVgrow(content,Priority.ALWAYS);

        // ================= ROOT =================
        BorderPane root=new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene=new Scene(root,1550,750);
        stage.setTitle("EventXpert - Review Event Request");
        stage.setScene(scene);
        stage.setWidth(1550);
        stage.setHeight(750);
        stage.show();
    }

    VBox summary(String t,String v){
        Label a=lbl(t,11,true),b=lbl(v,16,false);
        a.setTextFill(Color.web("#596273"));
        VBox x=new VBox(2,a,b);
        x.setPadding(new Insets(5,22,5,10));
        HBox.setHgrow(x,Priority.ALWAYS);
        return x;
    }

    void alert(String t,String h,String c){
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);a.setHeaderText(h);a.setContentText(c);
        a.showAndWait();
    }

        
    }

    

        