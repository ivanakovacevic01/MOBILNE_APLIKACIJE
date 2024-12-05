package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Optional;

public class SubcategorySuggestion implements Parcelable {
    private String id;
    private String userId;
    private String name;
    private String description;
    //private Long categoryId;
    private String category;
    private Product product;
    private Service service;

    public SubcategorySuggestion(String id, String name, String description, String category, SubcategoryType subcategoryType, SuggestionStatus status, String userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.subcategoryType = subcategoryType;
        this.status = status;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SubcategoryType getSubcategoryType() {
        return subcategoryType;
    }

    public void setSubcategoryType(SubcategoryType subcategoryType) {
        this.subcategoryType = subcategoryType;
    }

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    private SubcategoryType subcategoryType;
    private SuggestionStatus status;

    public SubcategorySuggestion(){
        this.status=SuggestionStatus.PENDING;
    }

    protected SubcategorySuggestion(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readString();
        }
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(id);
        }
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubcategorySuggestion> CREATOR = new Creator<SubcategorySuggestion>() {
        @Override
        public SubcategorySuggestion createFromParcel(Parcel in) {
            return new SubcategorySuggestion(in);
        }

        @Override
        public SubcategorySuggestion[] newArray(int size) {
            return new SubcategorySuggestion[size];
        }
    };
}
