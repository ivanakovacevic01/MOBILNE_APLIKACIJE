package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Firm implements Parcelable {
    private String id;
    private FirmType type;
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private ArrayList<String> images;
    private ArrayList<String> categoriesIds;
    private ArrayList<String> eventTypesIds;
    private boolean active;
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<String> getCategoriesIds() {
        return categoriesIds;
    }

    public void setCategoriesIds(ArrayList<String> categoriesIds) {
        this.categoriesIds = categoriesIds;
    }

    public ArrayList<String> getEventTypesIds() {
        return eventTypesIds;
    }

    public void setEventTypesIds(ArrayList<String> eventTypesIds) {
        this.eventTypesIds = eventTypesIds;
    }

    public Firm() {
    }

    public Firm(String id, FirmType type, String email, String name, String phoneNumber, String description, ArrayList<String> images) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FirmType getType() {
        return type;
    }

    public void setType(FirmType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    protected Firm(Parcel in) {
        id = in.readString();
        email = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        description = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<Firm> CREATOR = new Creator<Firm>() {
        @Override
        public Firm createFromParcel(Parcel in) {
            return new Firm(in);
        }

        @Override
        public Firm[] newArray(int size) {
            return new Firm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(description);
        dest.writeStringList(images);
    }
}
