package com.complyt.entity;

public class Address {
    private String city;
    private String country;
    private String state;
    private String street;
    private int zip;

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }

    public int getZip() {
        return zip;
    }

    public Address(String city, String country, String state, String street, int zip) {
        this.city = city;
        this.country = country;
        this.state = state;
        this.street = street;
        this.zip = zip;
    }
}