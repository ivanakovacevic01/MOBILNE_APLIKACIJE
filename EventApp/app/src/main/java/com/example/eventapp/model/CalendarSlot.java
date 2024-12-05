package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class CalendarSlot implements Parcelable {
    private Date date;
    private String fromTime;
    private String toTime;
    private String name;
    private String status;

    public CalendarSlot() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    protected CalendarSlot(Parcel in) {
        fromTime = in.readString();
        toTime = in.readString();
        name = in.readString();
        status = in.readString();
    }

    public static final Creator<CalendarSlot> CREATOR = new Creator<CalendarSlot>() {
        @Override
        public CalendarSlot createFromParcel(Parcel in) {
            return new CalendarSlot(in);
        }

        @Override
        public CalendarSlot[] newArray(int size) {
            return new CalendarSlot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(fromTime);
        dest.writeString(toTime);
        dest.writeString(name);
        dest.writeString(status);
    }
}
