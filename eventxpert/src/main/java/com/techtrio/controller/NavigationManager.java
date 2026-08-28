package com.techtrio.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.techtrio.view.AdminDashboardA;
import com.techtrio.view.EventRequestA;
import com.techtrio.view.EventsA;
import com.techtrio.view.NotificationsA;
import com.techtrio.view.OrganizerAssignA;
import com.techtrio.view.OrganizerDetailsA;
import com.techtrio.view.OrganizerManagementA;
import com.techtrio.view.ProfileA;
import com.techtrio.view.ReportA;
import com.techtrio.view.SettingsA;
import com.techtrio.view.SupportA;
import com.techtrio.view.VendorDetailsA;
import com.techtrio.view.VendorManagementA;
import com.techtrio.view.CalenderA;
import com.techtrio.view.ConflictDetectionA;
import com.techtrio.view.ConflictDetectionReviewA;
import com.techtrio.view.CustomerA;
import com.techtrio.view.CustomerDetailsA;
import com.techtrio.view.EventDetailsA;
import com.techtrio.view.EventRequesReviewA;

public class NavigationManager {

    public static void navigate(String page, Stage stage) {

        switch(page) {

            case "Dashboard":
                stage.setScene(
                    new AdminDashboardA().getadminDashboardScene());
                break;

            case "Event Requests":
                stage.setScene(
                    new EventRequestA().geteventRequestScene());
                break;

            case "Event Request Review":
                stage.setScene(
                        new EventRequesReviewA().geteventRequestReviewScene());
                break;

            case "Events":
                stage.setScene(
                    new EventsA().geteventsScene());
                break;

            case "Event Details":
                stage.setScene(
                        new EventDetailsA().geteventDetailsScene());
                break;

            case "Calendar":
                stage.setScene(
                    new CalenderA().getcalenderScene());
                break;

            case "Customers":
                stage.setScene(
                    new CustomerA().getcustomerScene());
                break;

            case "Customer Details":
                stage.setScene(
                    new CustomerDetailsA().getcustomerDetailsScene());
                break;
                

            case "Organizers":
                stage.setScene(
                    new OrganizerManagementA().getorganizerScene());
                break;

            case "Organizer Details":
                stage.setScene(
                        new OrganizerDetailsA().getorganizerDetailsScene());
                break;

               case "Assign Organizer":
                stage.setScene(
                        new OrganizerAssignA().getorganizerAssignScene());
                break;

            case "Vendors":
                stage.setScene(
                    new VendorManagementA().getvendorScene());
                break;    

            case "Vendor Details":
                stage.setScene(new VendorDetailsA().getvendorDetailScene());
                break;

            case "Conflict Detection":
                stage.setScene(
                    new ConflictDetectionA().getconflictDetectionScene());
                break;

            case "Conflict Detection Review":
                stage.setScene(
                        new ConflictDetectionReviewA().getconflictDetectionReviewScene());
                break;

            case "Reports & Analytics":
                stage.setScene(
                    new ReportA().getreportScene());
                break;

            case "Notifications":
                stage.setScene(
                    new NotificationsA().getnotificationsScene());
                break;

            case "Settings":
                stage.setScene(
                    new SettingsA().getsettingScene());
                break;

            case "Profile":
                stage.setScene(
                    new ProfileA().getprofileScene());
                break;

            case "Support":
                stage.setScene(
                    new SupportA().getsupportScene());
                break;
            
            
        }
    }
}
