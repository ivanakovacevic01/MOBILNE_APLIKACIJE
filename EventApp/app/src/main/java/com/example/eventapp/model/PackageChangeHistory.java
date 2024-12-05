package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PackageChangeHistory implements Parcelable {
    private String id;
    private String changeTime;
    private Package aPackage;

    public PackageChangeHistory(){
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

    public Package getaPackage() {
        return aPackage;
    }

    public void setaPackage(Package p) {
        this.aPackage = p;
    }

    public static final Parcelable.Creator<PackageChangeHistory> CREATOR = new Parcelable.Creator<PackageChangeHistory>() {
        @Override
        public PackageChangeHistory createFromParcel(Parcel in) {
            return new PackageChangeHistory(in);
        }

        @Override
        public PackageChangeHistory[] newArray(int size) {
            return new PackageChangeHistory[size];
        }
    };

    protected PackageChangeHistory(Parcel in) {
        id = in.readString();
        changeTime = in.readString();
        aPackage = in.readParcelable(Package.class.getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(changeTime);
        dest.writeParcelable(aPackage, flags);

    }
}
