package com.example.eventapp.model;

import java.util.ArrayList;

public class Favourites {
    private String id;
    private String userId;
    private String userEmail;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<Package> packages = new ArrayList<>();

    public Favourites() {
    }

    public Favourites(String userId, ArrayList<Product> products, ArrayList<Service> services, ArrayList<Package> packages) {
        this.userId = userId;
        this.products = products;
        this.services = services;
        this.packages = packages;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public ArrayList<Package> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }
}
