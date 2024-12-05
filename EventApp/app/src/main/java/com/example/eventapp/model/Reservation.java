package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class Reservation implements Parcelable {
    private String firmId;
    public String id;
    public ReservationStatus status;
    public ItemType type;
    public String productId;
    public String packageId;
    public String fromTime;
    public String toTime;

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String serviceId;
    public String createdDate;
    public String organizerEmail;
    public String eventId;
    public ArrayList<String> employees;
    public Date eventDate;


    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Reservation(){}

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }



    protected Reservation(Parcel in) {
        id = in.readString();
        productId = in.readString();
        packageId = in.readString();
        serviceId = in.readString();
        createdDate = in.readString();
        organizerEmail = in.readString();
        eventId = in.readString();
        firmId=in.readString();
        fromTime=in.readString();
        toTime=in.readString();
        employees = in.createStringArrayList();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productId);
        dest.writeString(packageId);
        dest.writeString(serviceId);
        dest.writeString(createdDate);
        dest.writeString(organizerEmail);
        dest.writeString(eventId);
        dest.writeStringList(employees);
        dest.writeString(firmId);
        dest.writeString(fromTime);
        dest.writeString(toTime);
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public ArrayList<String> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<String> employees) {
        this.employees = employees;
    }
}
