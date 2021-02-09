package com.codecool.shop.dao.database;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoJdbc implements OrderDao {
    private static OrderDaoJdbc instance;
    private DatabaseManager databaseManager;

    private OrderDaoJdbc() { }

    public static OrderDaoJdbc getInstance() {
        if(instance == null) {
            instance = new OrderDaoJdbc();
        }
        return instance;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void add(Order order) {
        try(Connection conn = databaseManager.getConnection()) {
            PreparedStatement st;

            //insert new order, get the id, and update parameter
            String insertOrder = "INSERT INTO orders(name, user_id, is_my_cart, date, order_status) " +
                    "VALUES (?, ?, ?, ?, ?::OrderStatus) " +
                    "RETURNING id";
            int orderId = 0;
            st = conn.prepareStatement(insertOrder);
            st.setString(1, "Order");
            st.setInt(2, order.getUser().getId());
            st.setBoolean(3, false);
            st.setDate(4, new Date(order.getDate().getTime()));
            st.setString(5, order.getStatus().toString());
            ResultSet rs = st.executeQuery();
            if(rs.next()) {
                orderId = rs.getInt(1);
                order.setId(orderId);
            }

            //add new items
            String insertItems = "INSERT INTO items(price_id, order_id, quantity) " +
                    "VALUES (?, ?, ?)";
            st = conn.prepareStatement(insertItems);
            List<Item> items = order.getCart().getItems();
            DatabaseManager.insertOrderItems(st, orderId, items);

            //add event
            String insertEvent = "INSERT INTO events(date, description, order_id) " +
                    "VALUES (?, ?, ?)";
            st = conn.prepareStatement(insertEvent);
            st.setDate(1, new Date(order.getDate().getTime()));
            st.setString(2, "Order created. Status = " + order.getStatus());
            st.setInt(3, orderId);
            st.executeUpdate();

            //close statement
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Add order error => " + exception.getMessage());
        }
    }

    @Override
    public void setPayed(Order order) {
        try(Connection conn = databaseManager.getConnection()) {
            //change order status
            String updateOrderStatus = "UPDATE orders " +
                    "SET order_status = CAST(? AS OrderStatus) " +
                    "WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(updateOrderStatus);
            st.setString(1, OrderStatus.PAID.toString());
            st.setInt(2, order.getId());
            st.executeUpdate();

            //update parameter
            order.pay();

            //add event
            String insertEvent = "INSERT INTO events(date, description, order_id) " +
                    "VALUES (?, ?, ?)";
            st = conn.prepareStatement(insertEvent);
            st.setDate(1, new Date(order.getDate().getTime()));
            st.setString(2, "Order paid. Status = " + order.getStatus());
            st.setInt(3, order.getId());
            st.executeUpdate();

            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Set payed order error => " + exception.getMessage());
        }
    }

    @Override
    public List<Order> getOrdersOf(User user) {
        List<Order> orders = new ArrayList<>();
        PreparedStatement st;
        try (Connection conn = databaseManager.getConnection()) {
            //get user orders
            String ordersQuery = "SELECT id, name, date, order_status " +
                    "FROM orders " +
                    "WHERE user_id = ? " +
                    "AND is_my_cart = false";

            st = conn.prepareStatement(ordersQuery);
            st.setInt(1, user.getId());

            ResultSet ordersResult = st.executeQuery();
            while( ordersResult.next() ) {
                //get order data
                int orderId = ordersResult.getInt("id");
                String name = ordersResult.getString("name");
                java.util.Date date = new java.util.Date(ordersResult.getDate("date").getTime());
                OrderStatus status = OrderStatus.valueOf(ordersResult.getString("order_status"));

                //get order items
                String itemsQuery = "SELECT itm.quantity AS itmQuantity, " +
                        "  pri.sum AS priSum, pri.currency AS priCurrency, pri.date AS priDate, " +
                        "  pro.id AS proId, pro.name AS proName, pro.description AS proDesc, pro.image_file_name AS proImage " +
                        "FROM orders ord, items itm, prices pri, products pro " +
                        "WHERE ord.id = ? " +
                        "  AND itm.order_id = ord.id " +
                        "  AND itm.price_id = pri.id " +
                        "  AND pri.product_id = pro.id";

                st = conn.prepareStatement(itemsQuery);
                st.setInt(1, orderId);

                ResultSet itemsResult = st.executeQuery();
                List<Item> items = new ArrayList<>();
                while (itemsResult.next()) {
                    //create price pri.sum, pri.currency, pri.date
                    double sum = itemsResult.getDouble("priSum");
                    String currency = itemsResult.getString("priCurrency");
                    java.util.Date priDate = itemsResult.getDate("priDate");
                    Price price = new Price(sum, currency, priDate);
                    List<Price> prices = new ArrayList<>();
                    prices.add(price);

                    //create product
                    int proId = itemsResult.getInt("proId");
                    String proName = itemsResult.getString("proName");
                    String proDesc = itemsResult.getString("proDesc");
                    String proImg = itemsResult.getString("proImage");
                    Product product = new Product(proId, proName, proDesc, proImg, prices, null, null);

                    //create item and add to list
                    int quantity = itemsResult.getInt("itmQuantity");
                    items.add(new Item(product, price, quantity));
                }

                //create order and add to list
                Cart cart = new Cart(items);
                orders.add( new Order(orderId, name, date, status, cart) );
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Get orders of user error => " + exception.getMessage());
        }

        return orders;
    }
}
