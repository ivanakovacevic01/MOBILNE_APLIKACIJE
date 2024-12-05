package com.example.eventapp.model;

public class Guest {
    private String id;
    private String eventId;
    private String name;
    private String lastName;
    private String ageGroup;
    private boolean isInvited;
    private boolean confirmed;
    private SpecialRequest specialRequest;

    public Guest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public SpecialRequest getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(SpecialRequest specialRequest) {
        this.specialRequest = specialRequest;
    }
}
