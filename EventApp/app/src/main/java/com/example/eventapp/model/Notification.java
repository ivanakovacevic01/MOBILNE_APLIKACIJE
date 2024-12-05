package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Notification implements Parcelable {
    private String date;
    private String id;
    private String message;
    private ShowStatus showStatus;

    public ShowStatus getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(ShowStatus showStatus) {
        this.showStatus = showStatus;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    private String senderId;
    private String receiverId;
    private UserType receiverRole;//za notifikacije namenjene bilo kom adminu
    private NotificationStatus status;
    public Notification(){
        receiverId="";
        this.status=NotificationStatus.NEW;
        this.showStatus=ShowStatus.UNSHOWED;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public UserType getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(UserType receiverRole) {
        this.receiverRole = receiverRole;
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };


    protected Notification(Parcel in) {
        id = in.readString();
        message=in.readString();
        senderId=in.readString();
        receiverId=in.readString();
        receiverRole = UserType.valueOf(in.readString()); // Read UserType enum from Parcel
        status = NotificationStatus.valueOf(in.readString()); // Read NotificationStatus enum from Parcel
        date=in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(message);
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeString(receiverRole.name()); // Write UserType enum to Parcel
        dest.writeString(status.name()); // Write NotificationStatus enum to Parcel
        dest.writeString(date);

    }
}
