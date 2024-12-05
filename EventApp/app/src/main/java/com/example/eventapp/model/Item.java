package com.example.eventapp.model;

public class Item {
    private ItemType type;
    private Package aPackage;
    private Service service;
    private Product product;
    
    public Item(Package p){
        this.type = ItemType.Package;
        this.aPackage = p;
    }

    public Item(Service s){
        this.type = ItemType.Service;
        this.service = s;
    }

    public Item(Product p){
        this.type = ItemType.Product;
        this.product = p;
    }

    public ItemType getType() {
        return type;
    }

    public Package getaPackage() {
        return aPackage;
    }

    public Service getService() {
        return service;
    }

    public Product getProduct() {
        return product;
    }
}
