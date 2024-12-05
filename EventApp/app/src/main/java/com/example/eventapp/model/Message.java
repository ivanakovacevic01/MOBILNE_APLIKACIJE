package com.example.eventapp.model;

import java.util.Date;

public class Message {
    private String id;
    private String senderId;
    private String recipientId;
    private Date date;
    private String time;
    private MessageStatus status;
    private String text;

    public Message() {
    }

    public Message(String id, String senderId, String recipientId, Date date, String time, MessageStatus status, String text) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
