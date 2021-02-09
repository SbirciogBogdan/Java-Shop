package com.codecool.shop.model;

public class User {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Address billing;
    private Address shipping;
    private int id;
    private final UserStatus userStatus;
    private Cart myCart;

    public User(int id, String name, String email, String password, String phoneNumber, Address billing, Address shipping, UserStatus userStatus, Cart myCart) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.billing = billing;
        this.shipping = shipping;
        this.userStatus = userStatus;
        this.myCart = myCart;
    }

    public User(String name, String email, String password, String phoneNumber, Address billing, Address shipping, UserStatus userStatus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.billing = billing;
        this.shipping = shipping;
        this.userStatus = userStatus;
        myCart = new Cart();

        //assume shipping = billing if only one present
        if (shipping == null && billing != null)
            this.shipping = billing;
        if (shipping != null && billing == null)
            this.billing = shipping;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getBilling() {
        return billing;
    }

    public void setBilling(Address billing) {
        this.billing = billing;
    }

    public Address getShipping() {
        return shipping;
    }

    public void setShipping(Address shipping) {
        this.shipping = shipping;
    }

    public UserStatus getUserStatus() { return userStatus; }

    public void setMyCart(Cart cart) { myCart = new Cart(cart); }

    public Cart getMyCart() { return myCart; }
}
