package com.codecool.shop.dao.database;

import com.codecool.shop.dao.DataStore;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Category;
import com.codecool.shop.model.Price;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDaoJdbc implements ProductDao{
    private static ProductDaoJdbc instance;
    private DatabaseManager databaseManager;

    private ProductDaoJdbc() { }

    public static ProductDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ProductDaoJdbc();
        }
        return instance;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void add(Product product) {
        String productsQuery = "INSERT INTO products ( name, description, " +
                "image_file_name, supplier_id, category_id ) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "RETURNING id";

        String pricesQuery = "INSERT INTO prices ( product_id, sum, currency, date )" +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection()){
            //set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(productsQuery);
            st.setString(1, product.getName());
            st.setString(2, product.getDescription());
            st.setString(3, product.getImageFileName());
            st.setInt(4, product.getSupplier().getId());
            st.setInt(5, product.getCategory().getId());

            //execute the prepared statement insert, get id of inserted product, update parameter
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                product.setId(rs.getInt(1));
            }

            //reuse statement to insert the product prices
            List<Price> prices = product.getPrices();
            st = conn.prepareStatement(pricesQuery);
            for (Price price : prices) {
                st.setInt(1, product.getId());
                st.setDouble(2, price.getSum());
                st.setString(3, price.getCurrency().toString());
                st.setDate(4, new java.sql.Date(price.getDate().getTime()));
                st.addBatch();
            }
            st.executeBatch();
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product add error => " + exception.getMessage());
        }
    }

    @Override
    public Product find(int id) {
        String query = "SELECT id, name, description, image_file_name, supplier_id, category_id" +
                " FROM products" +
                " WHERE id = ?";

        try (Connection conn = databaseManager.getConnection()){
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if(result.next()) {
                DataStore dataStore = DataStore.getInstance();
                String name = result.getString("name");
                String desc = result.getString("description");
                String image = result.getString("image_file_name");
                int supId = result.getInt("supplier_id");
                int catId = result.getInt("category_id");

                List<Price> prices = getPricesById(id);
                Supplier supplier = dataStore.supplierDao.find(supId);
                Category category = dataStore.categoryDao.find(catId);

                return new Product(id, name, desc, image, prices, category, supplier);
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product find error => " + exception.getMessage());
        }
        return null;
    }

    private List<Price> getPricesById(int id) {
        String query = "SELECT sum, currency, date" +
                " FROM prices" +
                " WHERE product_id = ?";

        List<Price> prices = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection()){
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            while(result.next()) {
                double sum = result.getDouble("sum");
                String currency = result.getString("currency");
                Date date = result.getDate("date");
                prices.add(new Price(sum, currency, date));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product get prices error => " + exception.getMessage());
        }
        return prices;
    }

    @Override
    public void remove(int id) {
        String query = "DELETE FROM products WHERE id = ?";

        try(Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement delete
            st.executeUpdate();
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product remove error => " + exception.getMessage());
        }
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT id, name, description, image_file_name, supplier_id, category_id" +
                " FROM products";

        List<Product> products = new ArrayList<>();
        try(Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            DataStore dataStore = DataStore.getInstance();
            while(result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                String desc = result.getString("description");
                String image = result.getString("image_file_name");
                int supId = result.getInt("supplier_id");
                int catId = result.getInt("category_id");

                List<Price> prices = getPricesById(id);
                Supplier supplier = dataStore.supplierDao.find(supId);
                Category category = dataStore.categoryDao.find(catId);

                products.add(new Product(id, name, desc, image, prices, category, supplier));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product get all error => " + exception.getMessage());
        }
        return products;
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        String query = "SELECT id, name, description, image_file_name, supplier_id, category_id" +
                " FROM products" +
                " WHERE supplier_id = ?";

        List<Product> products = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection()){
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, supplier.getId());

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            DataStore dataStore = DataStore.getInstance();
            while(result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                String desc = result.getString("description");
                String image = result.getString("image_file_name");
                int catId = result.getInt("category_id");

                List<Price> prices = getPricesById(id);
                Category category = dataStore.categoryDao.find(catId);

                products.add(new Product(id, name, desc, image, prices, category, supplier));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product get by supplier error => " + exception.getMessage());
        }

        return products;
    }

    @Override
    public List<Product> getBy(Category category) {
        String query = "SELECT id, name, description, image_file_name, supplier_id, category_id" +
                " FROM products" +
                " WHERE category_id = ?";

        List<Product> products = new ArrayList<>();
        try(Connection conn = databaseManager.getConnection()){
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, category.getId());

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            DataStore dataStore = DataStore.getInstance();
            while(result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                String desc = result.getString("description");
                String image = result.getString("image_file_name");
                int supId = result.getInt("supplier_id");

                List<Price> prices = getPricesById(id);
                Supplier supplier = dataStore.supplierDao.find(supId);

                products.add(new Product(id, name, desc, image, prices, category, supplier));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product get by category error => " + exception.getMessage());
        }
        return products;
    }

    @Override
    public int getCurrentPriceIdByProduct(int id) {
        String query = "SELECT id " +
                "FROM prices " +
                "WHERE product_id = ? " +
                "ORDER BY date DESC " +
                "LIMIT 1";

        int priceId = 0;
        try (Connection conn = databaseManager.getConnection()){
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if(result.next()) {
                priceId = result.getInt(1);
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Product get prices error => " + exception.getMessage());
        }
        return priceId;
    }
}
