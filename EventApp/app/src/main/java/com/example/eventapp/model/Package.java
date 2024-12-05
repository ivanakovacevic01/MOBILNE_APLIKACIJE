package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Package implements Parcelable {
    private String firmId;
    private String id;

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    private String name;
    private String description;
    private Boolean visible;
    private Boolean available;
    private double discount;
    private String category;
    private List<String> products;
    private Boolean deleted;
    private List<String> subcategories;
    private List<String> services;
    private List<String> type;

    private List<Pricelist> pricelist;
    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }

    private List<String> imageUris;
    private double price;
    private ArrayList<Change> changes;
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

    private String lastChange;

    public List<String> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<String> subcategories) {
        this.subcategories = subcategories;
    }

    private double reservationDeadline;
    private double cancellationDeadline;
    private Boolean manualConfirmation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getProducts() {
        return products;
    }

    public List<Pricelist> getPricelist() {
        return pricelist;
    }

    public void setPricelist(List<Pricelist> pricelist) {
        this.pricelist = pricelist;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



    public double getReservationDeadline() {
        return reservationDeadline;
    }

    public void setReservationDeadline(double reservationDeadline) {
        this.reservationDeadline = reservationDeadline;
    }

    public double getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(double cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public Boolean getManualConfirmation() {
        return manualConfirmation;
    }

    public void setManualConfirmation(Boolean manualConfirmation) {
        this.manualConfirmation = manualConfirmation;
    }

    public Package(String id, String name, String description, Boolean visible, Boolean available, double discount, String category, List<String> products, Boolean deleted, List<String> subcategories, List<String> services, List<String> type, List<Pricelist> pricelist, List<String> imageUris, double price, ArrayList<Change> changes, String lastChange, double reservationDeadline, double cancellationDeadline, Boolean manualConfirmation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.visible = visible;
        this.available = available;
        this.discount = discount;
        this.category = category;
        this.products = products;
        this.deleted = deleted;
        this.subcategories = subcategories;
        this.services = services;
        this.type = type;
        this.pricelist = pricelist;
        this.imageUris = imageUris;
        this.price = price;
        this.changes = changes;
        this.lastChange = lastChange;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.manualConfirmation = manualConfirmation;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Package(){
        this.discount=0;
        this.deleted=false;
        this.manualConfirmation=false;
        this.changes=new ArrayList<>();
        this.imageUris=new ArrayList<>();
        this.pricelist=new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibile=" + visible +
                ", available=" + available +
                ", discount=" + discount +
                ", category='" + category + '\'' +
                ", products=" + products +
                ", services=" + services +
                ", type=" + type +
                ", price=" + price +
                ", reservationDeadline=" + reservationDeadline +
                ", cancelationDeadline=" + cancellationDeadline +
                ", manualy=" + manualConfirmation +
                '}';
    }



    protected Package(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        subcategories=in.readArrayList(String.class.getClassLoader());
        price=in.readDouble();
        discount=in.readDouble();
        available=in.readBoolean();
        visible=in.readBoolean();
        type=in.readArrayList(String.class.getClassLoader());
        products=in.readArrayList(Product.class.getClassLoader());
        services=in.readArrayList(Service.class.getClassLoader());
        reservationDeadline=in.readDouble();
        cancellationDeadline=in.readDouble();
        manualConfirmation=in.readBoolean();
        changes = new ArrayList<>();
        in.readList(changes, getClass().getClassLoader());
        lastChange = in.readString();
        pricelist=new ArrayList<>();
        in.readList(pricelist,getClass().getClassLoader());
        firmId=in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeList(subcategories);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeList(type);
        dest.writeBoolean(available);
        dest.writeBoolean(visible);
        dest.writeBoolean(manualConfirmation);
        dest.writeList(products);
        dest.writeList(services);
        dest.writeDouble(cancellationDeadline);
        dest.writeDouble(reservationDeadline);
        dest.writeString(lastChange);
        dest.writeList(changes);
        dest.writeList(pricelist);
        dest.writeString(firmId);
    }

    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };
}
