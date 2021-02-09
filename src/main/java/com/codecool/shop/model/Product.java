package com.codecool.shop.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product extends BaseModel {

    private List<Price> prices = new ArrayList<>();
    private Category category;
    private Supplier supplier;
    private final String imageFileName;

    public Product(String name, float firstSum, String currency, Date date, String description, String imageFileName,
                   Category category, Supplier supplier) {
        super(name, description);
        setPrice(new Price(firstSum,currency,date));
        setSupplier(supplier);
        setCategory(category);
        this.imageFileName = imageFileName;
    }

    public Product(Integer id, String name, String description, String imageFileName, List<Price> prices,
                   Category category, Supplier supplier) {
        super(id, name, description);
        this.imageFileName = imageFileName;
        this.prices = prices;
        this.category = category;
        this.supplier = supplier;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    /**
     * Gets actual (most recent) price
     * @return most recent price
     */
    public Price getCurrentPrice() {
        int lastIndex = prices.size() - 1;
        return prices.get(lastIndex);
    }

    public void setNewPrice(Price newPrice) {
        Price lastPrice = getCurrentPrice();
        int lastIndex = prices.size() - 1;
        if(lastPrice.getDate().equals(newPrice.getDate()))
            prices.set(lastIndex, newPrice); //avoid multiple prices recorded at same date
        else
            prices.add(newPrice);
    }

    public List<Price> getPrices() {
        return prices;
    }

    public String getPrice() {
        return getCurrentPrice().toString();
    }

    public void setPrice(Price price) {
        prices.add(price);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.category.addProduct(this);
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        this.supplier.addProduct(this);
    }

    @Override
    public String toString() {
        return String.format("id: %1$d, " +
                        "name: %2$s, " +
                        "price: %3$s, " +
                        "category: %4$s, " +
                        "supplier: %5$s",
                id,
                name,
                getCurrentPrice().toString(),
                category.getName(),
                supplier.getName());
    }
}
