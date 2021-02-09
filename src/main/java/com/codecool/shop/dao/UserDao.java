package com.codecool.shop.dao;

import com.codecool.shop.model.Address;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.User;

public interface UserDao {
    void add(User user);
    boolean isSignedUp(String email);
    User getAuthenticatedUser(String email, String password);
    void remove(User user);
    void updateUser(User user, String name, String email, String password, String phone, Address billing, Address shipping);
    void updateUserCart(User user, Cart cart);
}
