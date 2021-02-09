package com.codecool.shop.dao;

import com.codecool.shop.Tester;
import com.codecool.shop.model.Category;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

class ProductDaoTest {
    private static DataStore dataStore;

    @BeforeAll
    static void setUp() {
        Tester.getInstance();
        dataStore = DataStore.getInstance();
    }

    @Test
    void testAddProduct() {
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);
        Assertions.assertTrue(product1.getId() > 0);
    }

    @Test
    void testFindProduct() {
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);
        Product dbProduct = dataStore.productDao.find(product1.getId());
        Assertions.assertAll("Product:",
                () -> Assertions.assertEquals(product1.getId(), dbProduct.getId()),
                () -> Assertions.assertEquals(product1.getSupplier().getId(), dbProduct.getSupplier().getId()),
                () -> Assertions.assertEquals(product1.getName(), dbProduct.getName()),
                () -> Assertions.assertEquals(product1.getDescription(), dbProduct.getDescription())
        );
    }

    @Test
    void testRemoveProduct() {
        //add new product
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);

        //get number of products before remove
        List<Product> productList = dataStore.productDao.getAll();
        int before = productList.size();

        //remove
        dataStore.productDao.remove(product1.getId());

        //get number of products after remove
        productList = dataStore.productDao.getAll();
        int after = productList.size();

        //check
        Assertions.assertEquals(before-1, after);
    }

    @Test
    void testGetAllProducts() {
        //get number of products before add
        List<Product> productList = dataStore.productDao.getAll();
        int before = productList.size();

        //add new product
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);

        //get number of products after add
        productList = dataStore.productDao.getAll();
        int after = productList.size();

        //check
        Assertions.assertEquals(before+1, after);
    }

    @Test
    void testGetBySupplier() {
        //add new supplier
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);

        //get number of supplier products before add
        List<Product> productList = dataStore.productDao.getBy(prada);
        int before = productList.size();

        //add new category product
        Date currentDate = new Date();
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);

        //get number of supplier products after add
        productList = dataStore.productDao.getBy(prada);
        int after = productList.size();

        //check
        Assertions.assertEquals(before+1, after);
    }

    @Test
    void testGetByCategory() {
        //add new category
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);

        //get number of category products before add
        List<Product> productList = dataStore.productDao.getBy(ring);
        int before = productList.size();

        //add new supplier product
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);

        //get number of category products after add
        productList = dataStore.productDao.getBy(ring);
        int after = productList.size();

        //check
        Assertions.assertEquals(before+1, after);
    }

    @Test
    void testGetCurrentPriceIdByProductId() {
        //add new product
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        dataStore.productDao.add(product1);

        Assertions.assertTrue(dataStore.productDao.getCurrentPriceIdByProduct(product1.getId()) > 0);
    }
}