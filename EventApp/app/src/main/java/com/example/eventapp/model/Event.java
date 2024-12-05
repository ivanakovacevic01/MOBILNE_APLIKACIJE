package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Parcelable {
    public static ArrayList<Event> eventsList = new ArrayList<>();

    protected Event(Parcel in) {
        id = in.readString();
        type = in.readParcelable(EventType.class.getClassLoader());
        eventTypeId = in.readString();
        name = in.readString();
        description = in.readString();
        maxNumberOfParticipans = in.readInt();
        byte tmpIsPrivate = in.readByte();
        isPrivate = tmpIsPrivate == 0 ? null : tmpIsPrivate == 1;
        location = in.readString();
        maxKms = in.readDouble();
        organizerEmail = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if(event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }
    private String id;
    private EventType type; // delete
    private String eventTypeId;
    private String name;
    private String description;
    private int maxNumberOfParticipans;
    private Boolean isPrivate;
    private String location;
    private double maxKms;
    private Date date;
    private String organizerEmail;

    public static void setEventsList(ArrayList<Event> eventsList) {
        Event.eventsList = eventsList;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxNumberOfParticipans(int maxNumberOfParticipans) {
        this.maxNumberOfParticipans = maxNumberOfParticipans;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMaxKms(double maxKms) {
        this.maxKms = maxKms;
    }

    public static ArrayList<Event> getEventsList() {
        return eventsList;
    }

    public EventType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxNumberOfParticipans() {
        return maxNumberOfParticipans;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public String getLocation() {
        return location;
    }

    public double getMaxKms() {
        return maxKms;
    }

    public Event(String name, Date date)
    {
        this.name = name;
        this.date = date;

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Event(){}

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(type, flags);
        dest.writeString(eventTypeId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(maxNumberOfParticipans);
        dest.writeByte((byte) (isPrivate == null ? 0 : isPrivate ? 1 : 2));
        dest.writeString(location);
        dest.writeDouble(maxKms);
        dest.writeString(organizerEmail);
    }
//TODO agenda, guest list and budget
}
