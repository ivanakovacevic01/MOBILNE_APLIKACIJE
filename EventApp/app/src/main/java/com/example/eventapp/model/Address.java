package com.example.eventapp.model;

public class Address {
    private String id;
    private String street;
    private String streetNumber;
    private String city;
    private String country;
    private String userId;
    private String firmId;


    public Address() {
    }

    public Address(String id,String street, String streetNumber, String city, String country, String userId, String firmId) {
        this.id=id;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.country = country;
        this.userId = userId;
        this.firmId = firmId;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress()
    {
        return street + ", " + streetNumber + ", " + city + ", " + country;
    }
}
