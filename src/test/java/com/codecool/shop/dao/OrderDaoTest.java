package com.codecool.shop.dao;

import com.codecool.shop.Tester;
import com.codecool.shop.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrderDaoTest {
    private static DataStore dataStore;

    @BeforeAll
    static void setUp() {
        Tester.getInstance();
        dataStore = DataStore.getInstance();
    }

    @Test
    void testAddOrder() {
        //add new user
        User user = new User("testname", "ordermail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        //add order for new user
        Order order = new Order(new Cart(), user);
        dataStore.orderDao.add(order);

        //check id
        Assertions.assertTrue(order.getId() > 0);
    }

    @Test
    void testSetPayedOrder() {
        //add new user
        User user = new User("testname", "paymail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        //add order for new user
        Order order = new Order(new Cart(), user);
        dataStore.orderDao.add(order);

        //pay order
        dataStore.orderDao.setPayed(order);

        //check status
        Assertions.assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void testGetOrdersOfUser() {
        //add new user
        User user = new User("testname", "ordermail@test.com", "testpass", "1234", null, null, UserStatus.SIGNED);
        dataStore.userDao.add(user);

        //get number of orders before adding a new order
        int before = dataStore.orderDao.getOrdersOf(user).size();

        //add order for new user
        Order order = new Order(new Cart(), user);
        dataStore.orderDao.add(order);

        //get number of orders after adding a new order
        int after = dataStore.orderDao.getOrdersOf(user).size();

        //check
        Assertions.assertEquals(before+1, after);
    }
}