package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Organizer extends User implements Parcelable {
    public Organizer() {
        super();
    }

    protected Organizer(Parcel in) {
        super(in);
    }

    public static final Creator<Organizer> CREATOR = new Creator<Organizer>() {
        @Override
        public Organizer createFromParcel(Parcel in) {
            return new Organizer(in);
        }

        @Override
        public Organizer[] newArray(int size) {
            return new Organizer[size];
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
