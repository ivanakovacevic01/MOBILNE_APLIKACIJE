package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class EventType implements Parcelable {
    private String id;
    private String name;
    private String description;
    private List<String> suggestedSubcategoriesIds;
    private Boolean isDeactivated;

    public EventType() {
    }

    public EventType(String id, String name, String description, Boolean isDeactivated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isDeactivated = isDeactivated;
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

    public List<String> getSuggestedSubcategoriesIds() {
        return suggestedSubcategoriesIds;
    }

    public void setSuggestedSubcategoriesIds(List<String> suggestedSubcategoriesIds) {
        this.suggestedSubcategoriesIds = suggestedSubcategoriesIds;
    }

    public Boolean getDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        isDeactivated = deactivated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    protected EventType(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        suggestedSubcategoriesIds = in.readArrayList(String.class.getClassLoader());
        byte tmpIsDeactivated = in.readByte();
        isDeactivated = tmpIsDeactivated == 0 ? null : tmpIsDeactivated == 1;

    }

    public static final Creator<EventType> CREATOR = new Creator<EventType>() {
        @Override
        public EventType createFromParcel(Parcel in) {
            return new EventType(in);
        }

        @Override
        public EventType[] newArray(int size) {
            return new EventType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeList(suggestedSubcategoriesIds);
        dest.writeByte((byte) (isDeactivated == null ? 0 : isDeactivated ? 1 : 2));
    }
}
