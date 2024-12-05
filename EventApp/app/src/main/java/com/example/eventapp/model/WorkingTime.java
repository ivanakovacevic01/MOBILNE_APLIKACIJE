package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class WorkingTime implements Parcelable{
    private String id;
    private String startDate;
    private String endDate;
    private String mondayStartTime;
    private String mondayEndTime;

    private String tuesdayStartTime;
    private String tuesdayEndTime;

    private String wednesdayStartTime;
    private String wednesdayEndTime;

    private String thursdayStartTime;
    private String thursdayEndTime;

    private String fridayStartTime;
    private String fridayEndTime;

    private String saturdayStartTime;
    private String saturdayEndTime;

    private String sundayStartTime;
    private String sundayEndTime;
    private String userId;

    public WorkingTime(){}

    public WorkingTime(String id, String startDate, String endDate, String mondayStartTime,String userId){
        this.id=id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mondayStartTime = mondayStartTime;

        this.userId = userId;
    }

    protected WorkingTime(Parcel in) {
        id = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        mondayStartTime = in.readString();
        mondayEndTime = in.readString();
        tuesdayStartTime = in.readString();
        tuesdayEndTime = in.readString();
        wednesdayStartTime = in.readString();
        wednesdayEndTime = in.readString();
        thursdayStartTime = in.readString();
        thursdayEndTime = in.readString();
        fridayStartTime = in.readString();
        fridayEndTime = in.readString();
        saturdayStartTime = in.readString();
        saturdayEndTime = in.readString();
        sundayStartTime = in.readString();
        sundayEndTime = in.readString();
        userId = in.readString();
    }

    public static final Creator<WorkingTime> CREATOR = new Creator<WorkingTime>() {
        @Override
        public WorkingTime createFromParcel(Parcel in) {
            return new WorkingTime(in);
        }

        @Override
        public WorkingTime[] newArray(int size) {
            return new WorkingTime[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMondayStartTime() {
        return mondayStartTime;
    }

    public void setMondayStartTime(String mondayStartTime) {
        this.mondayStartTime = mondayStartTime;
    }

    public String getMondayEndTime() {
        return mondayEndTime;
    }

    public void setMondayEndTime(String mondayEndTime) {
        this.mondayEndTime = mondayEndTime;
    }

    public String getTuesdayStartTime() {
        return tuesdayStartTime;
    }

    public void setTuesdayStartTime(String tuesdayStartTime) {
        this.tuesdayStartTime = tuesdayStartTime;
    }

    public String getTuesdayEndTime() {
        return tuesdayEndTime;
    }

    public void setTuesdayEndTime(String tuesdayEndTime) {
        this.tuesdayEndTime = tuesdayEndTime;
    }

    public String getWednesdayStartTime() {
        return wednesdayStartTime;
    }

    public void setWednesdayStartTime(String wednesdayStartTime) {
        this.wednesdayStartTime = wednesdayStartTime;
    }

    public String getWednesdayEndTime() {
        return wednesdayEndTime;
    }

    public void setWednesdayEndTime(String wednesdayEndTime) {
        this.wednesdayEndTime = wednesdayEndTime;
    }

    public String getThursdayStartTime() {
        return thursdayStartTime;
    }

    public void setThursdayStartTime(String thursdayStartTime) {
        this.thursdayStartTime = thursdayStartTime;
    }

    public String getThursdayEndTime() {
        return thursdayEndTime;
    }

    public void setThursdayEndTime(String thursdayEndTime) {
        this.thursdayEndTime = thursdayEndTime;
    }

    public String getFridayStartTime() {
        return fridayStartTime;
    }

    public void setFridayStartTime(String fridayStartTime) {
        this.fridayStartTime = fridayStartTime;
    }

    public String getFridayEndTime() {
        return fridayEndTime;
    }

    public void setFridayEndTime(String fridayEndTime) {
        this.fridayEndTime = fridayEndTime;
    }

    public String getSaturdayStartTime() {
        return saturdayStartTime;
    }

    public void setSaturdayStartTime(String saturdayStartTime) {
        this.saturdayStartTime = saturdayStartTime;
    }

    public String getSaturdayEndTime() {
        return saturdayEndTime;
    }

    public void setSaturdayEndTime(String saturdayEndTime) {
        this.saturdayEndTime = saturdayEndTime;
    }

    public String getSundayStartTime() {
        return sundayStartTime;
    }

    public void setSundayStartTime(String sundayStartTime) {
        this.sundayStartTime = sundayStartTime;
    }

    public String getSundayEndTime() {
        return sundayEndTime;
    }

    public void setSundayEndTime(String sundayEndTime) {
        this.sundayEndTime = sundayEndTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(mondayStartTime);
        dest.writeString(mondayEndTime);
        dest.writeString(tuesdayStartTime);
        dest.writeString(tuesdayEndTime);
        dest.writeString(wednesdayStartTime);
        dest.writeString(wednesdayEndTime);
        dest.writeString(thursdayStartTime);
        dest.writeString(thursdayEndTime);
        dest.writeString(fridayStartTime);
        dest.writeString(fridayEndTime);
        dest.writeString(saturdayStartTime);
        dest.writeString(saturdayEndTime);
        dest.writeString(sundayStartTime);
        dest.writeString(sundayEndTime);
        dest.writeString(userId);
    }
}

