package com.techtrio.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class ConflictDetectionA extends Application {

    final String PRIMARY="#002A60", SECONDARY="#8CAFC6",LIGHT="#C2E0F4", BG="#F6F9FC";

    TableView<Conflict> table=new TableView<>();
    TextField search=new TextField();

    Label label(String text,int size,boolean bold){
        Label l=new Label(text);
        l.setFont(Font.font("Arial",size));
        l.setTextFill(Color.web(PRIMARY));
        if(bold) l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    Button button(String text){
        Button b=new Button(text);
        b.setPadding(new Insets(8,14,8,14));
        b.setStyle("-fx-background-color:white;-fx-text-fill:"+PRIMARY+
                ";-fx-border-color:"+SECONDARY+";-fx-border-radius:6;"+
                "-fx-background-radius:6;-fx-font-weight:bold;");
        return b;
    }

    VBox card(){
        VBox v=new VBox(8);
        v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:white;-fx-border-color:#D9E1EA;"+
                "-fx-border-radius:8;-fx-background-radius:8;");
        return v;
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

        String[] items={"Dashboard","Event Requests","Events","Calendar",
                "Customers","Organizers","Vendors","Conflict Detection",
                "Reports & Analytics"};

        VBox menu=new VBox(5);

        for(String s:items){
            Label l=new Label(s);
            l.setMaxWidth(Double.MAX_VALUE);
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(9,14,9,14));

            if(s.equals("Conflict Detection"))
                l.setStyle("-fx-background-color:#1264A3;"+
                        "-fx-background-radius:6;-fx-font-weight:bold;");

            menu.getChildren().add(l);
        }

        Region gap=new Region();
        VBox.setVgrow(gap,Priority.ALWAYS);

        Label n=new Label("🔔  Notifications");
        Label set=new Label("⚙  Settings");
        Label p=new Label("👤  Profile");
        Label s=new Label("?  Support");

        for(Label l:new Label[]{n,set,p,s}){
            l.setTextFill(Color.WHITE);
            l.setFont(Font.font("Arial",17));
            l.setPadding(new Insets(7,14,7,14));
        }

        side.getChildren().addAll(head,menu,gap,n,set,p,s);
        side.setCursor(javafx.scene.Cursor.HAND);
        return side;
    }

    /* ================= STAT CARD ================= */

    VBox stat(String title,String value,String sub){
        VBox v=card();
        v.setPrefHeight(120);
        v.getChildren().addAll(
                label(title,14,true),
                label(value,34,true),
                label(sub,13,false)
        );
        return v;
    }

    /* ================= TABLE ================= */

    void createTable(){

        TableColumn<Conflict,String> c1=col("Conflict",Conflict::getConflict);
        TableColumn<Conflict,String> c2=col("Type",Conflict::getType);
        TableColumn<Conflict,String> c3=col("Events",Conflict::getEvents);
        TableColumn<Conflict,String> c4=col("Date",Conflict::getDate);
        TableColumn<Conflict,String> c5=col("Resource",Conflict::getResource);
        TableColumn<Conflict,String> c6=col("Severity",Conflict::getSeverity);
        TableColumn<Conflict,String> c7=col("Status",Conflict::getStatus);

        TableColumn<Conflict,String> action=new TableColumn<>("Actions");

        action.setCellFactory(c->new TableCell<>(){

            Button review=button("Review");

            {
                review.setOnAction(e->{
                    Conflict x=getTableView().getItems().get(getIndex());
                    new Alert(Alert.AlertType.INFORMATION,
                            "Conflict: "+x.getConflict()).showAndWait();
                });
            }

            @Override
            protected void updateItem(String x,boolean empty){
                super.updateItem(x,empty);
                setGraphic(empty?null:review);
            }
        });

        table.getColumns().addAll(c1,c2,c3,c4,c5,c6,c7,action);
        table.setItems(data);
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    TableColumn<Conflict,String> col(String title,
            java.util.function.Function<Conflict,String> f){

        TableColumn<Conflict,String> c=new TableColumn<>(title);
        c.setCellValueFactory(x->
                new javafx.beans.property.SimpleStringProperty(
                        f.apply(x.getValue())));
        return c;
    }

    /* ================= SEARCH ================= */

    void searchData(){

        String q=search.getText().toLowerCase().trim();

        table.setItems(data.filtered(x->
                q.isEmpty() ||
                x.getConflict().toLowerCase().contains(q) ||
                x.getType().toLowerCase().contains(q) ||
                x.getResource().toLowerCase().contains(q)));
    }

    ObservableList<Conflict> data=FXCollections.observableArrayList(
        new Conflict("Organizer double booking","Organizer",
                "Corporate Seminar\nCorporate Workshop",
                "18 Aug 2026","Elite Event Planners","Critical","Open"),

        new Conflict("Vendor double booking","Vendor",
                "Corporate Seminar\nCollege Seminar",
                "18 Aug 2026","Vision AV Solutions","High","Open"),

        new Conflict("Venue overlap","Venue",
                "Birthday Celebration\nCorporate Workshop",
                "25 Aug 2026","Hotel Tip Top","High","Resolving"),

        new Conflict("Schedule overlap","Schedule",
                "College Seminar\nAnnual Business Meet",
                "27 Aug 2026","10 AM - 12 PM","Medium","Open")
    );

    /* ================= MAIN ================= */

    @Override
    public void start(Stage stage){

        BorderPane root=new BorderPane();
        root.setStyle("-fx-background-color:"+BG+";");
        root.setLeft(sidebar());

        VBox main=new VBox(16);
        main.setPadding(new Insets(25));

        Label title=label("Conflict Detection",32,true);
        Label sub=label(
                "Identify and resolve scheduling, organizer, vendor and venue conflicts.",
                17,false);

        VBox heading=new VBox(4,title,sub);

        HBox stats=new HBox(
                15,
                stat("TOTAL CONFLICTS","12","All detected conflicts"),
                stat("CRITICAL CONFLICTS","3","Immediate attention"),
                stat("PENDING RESOLUTION","6","Needs admin action"),
                stat("RESOLVED","3","Successfully resolved")
        );

        for(javafx.scene.Node n:stats.getChildren())
            HBox.setHgrow(n,Priority.ALWAYS);

        /* SEARCH + FILTER */

        search.setPromptText("Search conflicts...");
        search.setPrefWidth(260);
        search.textProperty().addListener((a,b,c)->searchData());

        ComboBox<String> type=new ComboBox<>();
        type.getItems().addAll("All Types","Organizer","Vendor","Venue","Schedule");
        type.setValue("All Types");

        ComboBox<String> severity=new ComboBox<>();
        severity.getItems().addAll("All Severity","Critical","High","Medium");
        severity.setValue("All Severity");

        ComboBox<String> status=new ComboBox<>();
        status.getItems().addAll("All Status","Open","Resolving","Resolved");
        status.setValue("All Status");

        Button clear=button("Clear Filters");
        clear.setOnAction(e->{
            search.clear();
            type.setValue("All Types");
            severity.setValue("All Severity");
            status.setValue("All Status");
            table.setItems(data);
        });

        HBox filters=new HBox(
                10,search,type,severity,status,clear
        );
        filters.setPadding(new Insets(12));
        filters.setStyle("-fx-background-color:#EEF2F5;"+
                "-fx-background-radius:6;");

        VBox conflictCard=card();

        HBox heading2=new HBox();
        Label all=label("All Conflicts",21,true);

        Region space=new Region();
        HBox.setHgrow(space,Priority.ALWAYS);

        

        heading2.getChildren().addAll(all,space);

        createTable();

        HBox footer=new HBox();
       
        Region fspace=new Region();
        HBox.setHgrow(fspace,Priority.ALWAYS);

        Button previous=button("Previous");
        Button next=button("Next");

        HBox hb = new HBox(20,previous,next);

        footer.getChildren().addAll(hb,
            fspace,previous,next
        );

        conflictCard.getChildren().addAll(
                heading2,filters,table,new Separator(),footer
        );

        main.getChildren().addAll(
                heading,stats,conflictCard
        );

        ScrollPane scroll=new ScrollPane(main);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:"+BG+";");

        root.setCenter(scroll);

        Scene scene=new Scene(root,1550,750);
        stage.setTitle("EventXpert - Conflict Detection");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static class Conflict{

        String conflict,type,events,date,resource,severity,status;

        Conflict(String c,String t,String e,String d,
                 String r,String s,String st){
            conflict=c; type=t; events=e; date=d;
            resource=r; severity=s; status=st;
        }

        String getConflict(){return conflict;}
        String getType(){return type;}
        String getEvents(){return events;}
        String getDate(){return date;}
        String getResource(){return resource;}
        String getSeverity(){return severity;}
        String getStatus(){return status;}
    }

   
}

   