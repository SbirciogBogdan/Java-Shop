package com.codecool.shop.model;

public class Address {
    private int id;
    private String country;
    private String city;
    private String zipcode;
    private String homeAddress;

    public Address(int id, String country, String city, String zipcode, String homeAddress) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.zipcode = zipcode;
        this.homeAddress = homeAddress;
    }

    public Address(String country, String city, String zipcode, String homeAddress) {
        this.country = country;
        this.city = city;
        this.zipcode = zipcode;
        this.homeAddress = homeAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Override
    public String toString() {
        return country + ", \n"
             + city + ' ' + zipcode + ",\n"
             + homeAddress;
    }
}
