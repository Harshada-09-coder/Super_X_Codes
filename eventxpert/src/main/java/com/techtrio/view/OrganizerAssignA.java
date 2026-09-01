package com.techtrio.view;

import java.util.List;

import com.techtrio.controller.NavigationManager;
import com.techtrio.model.Event;
import com.techtrio.model.OrganizerProfile;
import com.techtrio.services.EventService;
import com.techtrio.services.OrganizerProfileService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
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

public class OrganizerAssignA {

    private Scene organizerAssignScene;

    public static Event currentSelectedEvent;

    private final EventService eventService = new EventService();
    private final OrganizerProfileService organizerService = new OrganizerProfileService();
    private OrganizerProfile selectedOrganizerObj = null;

    final String P="#002A60", S="#8CAFC6", L="#C2E0F4";

    Label lbl(String t,int s,boolean b){
        Label l=new Label(t != null ? t : "");
        l.setFont(Font.font("Arial",b?FontWeight.BOLD:FontWeight.NORMAL,s));
        return l;
    }

    VBox info(String title,String value){
        Label a=lbl(title.toUpperCase(),11,true),b=lbl(value,15,false);
        a.setTextFill(Color.web("#596273"));
        VBox v=new VBox(3,a,b);
        v.setPadding(new Insets(8));
        return v;
    }

    Button btn(String text,boolean primary){
        Button b=new Button(text);
        b.setPrefHeight(38);
        b.setFont(Font.font("Arial",FontWeight.BOLD,13));
        b.setStyle(primary
            ?"-fx-background-color:"+P+";-fx-text-fill:white;-fx-background-radius:7;"
            :"-fx-background-color:white;-fx-text-fill:"+P+";-fx-border-color:"+P+";-fx-border-radius:7;");
        return b;
    }

    VBox organizerCard(OrganizerProfile org, String exp, String rating, String events, String status, String score, boolean best, Label selected, String[] current){

        String name = (org.getOrganizationName() != null && !org.getOrganizationName().isBlank())
                ? org.getOrganizationName()
                : (org.getName() != null ? org.getName() : "Organizer");

        String location = (org.getAddress() != null && !org.getAddress().isBlank())
                ? org.getAddress()
                : "Pune";

        Label title=lbl(name,17,true);
        Label loc=lbl("⌖ "+location,12,false);
        Label match=lbl(score+"  Match Score",15,true);
        match.setTextFill(Color.web(P));
        match.setPadding(new Insets(7));
        match.setStyle("-fx-background-color:#EEF7FC;-fx-border-color:#D5E8F5;"
                +"-fx-background-radius:5;-fx-border-radius:5;");

        Label ex=lbl("Experience",13,false),ra=lbl("Rating",13,false);
        Label ev=lbl("Events Done",13,false),st=lbl("Status",13,false);
        Label exv=lbl(exp,13,true),rav=lbl(rating+" ★",13,true);
        Label evv=lbl(events,13,true),stv=lbl(status,12,true);

        stv.setTextFill(Color.web("#078A60"));
        stv.setPadding(new Insets(4,8,4,8));
        stv.setStyle("-fx-background-color:#E7F8F1;-fx-background-radius:5;");

        GridPane g=new GridPane();
        g.setHgap(20); g.setVgap(8);
        g.add(ex,0,0); g.add(exv,1,0);
        g.add(ra,0,1); g.add(rav,1,1);
        g.add(ev,0,2); g.add(evv,1,2);
        g.add(st,0,3); g.add(stv,1,3);

        Button details=btn("Details",false),select=btn("Select",best);
        
        select.setOnAction(e->{
            selected.setText("✓  SELECTED ORGANIZER\n"+name);
            current[0]=name;
            selectedOrganizerObj = org;
        });

        // 🟢 FIX: OrganizerDetailsA ला डेटा पास करून स्क्रीन उघडणे
        details.setOnAction(e -> {
            OrganizerDetailsA.selectedOrganizer = org;
            Stage stage = (Stage) details.getScene().getWindow();
            NavigationManager.navigate("Organizer Details", stage);
        });

        HBox buttons=new HBox(8,details,select);
        details.setMaxWidth(Double.MAX_VALUE);select.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(details,Priority.ALWAYS);HBox.setHgrow(select,Priority.ALWAYS);

        VBox card=new VBox(7,title,loc,match,g,buttons);
        card.setPadding(new Insets(13));
        card.setPrefWidth(215);
        card.setStyle("-fx-background-color:white;-fx-border-color:"
                +(best?P:"#CBD5E1")+";-fx-border-width:"+(best?"2":"1")
                +";-fx-border-radius:8;-fx-background-radius:8;");

        if(best){
            Label bm=lbl("BEST MATCH",10,true);
            bm.setTextFill(Color.WHITE);bm.setPadding(new Insets(4,9,4,9));
            bm.setStyle("-fx-background-color:"+P+";-fx-background-radius:12;");
            StackPane sp=new StackPane(card,bm);
            StackPane.setAlignment(bm,Pos.TOP_LEFT);
            StackPane.setMargin(bm,new Insets(-8,0,0,15));
            return new VBox(sp);
        }
        return card;
    }

