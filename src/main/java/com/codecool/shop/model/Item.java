package com.codecool.shop.model;

import java.util.Date;

public class Item {
    private final Product product;
    private final Price price;
    private int quantity;

    //copy constructor
    public Item(Item item) {
        product = item.getProduct();
        price = item.getPrice();
        quantity = item.getQuantity();
    }

    public Item(Product product, Price price) {
        this.product = product;
        this.price = price;
        quantity = 1;
    }

    public Item(Product product, Price price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() { return quantity; }

    public Price getPrice() { return price; }

    public Price getTotalPrice() {
        return new Price(price.getSum() * quantity, "USD", new Date());
    }
}
