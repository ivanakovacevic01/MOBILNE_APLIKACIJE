package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Product implements Parcelable {
    private String firmId;
    private String id;
    private String name;

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    private String description;
    private double price;
    private double discount;
    private Boolean available;
    private Boolean visible;
    private List<String> type;
    private String category;
    private String subcategory;
    private Boolean deleted;
    private ArrayList<Change> changes;
    private ArrayList<Pricelist> pricelist;
    private String lastChange;
    private ArrayList<String> imageUris;

    public ArrayList<Pricelist> getPricelist() {
        return pricelist;
    }

    public void setPricelist(ArrayList<Pricelist> pricelist) {
        this.pricelist = pricelist;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }

    public Product(){
        this.changes=new ArrayList<>();
        this.deleted=false;
        this.imageUris=new ArrayList<>();
        this.pricelist=new ArrayList<>();
    }


    public Product(String id, String name, String description, double price, double discount, Boolean available, Boolean visible, List<String> type, String category, String subcategory, Boolean deleted, ArrayList<Change> changes, ArrayList<Pricelist> pricelist, String lastChange, ArrayList<String> imageUris) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.available = available;
        this.visible = visible;
        this.type = type;
        this.category = category;
        this.subcategory = subcategory;
        this.deleted = deleted;
        this.changes = changes;
        this.pricelist = pricelist;
        this.lastChange = lastChange;
        this.imageUris = imageUris;
    }

    public ArrayList<Change> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<Change> changes) {
        this.changes = changes;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", available=" + available +
                ", visible=" + visible +
                ", types=" + type +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                "deleted="+deleted.toString()+
                '}';
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

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


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public List<String> getTypes() {
        return type;
    }

    public void setTypes(ArrayList<String> types) {
        this.type = types;
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

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        subcategory=in.readString();
        price=in.readDouble();
        discount=in.readDouble();
        available=in.readBoolean();
        visible=in.readBoolean();
        type=in.readArrayList(String.class.getClassLoader());
        changes = new ArrayList<>();
        in.readList(changes, getClass().getClassLoader());
        lastChange = in.readString();
        imageUris=in.readArrayList(String.class.getClassLoader());
        pricelist = new ArrayList<>();
        in.readList(pricelist, getClass().getClassLoader());
        firmId=in.readString();
    }

    public ArrayList<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(ArrayList<String> imageUris) {
        this.imageUris = imageUris;
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
        dest.writeString(subcategory);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeList(type);
        dest.writeBoolean(available);
        dest.writeBoolean(visible);
        dest.writeList(changes);
        dest.writeString(lastChange);
        dest.writeList(imageUris);
        dest.writeList(pricelist);
        dest.writeString(firmId);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
