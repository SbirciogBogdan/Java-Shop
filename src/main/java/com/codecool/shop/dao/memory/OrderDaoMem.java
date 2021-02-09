package com.codecool.shop.dao.memory;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDaoMem implements OrderDao {

    private final List<Order> data = new ArrayList<>();
    private static OrderDaoMem instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    private OrderDaoMem() {
    }

    public static OrderDaoMem getInstance() {
        if (instance == null) {
            instance = new OrderDaoMem();
        }
        return instance;
    }

    @Override
    public void add(Order order) {
        order.setId(data.size() + 1);
        data.add(order);
    }

    @Override
    public void setPayed(Order order) {
        for(Order ord : data) {
            if( order.getId() == ord.getId() ) {
                ord.pay();
                break;
            }
        }
    }

    @Override
    public List<Order> getOrdersOf(User user) {
        if(user == null)
            return null;
        return data.stream().filter(order -> order.getUser().getId() == user.getId()).collect(Collectors.toList());
    }
}
