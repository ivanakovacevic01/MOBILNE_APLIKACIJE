package com.example.eventapp.model;

public class WeeklyEvent {
    public enum EventType {
        RESERVED,
        BUSY
    }

    public String id;
    public String name;
    public String date;
    public String from;
    public String to;
    public String employeeId;
    public String firmId;
    public EventType eventType;
    public String reservationId;

    public WeeklyEvent(){}
    public WeeklyEvent(String id, String name, String date, String from, String to, String employeeId, EventType eventType, String firmId, String reservationId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.from = from;
        this.to = to;
        this.employeeId = employeeId;
        this.eventType = eventType;
        this.firmId=firmId;
        this.reservationId=reservationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
}
