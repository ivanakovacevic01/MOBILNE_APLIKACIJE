package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Change implements Parcelable {
    private String id;

    public Change(String id, LocalDateTime localDateTime) {
        this.id = id;
        this.localDateTime = localDateTime.toString();
    }
    public Change(){}
    private String localDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public static final Parcelable.Creator<Change> CREATOR = new Parcelable.Creator<Change>() {
        @Override
        public Change createFromParcel(Parcel in) {
            return new Change(in);
        }

        @Override
        public Change[] newArray(int size) {
            return new Change[size];
        }
    };

    protected Change(Parcel in) {
        id = in.readString();
        localDateTime = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(localDateTime.toString()); // Assuming lastUpdated is a LocalDateTime object
    }

}
