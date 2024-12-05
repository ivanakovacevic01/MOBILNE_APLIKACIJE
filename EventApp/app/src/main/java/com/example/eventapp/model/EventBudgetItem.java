package com.example.eventapp.model;

import java.util.ArrayList;

public class EventBudgetItem {
    private String id;
    private String subcategoryId;
    private ArrayList<String> itemsIds = new ArrayList<>();
    private double plannedBudget;

    public EventBudgetItem() {
    }

    public EventBudgetItem(String subcategoryId, ArrayList<String> itemsIds, double plannedBudget) {
        this.subcategoryId = subcategoryId;
        this.itemsIds = itemsIds;
        this.plannedBudget = plannedBudget;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public ArrayList<String> getItemsIds() {
        return itemsIds;
    }

    public void setItemsIds(ArrayList<String> itemsIds) {
        this.itemsIds = itemsIds;
    }

    public double getPlannedBudget() {
        return plannedBudget;
    }

    public void setPlannedBudget(double plannedBudget) {
        this.plannedBudget = plannedBudget;
    }
}
