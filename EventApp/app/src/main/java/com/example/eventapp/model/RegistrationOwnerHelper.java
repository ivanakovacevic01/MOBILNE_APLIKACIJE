package com.example.eventapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class RegistrationOwnerHelper implements Parcelable {
    private Owner owner;
    private Address ownerAddress;
    private Address firmAddress;
    private Firm firm;
    private String password;


    public RegistrationOwnerHelper() {
    }

    protected RegistrationOwnerHelper(Parcel in) {
        owner = in.readParcelable(Owner.class.getClassLoader());
        firm = in.readParcelable(Firm.class.getClassLoader());
        password = in.readString();
    }

    public static final Creator<RegistrationOwnerHelper> CREATOR = new Creator<RegistrationOwnerHelper>() {
        @Override
        public RegistrationOwnerHelper createFromParcel(Parcel in) {
            return new RegistrationOwnerHelper(in);
        }

        @Override
        public RegistrationOwnerHelper[] newArray(int size) {
            return new RegistrationOwnerHelper[size];
        }
    };

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Address getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(Address ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public Address getFirmAddress() {
        return firmAddress;
    }

    public void setFirmAddress(Address firmAddress) {
        this.firmAddress = firmAddress;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(owner, flags);
        dest.writeParcelable(firm, flags);
        dest.writeString(password);
    }


}
