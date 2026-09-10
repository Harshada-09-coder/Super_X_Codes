package com.techtrio.model;

import java.util.UUID;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Guest {
    private String id;
    private String name;
    private String mobile;
    private String email;
    private String type; // VIP, Speaker, General, etc.
    private boolean checkedIn;
    private Long checkInTime;

    public Guest() {
        this.id = UUID.randomUUID().toString();
        this.checkedIn = false;
        this.checkInTime = null;
    }

    public Guest(String name, String mobile, String email, String type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.type = (type == null || type.isBlank()) ? "General" : type;
        this.checkedIn = false;
        this.checkInTime = null;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    public Long getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Long checkInTime) { this.checkInTime = checkInTime; }
}