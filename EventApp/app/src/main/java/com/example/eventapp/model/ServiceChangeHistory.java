package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ServiceChangeHistory implements Parcelable{
    private String id;
    private String changeTime;
    private Service service;

    public ServiceChangeHistory(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public static final Parcelable.Creator<ServiceChangeHistory> CREATOR = new Parcelable.Creator<ServiceChangeHistory>() {
        @Override
        public ServiceChangeHistory createFromParcel(Parcel in) {
            return new ServiceChangeHistory(in);
        }

        @Override
        public ServiceChangeHistory[] newArray(int size) {
            return new ServiceChangeHistory[size];
        }
    };

    protected ServiceChangeHistory(Parcel in) {
        id = in.readString();
        changeTime = in.readString();
        service = in.readParcelable(Service.class.getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(changeTime);
        dest.writeParcelable(service, flags);

    }
}
