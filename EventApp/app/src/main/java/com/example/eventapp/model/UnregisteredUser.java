package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UnregisteredUser extends User implements Parcelable {

    public UnregisteredUser() {
        super();
    }

    protected UnregisteredUser(Parcel in) {
        super(in);
    }

    public static final Creator<UnregisteredUser> CREATOR = new Creator<UnregisteredUser>() {
        @Override
        public UnregisteredUser createFromParcel(Parcel in) {
            return new UnregisteredUser(in);
        }

        @Override
        public UnregisteredUser[] newArray(int size) {
            return new UnregisteredUser[size];
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
