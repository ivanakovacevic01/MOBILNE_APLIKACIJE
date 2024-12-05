package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Subcategory  implements Parcelable {
    private String id;
    private String name;

    public Subcategory() {
        // Potreban prazan kontr zbog firestore db
    }
    public Subcategory(String id, String name, String description, String categoryId, SubcategoryType subcategoryType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.subcategoryType = subcategoryType;
    }

    private String description;
    private String categoryId;
    private SubcategoryType subcategoryType;

    protected Subcategory(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        categoryId = in.readString();
    }

    public static final Creator<Subcategory> CREATOR = new Creator<Subcategory>() {
        @Override
        public Subcategory createFromParcel(Parcel in) {
            return new Subcategory(in);
        }

        @Override
        public Subcategory[] newArray(int size) {
            return new Subcategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(categoryId);
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public SubcategoryType getSubcategoryType() {
        return subcategoryType;
    }

    public void setSubcategoryType(SubcategoryType subcategoryType) {
        this.subcategoryType = subcategoryType;
    }
}
