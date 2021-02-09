package com.codecool.shop.dao.memory;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.model.Address;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.User;
import com.codecool.shop.model.UserStatus;

import java.util.ArrayList;
import java.util.List;

public class UserDaoMem implements UserDao {
    private final List<User> data = new ArrayList<>();
    private static UserDaoMem instance = null;

    private UserDaoMem() {
    }

    public static UserDaoMem getInstance() {
        if (instance == null) {
            instance = new UserDaoMem();
        }
        return instance;
    }

    @Override
    public void add(User user) {
        user.setId(data.size() + 1);
        data.add(user);
    }

    @Override
    public boolean isSignedUp(String email) {
        return getSignedUserByMail(email) != null ;
    }

    @Override
    public User getAuthenticatedUser(String email, String password) {
        for(User user : data) {
            if(user.getUserStatus() == UserStatus.SIGNED && user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private User getSignedUserByMail(String email) {
        for(User user : data) {
            if(user.getEmail().equals(email) && user.getUserStatus() == UserStatus.SIGNED) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void remove(User user) {
        data.remove(user);
    }

    @Override
    public void updateUser(User user, String name, String email, String password, String phone, Address billing, Address shipping) {
        for(User old : data) {
            if(user.getId() == old.getId()) {
                old.setName(name);
                old.setEmail(email);
                old.setPassword(password);
                old.setPhoneNumber(phone);
                old.setBilling(billing);
                old.setShipping(shipping);
            }
        }
    }

    @Override
    public void updateUserCart(User user, Cart cart) {
        for(User old : data) {
            if(user.getId() == old.getId()) {
                old.setMyCart(cart);
                break;
            }
        }
    }
}
