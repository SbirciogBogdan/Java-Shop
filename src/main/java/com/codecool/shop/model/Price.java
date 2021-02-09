package com.codecool.shop.model;

import java.util.Currency;
import java.util.Date;

/**
 * Used to allow multiple prices for one product
 * (price evolution in time)
 */
public class Price {
    private double sum; //price of object
    private final Currency currency;
    private final Date date; //start date for price

    public Price(double sum, String currency, Date date) {
        this.sum = sum;
        this.currency = Currency.getInstance(currency);
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%1$.2f %2$s", sum, currency.toString());
    }

    public Date getDate() {
        return date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getSum() {
        return sum;
    }
}
