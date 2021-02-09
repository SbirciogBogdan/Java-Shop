package com.codecool.shop.dao;

import com.codecool.shop.Tester;
import com.codecool.shop.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;

class UserDaoTest {
    private static DataStore dataStore;

    @BeforeAll
    static void setUp() {
        Tester.getInstance();
        dataStore = DataStore.getInstance();
    }

    @Test
    void testAddUser() {
        //add new user
        User user = new User("testname", "testemail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        //check if id is set
        Assertions.assertTrue(user.getId() > 0);
    }

    @Test
    void testIsSignedUpUser() {
        //add new user
        User user = new User("testname", "testemail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        Assertions.assertTrue(dataStore.userDao.isSignedUp("testemail@test.com"));
        Assertions.assertFalse(dataStore.userDao.isSignedUp("wrongemail"));
    }

    @Test
    void testGetAuthenticatedUser() {
        //add new user
        User user = new User("testname", "testemail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        //get users
        User user1Good = dataStore.userDao.getAuthenticatedUser("testemail@test.com", "testpass");
        User user2WrongPass = dataStore.userDao.getAuthenticatedUser("testemail@test.com", "wrongpass");
        User user3WrongMail = dataStore.userDao.getAuthenticatedUser("wrongmail@test.com", "testpass");

        Assertions.assertEquals(user.getId(), user1Good.getId());
        Assertions.assertNull(user2WrongPass);
        Assertions.assertNull(user3WrongMail);
    }

    @Test
    void testRemoveUser() {
        //add new user
        User user = new User("testname", "removemail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        Assertions.assertTrue(dataStore.userDao.isSignedUp("removemail@test.com"));
        dataStore.userDao.remove(user);
        Assertions.assertFalse(dataStore.userDao.isSignedUp("removemail@test.com"));
    }

    @Test
    void testUpdateUser() {
        //add new user
        Address address1 = new Address("a", "a", "a", "a");
        Address address2 = new Address("a", "a", "a", "a");
        User user = new User("testname", "testemail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        dataStore.userDao.updateUser(user, "newname", "newmail", "newpass", "5678", address1, address2);

        Assertions.assertAll("User update:",
                () -> Assertions.assertEquals("newname", user.getName()),
                () -> Assertions.assertEquals("newmail", user.getEmail()),
                () -> Assertions.assertEquals("newpass", user.getPassword()),
                () -> Assertions.assertEquals("5678", user.getPhoneNumber()),
                () -> Assertions.assertEquals(address1.getId(), user.getBilling().getId()),
                () -> Assertions.assertEquals(address2.getId(), user.getShipping().getId())
        );
    }

    @Test
    void testUpdateUserCart() {
        //add new user
        User user = new User("testname", "cartmail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        //get number of items before update
        int before = user.getMyCart().getNumberOfItems();

        //create new cart
        Cart cart = new Cart();

        //add valid products to new cart
        Date currentDate = new Date();
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Supplier bvlgari = new Supplier("Bvlgari", "Luxury brand known for its jewellery, watches, fragrances, " +
                "accessories and leather goods");
        dataStore.supplierDao.add(bvlgari);
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        dataStore.categoryDao.add(ring);
        Category necklace = new Category("Necklace", "Jewelry", "String of stones, beads, jewels, or the like, for " +
                "wearing");
        dataStore.categoryDao.add(necklace);
        Category earrings = new Category("Earrings", "Jewelry", "Ornaments worn as accessories");
        dataStore.categoryDao.add(earrings);
        Product product1 = new Product("Prada Sapphire and diamonds Rings", 250 , "USD", currentDate, "24 karate " +
                "white gold rings with sapphire and diamonds", "pic1.jpg", ring, prada);
        Product product2 = new Product("Bvlgari Ruby Earrings", 500, "USD", currentDate, "Ruby inlaid earrings in " +
                "genuine 925 silver. Red tourmaline jewelry. The earrings are plated with 18 carat white gold.",
                "pic2.jpg", earrings, bvlgari);
        Product product3 = new Product("Prada Blue crystal Necklace", 350, "USD", currentDate, "Simple and " +
                "fashionable design. Material: crystal, resin and metal alloy. Pendant dimensions: 2x2cm. Chain length: 38 cm.", "pic3.jpg", necklace, prada);
        dataStore.productDao.add(product1);
        dataStore.productDao.add(product2);
        dataStore.productDao.add(product3);
        cart.addProduct(product1, product1.getCurrentPrice());
        cart.addProduct(product2, product2.getCurrentPrice());
        cart.addProduct(product3, product3.getCurrentPrice());

        //update user cart
        dataStore.userDao.updateUserCart(user, cart);

        //get and check number of items in user (parameter) cart after update
        int after = user.getMyCart().getNumberOfItems();
        Assertions.assertEquals(before + 3, after, "Wrong number of items in user variable");

        //get and check number of items in user (database) cart after update
        user = dataStore.userDao.getAuthenticatedUser("cartmail@test.com", "testpass");
        Assertions.assertNotNull(user, "Wrong mail or password");
        after = user.getMyCart().getNumberOfItems();
        Assertions.assertEquals(before + 3, after, "Wrong number of items in user database");
        dataStore.userDao.remove(user);
    }
}