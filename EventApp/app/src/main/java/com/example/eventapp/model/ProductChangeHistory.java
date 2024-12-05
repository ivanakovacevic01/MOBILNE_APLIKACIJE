package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ProductChangeHistory implements Parcelable{
    private String id;
    private String changeTime;
    private Product product;

    public ProductChangeHistory(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public static final Parcelable.Creator<ProductChangeHistory> CREATOR = new Parcelable.Creator<ProductChangeHistory>() {
        @Override
        public ProductChangeHistory createFromParcel(Parcel in) {
            return new ProductChangeHistory(in);
        }

        @Override
        public ProductChangeHistory[] newArray(int size) {
            return new ProductChangeHistory[size];
        }
    };

    protected ProductChangeHistory(Parcel in) {
        id = in.readString();
        changeTime = in.readString();
        product = in.readParcelable(Product.class.getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(changeTime);
        dest.writeParcelable(product, flags);

    }
}