    public Scene getorganizerAssignScene(){

        Event ev = currentSelectedEvent;

        VBox sidebar=new VBox(7);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setPadding(new Insets(25,18,18,18));
        sidebar.setStyle("-fx-background-color:"+P+";");

        Label logo=lbl("EventXpert",26,true);
        Label admin=lbl("ADMIN DASHBOARD",12,true);
        logo.setTextFill(Color.WHITE);admin.setTextFill(Color.WHITE);

        VBox logoBox=new VBox(1,logo,admin);
        VBox menu=new VBox(7);

        String[] items={"Dashboard","Event Requests","Events","Calendar","Customers","Organizers","Vendors","Conflict Detection","Reports & Analytics"};

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

        /* ================= HEADER ================= */

        Region hs=new Region();
        HBox.setHgrow(hs,Priority.ALWAYS);

        HBox header=new HBox(18,hs,
                lbl("🔔",18,false),
                lbl("?",18,false),
                lbl("⚙",18,false),
                lbl("👤",18,false));

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(8,25,8,25));
        header.setPrefHeight(55);
        header.setStyle("-fx-background-color:white;-fx-border-color:#E5EAF0;");

        /* ================= TITLE ================= */

        Label title=lbl("Organizer Assignment",29,true);
        title.setTextFill(Color.web(P));

        String currentStatus = (ev != null && ev.getStatus() != null) ? ev.getStatus() : "Pending Assignment";
        Label status=lbl(currentStatus,12,true);
        status.setPadding(new Insets(5,12,5,12));
        status.setStyle("-fx-background-color:"+L+";-fx-border-color:"+S+
                ";-fx-background-radius:15;-fx-border-radius:15;");

        HBox titleLine=new HBox(10,title,status);
        titleLine.setAlignment(Pos.CENTER_LEFT);

        Label sub=lbl("Select the most suitable organizer for this customer event.",14,false);
        VBox titleBox=new VBox(3,titleLine,sub);

        Button back=btn("←  Back to Review",false);
        back.setPrefWidth(145);

        back.setOnAction(e -> {
            Stage stage = (Stage) back.getScene().getWindow();
            NavigationManager.navigate("Event Request Review", stage);
        });

        Region ts=new Region();
        HBox.setHgrow(ts,Priority.ALWAYS);

        HBox titleRow=new HBox(titleBox,ts,back);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        /* ================= DYNAMIC SUMMARY ================= */

        String eventAndDate = (ev != null)
                ? (ev.getEventName() != null ? ev.getEventName() : (ev.getEventType() != null ? ev.getEventType() : "Event")) + "\n" + (ev.getCity() != null ? ev.getCity() + ", " : "") + (ev.getEventDate() != null ? ev.getEventDate() : "")
                : "Corporate Seminar\nPune, 18 Aug 2026";

        String guestsAndVenue = (ev != null)
                ? ev.getGuests() + " Guests\n" + (ev.getVenueType() != null ? ev.getVenueType() : (ev.getLocality() != null ? ev.getLocality() : "Venue TBD"))
                : "200 Guests\nHotel Tip Top";

        String budgetVal = (ev != null && ev.getBudget() != null) ? ("₹" + ev.getBudget()) : "₹30,000";
        String durationVal = (ev != null && ev.getTime() != null) ? ev.getTime() : "4 Hours";

        VBox summary=new VBox(lbl("Event Summary",20,true));
        HBox summaryData=new HBox(10,
                info("EVENT & DATE", eventAndDate),
                info("GUESTS & VENUE", guestsAndVenue),
                info("BUDGET", budgetVal),
                info("DURATION", durationVal));

