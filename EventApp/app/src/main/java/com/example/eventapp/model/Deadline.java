package com.example.eventapp.model;

public class Deadline {
    public String id;
    public String firmId;
    public String organizerEmail;
    public String date;

    public Deadline() {}

    public Deadline(String id, String firmId, String organizerEmail, String date) {
        this.id = id;
        this.firmId = firmId;
        this.organizerEmail = organizerEmail;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String email) {
        this.organizerEmail = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
