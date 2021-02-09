package com.codecool.shop.dao;

import com.codecool.shop.dao.database.*;
import com.codecool.shop.dao.memory.*;

import java.io.IOException;

/**
 * Abstraction layer for easy selection of DAO implementation
 */
public class DataStore {
    private DaoImplementations daoImplementation;
    private DatabaseManager databaseManager = null;

    //DAOs
    public CategoryDao categoryDao;
    public SupplierDao supplierDao;
    public ProductDao productDao;
    public UserDao userDao;
    public OrderDao orderDao;

    private static DataStore instance = null;

    private DataStore() {}

    public void SetDaoImplementation(DaoImplementations daoImplementation) {
        this.daoImplementation = daoImplementation;
        switch (daoImplementation) {
            case MEMORY:
                categoryDao = CategoryDaoMem.getInstance();
                supplierDao = SupplierDaoMem.getInstance();
                productDao = ProductDaoMem.getInstance();
                userDao = UserDaoMem.getInstance();
                orderDao = OrderDaoMem.getInstance();
                break;
            case DATABASE:
                categoryDao = CategoryDaoJdbc.getInstance();
                supplierDao = SupplierDaoJdbc.getInstance();
                productDao = ProductDaoJdbc.getInstance();
                userDao = UserDaoJdbc.getInstance();
                orderDao = OrderDaoJdbc.getInstance();
                break;
        }
    }

    public void SetDatabase(String propFile) {
        try {
            databaseManager = DatabaseManager.getInstance(propFile);
            ProductDaoJdbc.getInstance().setDatabaseManager(databaseManager);
            CategoryDaoJdbc.getInstance().setDatabaseManager(databaseManager);
            SupplierDaoJdbc.getInstance().setDatabaseManager(databaseManager);
            UserDaoJdbc.getInstance().setDatabaseManager(databaseManager);
            OrderDaoJdbc.getInstance().setDatabaseManager(databaseManager);
        } catch (IOException e) {
            System.err.println("ERROR: DatabaseManager initialization failed = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DaoImplementations getDaoImplementation() {
        return daoImplementation;
    }

    public static DataStore getInstance() {
        if(instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
