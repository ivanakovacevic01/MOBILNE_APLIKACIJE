package com.example.eventapp.model;

public class PdfItem {
    private String name;
    private String type;
    private double price;
    private double discount;
    private double discountPrice;
    private int number;
    public PdfItem(){}

    public PdfItem(String name, String type, double price, double discount, double discountPrice, Integer number) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.discount = discount;
        this.discountPrice = discountPrice;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
