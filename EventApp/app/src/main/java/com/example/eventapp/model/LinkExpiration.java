package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.type.DateTime;

public class LinkExpiration implements Parcelable {
    String email;
    String sentTime;



    protected LinkExpiration(Parcel in) {
        email = in.readString();
        sentTime = in.readString();
    }

    public static final Creator<LinkExpiration> CREATOR = new Creator<LinkExpiration>() {
        @Override
        public LinkExpiration createFromParcel(Parcel in) {
            return new LinkExpiration(in);
        }

        @Override
        public LinkExpiration[] newArray(int size) {
            return new LinkExpiration[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(sentTime);
    }

    public LinkExpiration() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }
}
