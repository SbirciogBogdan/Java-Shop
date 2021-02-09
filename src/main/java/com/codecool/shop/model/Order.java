package com.codecool.shop.model;

import com.codecool.shop.utilities.Log;

import java.util.Date;

public class Order extends BaseModel{
    private final Cart cart;
    private final User user;
    private OrderStatus orderStatus;
    private final Date date;
    private final Log log;

    public Order(Cart cart, User user) {
        super("Order");
        this.cart = cart;
        this.user = user;
        orderStatus = OrderStatus.CHECKED;
        date = new Date();
        log = new Log();
    }

    public Order(int id, String name, Date date, OrderStatus status, Cart cart) {
        super(id, name, "");
        this.date = date;
        orderStatus = status;
        this.cart = cart;
        user = null;
        log = new Log();
    }

    public Log getLog() { return log; }

    public Cart getCart() {
        return cart;
    }

    public User getUser() {
        return user;
    }

    public void pay() { orderStatus = OrderStatus.PAID; }

    public void confirm() {
        orderStatus = OrderStatus.CONFIRMED;
    }

    public void ship() { orderStatus = OrderStatus.SHIPPED; }

    public String getName() {
        return "Order-" + id;
    }

    public Date getDate() { return date; }

    public OrderStatus getStatus() { return orderStatus; }

    public void logEvent(Date date, String description) {
        log.addEvent(date, description);
    }
}