        summaryData.setAlignment(Pos.CENTER_LEFT);
        summary.getChildren().add(summaryData);
        summary.setSpacing(5);
        summary.setPadding(new Insets(12,18,12,18));
        summary.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"
                +"-fx-border-radius:9;-fx-background-radius:9;");

        /* ================= DYNAMIC ORGANIZERS ================= */

        Label recTitle=lbl("Recommended Organizers",20,true);
        recTitle.setTextFill(Color.web(P));

        Label recSub=lbl(
                "Organizers matched according to event type, location, availability, experience and workload.",
                13,false);

        VBox recHead=new VBox(2,recTitle,recSub);

        Label selected=lbl("✓  SELECTED ORGANIZER\nSelect an organizer",13,true);
        selected.setTextFill(Color.web(P));
        selected.setPadding(new Insets(12));
        selected.setStyle("-fx-background-color:#F1F8FC;-fx-border-color:#C8E5F5;"
                +"-fx-border-radius:6;-fx-background-radius:6;");

        String[] current={""};

        HBox cards=new HBox(12);
        cards.setAlignment(Pos.TOP_LEFT);

        // Fetch Organizers from Firebase
        List<OrganizerProfile> dbOrganizers = organizerService.getAllOrganizers();
        if (dbOrganizers == null || dbOrganizers.isEmpty()) {
            dbOrganizers = eventService.getAllActiveOrganizers();
        }

        if (dbOrganizers != null && !dbOrganizers.isEmpty()) {
            String[] scores = {"95%", "89%", "82%", "78%"};
            String[] exps = {"6 Yrs", "4 Yrs", "7 Yrs", "5 Yrs"};
            String[] ratings = {"4.8", "4.6", "4.7", "4.5"};
            String[] done = {"45", "32", "51", "28"};

            for (int i = 0; i < dbOrganizers.size(); i++) {
                OrganizerProfile org = dbOrganizers.get(i);
                boolean isBest = (i == 0);
                String score = (i < scores.length) ? scores[i] : "80%";
                String exp = (i < exps.length) ? exps[i] : "3 Yrs";
                String rating = (i < ratings.length) ? ratings[i] : "4.5";
                String evDone = (i < done.length) ? done[i] : "20";
                String st = (org.getOnboardingStatus() != null) ? org.getOnboardingStatus() : "Available";

                if (isBest) {
                    selectedOrganizerObj = org;
                    String orgName = org.getOrganizationName() != null ? org.getOrganizationName() : (org.getName() != null ? org.getName() : "Organizer");
                    selected.setText("✓  SELECTED ORGANIZER\n" + orgName);
                    current[0] = orgName;
                }

                cards.getChildren().add(organizerCard(org, exp, rating, evDone, st, score, isBest, selected, current));
            }
        }

        VBox recommended=new VBox(30,recHead,cards);

        /* ================= CONFIRMATION PANE ================= */

        VBox confirm=new VBox(lbl("Assignment Confirmation",19,true));

        Label eventType=lbl("Event Type",11,true);
        Label eventValue=lbl(ev != null && ev.getEventType() != null ? ev.getEventType() : "Corporate Seminar",13,false);
        Label date=lbl("Date",11,true);
        Label dateValue=lbl(ev != null && ev.getEventDate() != null ? ev.getEventDate() : "18 Aug 2026",13,false);

        GridPane cg=new GridPane();
        cg.setHgap(35);cg.setVgap(7);
        cg.add(eventType,0,0);cg.add(date,1,0);
        cg.add(eventValue,0,1);cg.add(dateValue,1,1);

        TextArea notes=new TextArea();
        notes.setPromptText("Add any specific instructions or context for this assignment...");
        notes.setWrapText(true);
        notes.setPrefRowCount(3);

        Button confirmBtn=btn("Confirm Assignment  ✓",true);
        Button changeBtn=btn("Change Organizer",false);

        confirmBtn.setOnAction(e -> {
            String eventId = (ev != null && ev.getEventId() != null) ? ev.getEventId() : "";
            String orgId = (selectedOrganizerObj != null && selectedOrganizerObj.getOrganizerId() != null) ? selectedOrganizerObj.getOrganizerId() : "";
            String orgName = current[0];

            if (!eventId.isBlank() && !orgId.isBlank()) {
                eventService.assignOrganizerToEvent(eventId, orgId, orgName, "Admin", notes.getText());
            }

            status.setText("Assignment Confirmed");
            status.setStyle(
                "-fx-background-color:#DDF5EA;" +
                "-fx-text-fill:#087A4B;" +
                "-fx-background-radius:15;");

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Success");
            a.setHeaderText("Organizer Assigned Successfully");
            a.setContentText(
                "Organizer: " + current[0] +
                "\nEvent: " + (ev != null && ev.getEventName() != null ? ev.getEventName() : "Corporate Seminar") +
                "\nAssignment confirmed successfully.");
            a.showAndWait();

            Stage stage = (Stage) confirmBtn.getScene().getWindow();
            NavigationManager.navigate("Event Requests", stage);
        });

        changeBtn.setOnAction(e->{
            selected.setText("Select an organizer");
            current[0]="";
            selectedOrganizerObj = null;
            status.setText("Pending Assignment");
            status.setStyle("-fx-background-color:"+L+";-fx-border-color:"+S+
                    ";-fx-background-radius:15;-fx-border-radius:15;");
        });

        confirm.getChildren().addAll(selected,cg,
                lbl("Assignment Notes (Internal)",11,true),
                notes,confirmBtn,changeBtn);

        confirm.setSpacing(8);
        confirm.setPadding(new Insets(14));
        confirm.setPrefWidth(330);
        confirm.setStyle("-fx-background-color:white;-fx-border-color:#CBD5E1;"
                +"-fx-border-radius:9;-fx-background-radius:9;");

        VBox left=new VBox(10,recommended);
        HBox body=new HBox(15,left,confirm);
        HBox.setHgrow(left,Priority.ALWAYS);

        VBox content=new VBox(10,titleRow,summary,body);
        content.setPadding(new Insets(10,25,15,25));
        content.setStyle("-fx-background-color:#F7F9FC;");

        VBox center=new VBox(header,content);

        BorderPane root=new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene=new Scene(root,1550,750);
        organizerAssignScene = scene;
        return organizerAssignScene;
    }
}