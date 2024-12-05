package com.example.eventapp.model;

import java.util.ArrayList;
import java.util.Date;

public class Filters {
    public String text;
    public ArrayList<EventType> types = new ArrayList<>();
    public ArrayList<Category> categories = new ArrayList<>();
    public ArrayList<Subcategory> subcategories = new ArrayList<>();
    public float startPrice;
    public float endPrice;
    public Date startDate;
    public Date endDate;
    public int availability;
}
