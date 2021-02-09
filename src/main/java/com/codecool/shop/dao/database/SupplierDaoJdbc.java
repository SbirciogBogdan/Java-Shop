package com.codecool.shop.dao.database;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoJdbc implements SupplierDao {
    private static SupplierDaoJdbc instance;
    private DatabaseManager databaseManager;

    private SupplierDaoJdbc() { }

    public static SupplierDaoJdbc getInstance() {
        if(instance == null) {
            instance = new SupplierDaoJdbc();
        }
        return instance;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void add(Supplier supplier) {
        String query = "INSERT INTO suppliers ( name, description ) " +
                "VALUES (?, ?) " +
                "RETURNING id";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, supplier.getName());
            st.setString(2, supplier.getDescription());

            // execute the prepared statement insert, get id of inserted supplier, update parameter
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                supplier.setId(rs.getInt(1));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Supplier add error => " + exception.getMessage());
        }
    }

    @Override
    public Supplier find(int id) {
        String query = "SELECT id, name, description" +
                " FROM suppliers" +
                " WHERE id = ?";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            if (result.next()) {
                String name = result.getString("name");
                String description = result.getString("description");
                return new Supplier(id, name, description);
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Supplier find error => " + exception.getMessage());
        }
        return null;
    }

    @Override
    public void remove(int id) {
        String query = "DELETE FROM suppliers WHERE id = ?";

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);

            // execute the prepared statement delete
            st.executeUpdate();
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Supplier remove error => " + exception.getMessage());
        }
    }

    @Override
    public List<Supplier> getAll() {
        String query = "SELECT id, name, description FROM suppliers";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection()) {
            // set all the prepared statement parameters
            PreparedStatement st = conn.prepareStatement(query);

            // execute the prepared statement select
            ResultSet result = st.executeQuery();
            while (result.next()) {
                Integer id = result.getInt("id");
                String name = result.getString("name");
                String description = result.getString("description");
                suppliers.add(new Supplier(id, name, description));
            }
            st.close();
        } catch (SQLException exception) {
            System.err.println("ERROR: Supplier get all error => " + exception.getMessage());
        }
        return suppliers;
    }
}
