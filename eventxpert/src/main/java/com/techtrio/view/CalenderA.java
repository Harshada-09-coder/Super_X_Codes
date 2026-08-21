package com.techtrio.view;
import com.techtrio.controller.NavigationManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CalenderA{
        private Scene calenderScene;


    final String PRIMARY="#002A60", SECONDARY="#8CAFC6", LIGHT="#C2E0F4";

     Label label(String s,int size,boolean bold){
        Label l=new Label(s);
        l.setFont(Font.font("Arial",bold?FontWeight.BOLD:FontWeight.NORMAL,size));
        return l;
    }

    Button button(String s,boolean primary){
        Button b=new Button(s);
        b.setFont(Font.font("Arial",FontWeight.BOLD,13));
        b.setPrefHeight(38);
        b.setStyle(primary
                ?"-fx-background-color:"+PRIMARY+";-fx-text-fill:white;-fx-background-radius:7;"
                :"-fx-background-color:white;-fx-text-fill:"+PRIMARY+
                 ";-fx-border-color:#B8C7D8;-fx-border-radius:7;");
        return b;
    }

    VBox summary(String title,String value,String icon,boolean danger){
        Label t=label(title,14,false);
        Label v=label(value,22,true);
        v.setTextFill(Color.web(danger?"#B3261E":PRIMARY));

        Label i=label(icon,18,true);
        i.setTextFill(Color.web(danger?"#B3261E":PRIMARY));

        StackPane iconBox=new StackPane(i);
        iconBox.setPrefSize(45,45);
        iconBox.setStyle("-fx-background-color:"+
                (danger?"#FFE4E1":"#DCECF8")+
                ";-fx-background-radius:25;");

        Region r=new Region();
        HBox.setHgrow(r,Priority.ALWAYS);

        HBox top=new HBox(t,r,iconBox);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox box=new VBox(8,top,v);
        box.setPadding(new Insets(15,18,12,18));
        box.setPrefHeight(105);
        box.setPrefWidth(245);
        box.setStyle("-fx-background-color:white;"+
                "-fx-border-color:"+
                (danger?"#F1B5B0":"#E0E5EA")+
                ";-fx-background-radius:8;-fx-border-radius:8;");
        return box;
    }

    Label event(String name,boolean conflict){
        Label e=label(name,11,false);
        e.setMaxWidth(Double.MAX_VALUE);
        e.setPadding(new Insets(4,6,4,6));
        e.setStyle("-fx-background-color:"+
                (conflict?"#FFE3E0":"#C2E0F4")+
                ";-fx-text-fill:"+
                (conflict?"#B3261E":"#486A80")+
                ";-fx-background-radius:4;");
        return e;
    }

    VBox day(String n){
        VBox box=new VBox(4);
        box.setPadding(new Insets(7));
        box.setPrefHeight(82);
        box.setStyle("-fx-background-color:white;-fx-border-color:#E1E6EC;");
        box.getChildren().add(label(n,13,false));
        return box;
    }

    HBox upcoming(String date,String name,String place){
        Label d=label(date,11,true);
        d.setTextFill(Color.web(PRIMARY));

        StackPane dateBox=new StackPane(d);
        dateBox.setPrefSize(48,48);
        dateBox.setStyle("-fx-background-color:#E8EDF2;-fx-background-radius:7;");

        VBox info=new VBox(
                label(name,14,true),
                label("⌖  "+place,12,false)
        );

        HBox row=new HBox(12,dateBox,info);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }




        public Scene getcalenderScene(){

        VBox sidebar=new VBox(7);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25,18,18,18));
        sidebar.setStyle("-fx-background-color:"+PRIMARY+";");

        Label logo=label("EventXpert",26,true);
        Label admin=label(" ADMIN DASHBOARD",12,true);
        logo.setTextFill(Color.WHITE);
        admin.setTextFill(Color.WHITE);

        VBox logoBox=new VBox(1,logo,admin);
        VBox menu=new VBox(7);

        String[] menuItems={
                "Dashboard","Event Requests","Events","Calendar",
                "Customers","Organizers","Vendors",
                "Conflict Detection","Reports & Analytics"
        };

        for(String item:menuItems){
            Label m = label(item,14,true);
            m.setTextFill(Color.WHITE);
            m.setMaxWidth(Double.MAX_VALUE);
            m.setPadding(new Insets(9,13,9,13));

            if(item.equals("Calendar"))
                m.setStyle("-fx-background-color:#07539A;-fx-background-radius:5;");

             m.setOnMouseClicked(e -> {

        Stage stage =(Stage) m.getScene().getWindow();

        NavigationManager.navigate(item, stage);
    });


            menu.getChildren().add(m);
        }

        Region sideSpace=new Region();
        VBox.setVgrow(sideSpace,Priority.ALWAYS);

        Label notifications=label("🔔  Notifications",13,true);
        Label settings=label("⚙  Settings",13,true);
        Label profile=label("👤  Profile",13,true);
        Label support=label("?  Support",13,true);

        for(Label l:new Label[]{
                notifications,settings,profile,support}){
            l.setTextFill(Color.WHITE);
            l.setPadding(new Insets(7,10,7,10));
        }

        sidebar.getChildren().addAll(
                logoBox,menu,sideSpace,
                notifications,settings,profile,support
        );

        /* ================= TOP BAR ================= */

        TextField search=new TextField();
        search.setPromptText("Search events...");
        search.setPrefWidth(465);
        search.setPrefHeight(38);

        Region headerSpace=new Region();
        HBox.setHgrow(headerSpace,Priority.ALWAYS);

        HBox header=new HBox(
                search,headerSpace,
                label("🔔",18,false),
                label("?",18,true),
                label("⚙",18,false),
                label("👤",18,false)
        );

        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(18);
        header.setPadding(new Insets(10,25,10,25));
        header.setPrefHeight(65);
        header.setStyle("-fx-background-color:white;"+
                "-fx-border-color:#E5EAF0;");

        /* ================= TITLE ================= */

        Label title=label("Calendar",30,true);
        title.setTextFill(Color.web(PRIMARY));

        Label subtitle=label(
                "View and manage all events, bookings and schedules.",
                15,false
        );

        VBox heading=new VBox(3,title,subtitle);

        Button create=button("+  Create Event",true);
        create.setPrefWidth(145);

        Region titleSpace=new Region();
        HBox.setHgrow(titleSpace,Priority.ALWAYS);

        HBox titleRow=new HBox(
                heading,titleSpace,create
        );
        titleRow.setAlignment(Pos.CENTER_LEFT);

        /* ================= SUMMARY CARDS ================= */

        HBox cards=new HBox(
                20,
                summary("Today","4 Events","▣",false),
                summary("This Week","12 Events","▣",false),
                summary("Upcoming","18 Events","→",false),
                summary("Conflicts","1 Detected","⚠",true)
        );

        /* ================= CALENDAR TOOLBAR ================= */

        Button today=button("Today",false);
        Button previous=button("‹",false);
        Button next=button("›",false);

        today.setPrefWidth(80);
        previous.setPrefWidth(42);
        next.setPrefWidth(42);

        Label month=label("August 2026",20,false);

        Region toolSpace=new Region();
        HBox.setHgrow(toolSpace,Priority.ALWAYS);

        ToggleButton monthBtn=new ToggleButton("Month");
       // ToggleButton weekBtn=new ToggleButton("Week");
        ToggleButton dayBtn=new ToggleButton("Day");

        ToggleGroup group=new ToggleGroup();
        monthBtn.setToggleGroup(group);
       // weekBtn.setToggleGroup(group);
        dayBtn.setToggleGroup(group);
        monthBtn.setSelected(true);

        HBox views=new HBox(monthBtn,/*weekBtn,*/dayBtn);
        views.setSpacing(2);
        views.setPadding(new Insets(4));
        views.setStyle("-fx-background-color:#EEF1F5;-fx-background-radius:7;");

        HBox toolbar=new HBox(
                today,previous,next,month,toolSpace,views
        );
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(11));

        /* ================= CALENDAR ================= */

        GridPane calendar=new GridPane();
        calendar.setPrefWidth(700);
        calendar.setPrefHeight(500);

        String[] week={
                "Sun","Mon","Tue","Wed","Thu","Fri","Sat"
        };

        for(int i=0;i<7;i++){
            Label d=label(week[i],13,false);
            d.setAlignment(Pos.CENTER);
            d.setMaxWidth(Double.MAX_VALUE);
            d.setPadding(new Insets(8));
            d.setStyle("-fx-background-color:#F8FAFC;"+
                    "-fx-border-color:#E1E6EC;");
            calendar.add(d,i,0);
        }

        String[][] dates={
                {"26","27","28","29","30","31","1"},
                {"2","3","4","5","6","7","8"},
                {"9","10","11","12","13","14","15"},
                {"16","17","18","19","20","21","22"},
                {"23","24","25","26","27","28","29"},
                {"30","31","1","2","3","4","5"}
        };

        for(int r=0;r<6;r++){
            for(int c=0;c<7;c++){

                VBox cell=day(dates[r][c]);

                if(r==3&&c==2)
                    cell.getChildren().add(
                            event("College...",false));

                if(r==3&&c==6)
                    cell.getChildren().add(
                            event("Birthday...",false));

                if(r==4&&c==2)
                    cell.getChildren().add(
                            event("Corporate...",false));

                if(r==4&&c==4){
                    cell.getChildren().add(
                            event("Birthday...",false));
                    cell.getChildren().add(
                            event("Conflict...",true));
                }

                calendar.add(cell,c,r+1);
            }
        }

        for(int i=0;i<7;i++)
            calendar.getColumnConstraints().add(
                    new ColumnConstraints(100,100,Double.MAX_VALUE)
            );

        VBox calendarCard=new VBox(toolbar,calendar);
        calendarCard.setStyle("-fx-background-color:white;"+
                "-fx-border-color:#E1E6EC;"+
                "-fx-background-radius:8;"+
                "-fx-border-radius:8;");

        /* ================= LEGEND ================= */

       /*  VBox legend=new VBox(
                13,
                label("Legend",20,true),
                label("●  Upcoming",14,false),
                label("●  Ongoing",14,false),
                label("●  Completed",14,false),
                label("○  Conflict",14,false)
        );

        legend.setPadding(new Insets(20));
        legend.setPrefWidth(320);
        legend.setStyle("-fx-background-color:white;"+
                "-fx-border-color:#E1E6EC;"+
                "-fx-background-radius:8;"+
                "-fx-border-radius:8;");

        */

        /* ================= UPCOMING ================= */

        HBox upcomingTitle=new HBox(
                label("Upcoming Events",20,true),
                new Region(),
                label("View All",13,true)
        );
        HBox.setHgrow(upcomingTitle.getChildren().get(1),Priority.ALWAYS);

        VBox upcomingBox=new VBox(
                16,
                upcomingTitle,
                upcoming("AUG\n18","College Seminar","Pune"),
                upcoming("AUG\n27","Birthday Celebration","Nashik"),
                upcoming("AUG\n25","Corporate Workshop","Mumbai"),
                upcoming("SEP\n02","Family Reunion","Pune")
        );

        upcomingBox.setPadding(new Insets(20));
        upcomingBox.setPrefWidth(320);
        upcomingBox.setStyle("-fx-background-color:white;"+
                "-fx-border-color:#E1E6EC;"+
                "-fx-background-radius:8;"+
                "-fx-border-radius:8;");

        VBox right=new VBox(20,upcomingBox);
        right.setPrefWidth(320);

        HBox calendarArea=new HBox(
                20,calendarCard,right
        );
        HBox.setHgrow(calendarCard,Priority.ALWAYS);

        /* ================= CONTENT ================= */

        VBox content=new VBox(
                14,titleRow,cards,calendarArea
        );

        content.setPadding(new Insets(15,25,20,25));
        content.setStyle("-fx-background-color:#F7F9FC;");

        ScrollPane scroll=new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#F7F9FC;"+
                "-fx-border-color:transparent;");

        VBox center=new VBox(header,scroll);

        /* ================= ROOT ================= */

        BorderPane root=new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene=new Scene(root,1550,750);
        calenderScene = scene;

        return calenderScene;
    }
    
    }
    




      
  