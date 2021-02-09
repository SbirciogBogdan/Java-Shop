package com.codecool.shop.dao.database;

import com.codecool.shop.dao.CategoryDao;
import com.codecool.shop.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoJdbc implements CategoryDao {
    private static CategoryDaoJdbc instance;
    private DatabaseManager databaseManager;

    private CategoryDaoJdbc() {
    }

    public static CategoryDaoJdbc getInstance() {
        if (instance == null) {
            instance = new CategoryDaoJdbc();
        }
        return instance;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void add(Category category) {
        String query = "INSERT INTO categories (name, department, description) " +
                "VALUES (?, ?, ?) " +
                "RETURNING id";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, category.getName());
            st.setString(2, category.getDepartment());
            st.setString(3, category.getDescription());

            // execute the prepared statement insert, get id of inserted category, update parameter
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Category add error => " + exception.getMessage());
        }
    }

    @Override
    public Category find(int id) {
        String query = "SELECT id, name, department, description" +
                " FROM categories" +
                " WHERE id = ?";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if (result.next()) {
                String name = result.getString("name");
                String department = result.getString("department");
                String description = result.getString("description");
                return new Category(id, name, department, description);
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Category find error => " + exception.getMessage());
        }
        return null;
    }

    @Override
    public void remove(int id) {
        String query = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement delete
            st.executeUpdate();
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Category remove error => " + exception.getMessage());
        }
    }

    @Override
    public List<Category> getAll() {
        String query = "SELECT id, name, department, description FROM categories";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            while (result.next()) {
                Integer id = result.getInt("id");
                String name = result.getString("name");
                String department = result.getString("department");
                String description = result.getString("description");
                categories.add(new Category(id, name, department, description));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Category get all error => " + exception.getMessage());
        }
        return categories;
    }
}
