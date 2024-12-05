package com.example.eventapp.model;

import java.util.ArrayList;

import kotlin.text.UStringsKt;

public class EventBudget {
    private String id;
    private String eventId;
    private ArrayList<String> eventBudgetItemsIds = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventBudget() {
    }

    public EventBudget(String eventId, ArrayList<String> eventBudgetItemsIds) {
        this.eventId = eventId;
        this.eventBudgetItemsIds = eventBudgetItemsIds;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ArrayList<String> getEventBudgetItemsIds() {
        return eventBudgetItemsIds;
    }

    public void setEventBudgetItemsIds(ArrayList<String> eventBudgetItemsIds) {
        this.eventBudgetItemsIds = eventBudgetItemsIds;
    }
}
