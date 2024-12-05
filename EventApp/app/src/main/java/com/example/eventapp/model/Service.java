package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class Service implements Parcelable{
    private String firmId;
    private String id;
    private String category;

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    private String subcategory;
    private String name;
    private String description;
    private String specifics;
    private double pricePerHour;
    private double price;
    private double minDuration=-1;
    private double maxDuration=-1;
    private String location;
    private double discount;
    private List<String> attendants;
    private List<String> type;
    private int reservationDeadline;
    private int cancellationDeadline;
    private boolean available;
    private boolean visible;
    private boolean manualConfirmation;
    private boolean deleted;
    private double duration;
    private List<String> imageUris;

    private ArrayList<Change> changes;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setReservationDeadline(int reservationDeadline) {
        this.reservationDeadline = reservationDeadline;
    }

    public void setCancellationDeadline(int cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }




    public void setManualConfirmation(boolean manualConfirmation) {
        this.manualConfirmation = manualConfirmation;
    }

    public ArrayList<Pricelist> getPriceList() {
        return priceList;
    }

    public void setPriceList(ArrayList<Pricelist> priceList) {
        this.priceList = priceList;
    }

    private String lastChange;
    private ArrayList<Pricelist> priceList;

    public ArrayList<Change> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<Change> changes) {
        this.changes = changes;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }

    public String getId() {
        return id;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(double maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(double duration) {
        this.minDuration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<String> getAttendants() {
        return attendants;
    }

    public void setAttendants(List<String> attendants) {
        this.attendants = attendants;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public double getReservationDeadline() {
        return reservationDeadline;
    }


    public double getCancellationDeadline() {
        return cancellationDeadline;
    }

    public boolean getAvailable() {

        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public boolean getManualConfirmation() {
        return manualConfirmation;
    }


    public Service(String id, String category, String subcategory, String name, String description, String specifics, double pricePerHour, double price, double minDuration, double maxDuration, String location, double discount, List<String> attendants, List<String> type, int reservationDeadline, int cancellationDeadline, boolean available, boolean visible, boolean manualConfirmation, boolean deleted, List<String> imageUris, ArrayList<Change> changes, String lastChange, ArrayList<Pricelist> priceList,double duration) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.description = description;
        this.specifics = specifics;
        this.pricePerHour = pricePerHour;
        this.price = price;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.location = location;
        this.discount = discount;
        this.attendants = attendants;
        this.type = type;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.available = available;
        this.visible = visible;
        this.manualConfirmation = manualConfirmation;
        this.deleted = deleted;
        this.imageUris = imageUris;
        this.changes = changes;
        this.lastChange = lastChange;
        this.priceList = priceList;
        this.duration=duration;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", specifics='" + specifics + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", price=" + price +
                ", duration=" + minDuration +
                ", location='" + location + '\'' +
                ", discount=" + discount +
                ", attendants=" + attendants +
                ", type=" + type +
                ", reservationDeadline=" + reservationDeadline +
                ", cancelationDeadline=" + cancellationDeadline +
                ", available=" + available +
                ", visible=" + visible +
                ", manualy=" + manualConfirmation +
                '}';
    }

    public Service(){
        this.deleted=false;
        this.discount=0;
        this.specifics="";
        this.changes=new ArrayList<>();
        this.imageUris=new ArrayList<>();
        this.priceList=new ArrayList<>();

    }

    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    protected Service(Parcel in) {
        // Čitanje ostalih atributa proizvoda iz Parcel objekta
        id = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        subcategory=in.readString();
        price=in.readDouble();
        discount=in.readDouble();
        available=in.readBoolean();
        visible=in.readBoolean();
        specifics=in.readString();
        pricePerHour=in.readDouble();
        type=in.readArrayList(String.class.getClassLoader());
        minDuration=in.readDouble();
        maxDuration=in.readDouble();
        location=in.readString();
        attendants=in.readArrayList(String.class.getClassLoader());
        reservationDeadline=in.readInt();
        cancellationDeadline=in.readInt();
        manualConfirmation=in.readBoolean();
        lastChange=in.readString();
        changes = new ArrayList<>();
        in.readList(changes, getClass().getClassLoader());
        imageUris=in.readArrayList(String.class.getClassLoader());
        priceList=new ArrayList<>();
        in.readList(priceList,getClass().getClassLoader());
        firmId=in.readString();
        duration=in.readDouble();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(subcategory);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeList(type);
        dest.writeBoolean(available);
        dest.writeBoolean(visible);
        dest.writeString(specifics);
        dest.writeString(name);
        dest.writeDouble(pricePerHour);
        dest.writeDouble(minDuration);
        dest.writeDouble(maxDuration);
        dest.writeString(location);
        dest.writeList(attendants);
        dest.writeInt(reservationDeadline);
        dest.writeInt(cancellationDeadline);
        dest.writeBoolean(manualConfirmation);
        dest.writeList(changes);
        dest.writeString(lastChange);
        dest.writeList(imageUris);
        dest.writeList(priceList);
        dest.writeString(firmId);
        dest.writeDouble(duration);
    }

}
