package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Pricelist implements Parcelable {
    private String name;
    private String from;
    private String to;
    private double price;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    private double discount;
    private double discountPrice;

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
    public Pricelist(){}


    public Pricelist(String name, String from, String to, double price, double discount, double discountPrice) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.price = price;
        this.discount = discount;
        this.discountPrice = discountPrice;
    }

    protected Pricelist(Parcel in) {
        name = in.readString();
        from = in.readString();
        to = in.readString();
        price = in.readDouble();
        discount= in.readDouble();
        discountPrice= in.readDouble();

    }

    public static final Creator<Pricelist> CREATOR = new Creator<Pricelist>() {
        @Override
        public Pricelist createFromParcel(Parcel in) {
            return new Pricelist(in);
        }

        @Override
        public Pricelist[] newArray(int size) {
            return new Pricelist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeDouble(discountPrice);
    }
}
