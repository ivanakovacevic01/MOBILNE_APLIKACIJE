package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class RegistrationOwnerRequest implements Parcelable {

    private String id;
    private String ownerId;
    private OwnerRequestStatus status;
    private String createdDate;


    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public RegistrationOwnerRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public OwnerRequestStatus getStatus() {
        return status;
    }

    public void setStatus(OwnerRequestStatus status) {
        this.status = status;
    }

    protected RegistrationOwnerRequest(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        createdDate = in.readString();
    }

    public static final Creator<RegistrationOwnerRequest> CREATOR = new Creator<RegistrationOwnerRequest>() {
        @Override
        public RegistrationOwnerRequest createFromParcel(Parcel in) {
            return new RegistrationOwnerRequest(in);
        }

        @Override
        public RegistrationOwnerRequest[] newArray(int size) {
            return new RegistrationOwnerRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(createdDate);
    }
}
