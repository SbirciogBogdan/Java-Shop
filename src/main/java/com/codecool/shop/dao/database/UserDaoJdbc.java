package com.codecool.shop.dao.database;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private static UserDaoJdbc instance;
    private DatabaseManager databaseManager;

    private UserDaoJdbc() {
    }

    public static UserDaoJdbc getInstance() {
        if (instance == null) {
            instance = new UserDaoJdbc();
        }
        return instance;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void add(User user) {
        String usersQuery = "INSERT INTO users (name, email, password, " +
                "phone_number, user_status) " +
                "VALUES (?, ?, ?, ?, ?::UserStatus) " +
                "RETURNING id";

        String ordersQuery = "INSERT INTO orders (name, user_id, is_my_cart, " +
                "date, order_status) " +
                "VALUES (?, ?, ?, ?, ?::OrderStatus) ";

        try (Connection conn = databaseManager.getConnection()) {
            //insert new user
            PreparedStatement st = conn.prepareStatement(usersQuery);
            st.setString(1, user.getName());
            st.setString(2, user.getEmail());
            st.setString(3, user.getPassword());
            st.setString(4, user.getPhoneNumber());
            st.setString(5, user.getUserStatus().toString());

            // execute the prepared statement insert, get id of inserted user,
            // update parameter
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

            //insert empty "my cart" order for the user
            st = conn.prepareStatement(ordersQuery);
            st.setString(1, "MyCart");
            st.setInt(2, user.getId());
            st.setBoolean(3, true);
            st.setDate(4, new Date(new java.util.Date().getTime()));
            st.setString(5, OrderStatus.CHECKED.toString());

            // execute the prepared statement insert
            st.executeUpdate();

            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: User add error => " + exception.getMessage());
        }
    }

    @Override
    public boolean isSignedUp(String email) {
        String query = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, email);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if (result.next()) {
                st.close();
                return true;
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: User isSignedUp error => " + exception.getMessage());
        }
        return false;
    }

    @Override
    public User getAuthenticatedUser(String email, String password) {
        String query = "SELECT id, name, email, password, phone_number, " +
                "billing_id, shipping_id, user_status " +
                "FROM users " +
                "WHERE email = ? and password = ?";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, email);
            st.setString(2, password);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                String phone = result.getString("phone_number");
                int shipId = result.getInt("shipping_id");
                int billId = result.getInt("billing_id");
                UserStatus status = UserStatus.valueOf(result.getString("user_status"));

                //3 selects here - should be optimized somehow
                Address shipping = getAddressById(shipId);
                Address billing = getAddressById(billId);
                Cart myCart = new Cart(getMyCartItemsByUserId(id));

                return new User(id, name, email, password, phone, billing, shipping, status, myCart);
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product find error => " + exception.getMessage());
        }
        return null;
    }

    // TODO: 27.08.2020 rewrite select in getAuthenticatedUser to also extract addresses data and delete this function
    private Address getAddressById(int id) {
        String query = "SELECT id, country, city, zipcode, home_address" +
                " FROM addresses" +
                " WHERE id = ?";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if (result.next()) {
                String country = result.getString("country");
                String city = result.getString("city");
                String zipcode = result.getString("zipcode");
                String home = result.getString("home_address");
                return new Address(id, country, city, zipcode, home);
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Get address by id error => " + exception.getMessage());
        }
        return null;
    }

    // TODO: 27.08.2020 rewrite select in getAuthenticatedUser to also extract myCart items data and delete this
    //  function
    private List<Item> getMyCartItemsByUserId(int id) {
        String query = "SELECT itm.quantity AS itmQuantity, " +
                "     pri.sum AS priSum, pri.currency AS priCurrency, pri.date AS priDate, " +
                "     pro.id AS proId, pro.name AS proName, pro.description AS proDesc, pro.image_file_name AS proImage, " +
                "     sup.id AS supId, sup.name AS supName, sup.description AS supDesc, " +
                "     cat.id AS catId, cat.name AS catName, cat.department AS catDept, cat.description AS catDesc " +
                "FROM users usr, orders ord, items itm, prices pri, " +
                "     products pro, suppliers sup, categories cat " +
                "WHERE usr.id = ? " +
                "  AND ord.user_id = usr.id " +
                "  AND ord.is_my_cart = true " +
                "  AND itm.order_id = ord.id " +
                "  AND itm.price_id = pri.id " +
                "  AND pri.product_id = pro.id " +
                "  AND pro.supplier_id = sup.id " +
                "  AND pro.category_id = cat.id";

        List<Item> items = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            while (result.next()) {
                //create supplier
                int supId = result.getInt("supId");
                String supName = result.getString("supName");
                String supDesc = result.getString("supDesc");
                Supplier sup = new Supplier(supId, supName, supDesc);

                //create category
                int catId = result.getInt("catId");
                String catName = result.getString("catName");
                String catDept = result.getString("catDept");
                String catDesc = result.getString("catDesc");
                Category cat = new Category(catId, catName, catDept, catDesc);

                //create price pri.sum, pri.currency, pri.date
                double sum = result.getDouble("priSum");
                String currency = result.getString("priCurrency");
                java.util.Date date = result.getDate("priDate");
                Price price = new Price(sum, currency, date);
                List<Price> prices = new ArrayList<>();
                prices.add(price);

                //create product
                int proId = result.getInt("proId");
                String proName = result.getString("proName");
                String proDesc = result.getString("proDesc");
                String proImg = result.getString("proImage");
                Product product = new Product(proId, proName, proDesc, proImg, prices, cat, sup);

                //create item and add to list
                int quantity = result.getInt("itmQuantity");
                items.add(new Item(product, price, quantity));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Get may cart items by id error => " + exception.getMessage());
        }

        return items;
    }

    @Override
    public void remove(User user) {
        String query = "DELETE FROM users WHERE id = ?";

        try(Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, user.getId());

            // execute the prepared statement delete
            st.executeUpdate();
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: User remove error => " + exception.getMessage());
        }
    }

    @Override
    public void updateUser(User user, String name, String email, String password, String phone, Address billing, Address shipping) {
        String insertAddressQuery = "INSERT INTO addresses(country, city, zipcode, home_address) " +
                "VALUES (?, ?, ?, ?) " +
                "RETURNING id";

        String updateAddressQuery = "UPDATE addresses SET country = ?, city = ?, zipcode = ?, home_address = ? " +
                "WHERE id = ?";

        String updateUserQuery = "UPDATE users SET name = ?, email = ?, password = ?, phone_number = ?, " +
                "billing_id = ?, shipping_id = ? " +
                "WHERE id = ?";

        try(Connection conn = databaseManager.getConnection()) {
            PreparedStatement st;
            int shippingId = 0, billingId = 0;

            //update shipping
            if (user.getShipping() == null) { //no shipping address yet
                // TODO: 2.09.2020 write auxiliary functions to avoid code duplication
                //set parameters of shipping address insert
                st = conn.prepareStatement(insertAddressQuery);
                st.setString(1, shipping.getCountry());
                st.setString(2, shipping.getCity());
                st.setString(3, shipping.getZipcode());
                st.setString(4, shipping.getHomeAddress());

                //execute insert and get id
                st.execute();
                ResultSet rs = st.getResultSet();
                if (rs.next()) {
                    shippingId = rs.getInt(1);
                }

                //update shipping
                shipping.setId(shippingId);
            } else { //there is a shipping address to update
                //set parameters of shipping address update
                st = conn.prepareStatement(updateAddressQuery);
                st.setString(1, shipping.getCountry());
                st.setString(2, shipping.getCity());
                st.setString(3, shipping.getZipcode());
                st.setString(4, shipping.getHomeAddress());
                st.setInt(5, shipping.getId());

                //execute update
                st.executeUpdate();
            }
            user.setShipping(shipping);

            //update billing
            if (user.getBilling() == null) {
                //set parameters of billing address insert
                st = conn.prepareStatement(insertAddressQuery);
                st.setString(1, billing.getCountry());
                st.setString(2, billing.getCity());
                st.setString(3, billing.getZipcode());
                st.setString(4, billing.getHomeAddress());

                //execute insert and get id
                st.execute();
                ResultSet rs = st.getResultSet();
                if (rs.next()) {
                    billingId = rs.getInt(1);
                }

                //update billing
                billing.setId(billingId);
            } else {
                //set parameters of billing address update
                st = conn.prepareStatement(updateAddressQuery);
                st.setString(1, billing.getCountry());
                st.setString(2, billing.getCity());
                st.setString(3, billing.getZipcode());
                st.setString(4, billing.getHomeAddress());
                st.setInt(5, billing.getId());

                //execute update
                st.executeUpdate();
            }
            user.setBilling(billing);

            //set parameters of user update
            st = conn.prepareStatement(updateUserQuery);
            st.setString(1, name);
            st.setString(2, email);
            st.setString(3, password);
            st.setString(4, phone);
            st.setInt(5, billingId);
            st.setInt(6, shippingId);
            st.setInt(7, user.getId());

            //execute update of user data in database
            st.executeUpdate();

            //update user parameter
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhoneNumber(phone);

            //close statement
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: User update error => " + exception.getMessage());
        }
    }

    @Override
    public void updateUserCart(User user, Cart cart) {
        try(Connection conn = databaseManager.getConnection()) {
            PreparedStatement st;

            //get id of user cart
            int orderId = 0;
            String getCartIdQuery = "SELECT id FROM orders WHERE user_id = ? AND is_my_cart = true";
            st = conn.prepareStatement(getCartIdQuery);
            st.setInt(1, user.getId());
            ResultSet rs = st.executeQuery();
            if(rs.next()) {
                orderId = rs.getInt(1);
            }

            //delete old items
            String deleteItemsQuery = "DELETE FROM items WHERE order_id = ?";
            st = conn.prepareStatement(deleteItemsQuery);
            st.setInt(1, orderId);
            st.executeUpdate();

            //add new items
            String insertItemsQuery = "INSERT INTO items(price_id, order_id, quantity) " +
                    "VALUES (?, ?, ?)";
            st = conn.prepareStatement(insertItemsQuery);
            List<Item> items = cart.getItems();
            DatabaseManager.insertOrderItems(st, orderId, items);

            //update parameter
            user.setMyCart(cart);

            //close statement
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: User cart update error => " + exception.getMessage());
        }
    }
}
