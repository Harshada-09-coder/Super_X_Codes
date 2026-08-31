package com.techtrio.model;

public class Conflict {

    private String conflictId;

    private String eventId;

    private String conflictingEventId;

    private String type;

    private String severity;

    private String status;

    private String date;

    private String resource;

    private String description;

    private String createdAt;

    private String resolvedAt;

    private String resolutionNotes;


    // =========================
    // CONSTRUCTORS
    // =========================

    public Conflict() {
        // Required for Firebase
    }


    public Conflict(
            String conflictId,
            String eventId,
            String conflictingEventId,
            String type,
            String severity,
            String status,
            String date,
            String resource,
            String description,
            String createdAt,
            String resolvedAt,
            String resolutionNotes) {

        this.conflictId = conflictId;
        this.eventId = eventId;
        this.conflictingEventId = conflictingEventId;
        this.type = type;
        this.severity = severity;
        this.status = status;
        this.date = date;
        this.resource = resource;
        this.description = description;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.resolutionNotes = resolutionNotes;
    }


    // =========================
    // GETTERS & SETTERS
    // =========================

    public String getConflictId() {
        return conflictId;
    }

    public void setConflictId(String conflictId) {
        this.conflictId = conflictId;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getConflictingEventId() {
        return conflictingEventId;
    }

    public void setConflictingEventId(
            String conflictingEventId) {

        this.conflictingEventId = conflictingEventId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }


    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(
            String resolutionNotes) {

        this.resolutionNotes = resolutionNotes;
    }
}

