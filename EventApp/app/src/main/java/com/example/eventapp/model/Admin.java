package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Admin extends User implements Parcelable {
    public Admin() {
        super();
    }

    protected Admin(Parcel in) {
        super(in);
    }

    public static final Creator<Admin> CREATOR = new Creator<Admin>() {
        @Override
        public Admin createFromParcel(Parcel in) {
            return new Admin(in);
        }

        @Override
        public Admin[] newArray(int size) {
            return new Admin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
