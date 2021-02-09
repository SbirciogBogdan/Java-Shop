package com.codecool.shop;

import com.codecool.shop.dao.DaoImplementations;
import com.codecool.shop.dao.DataStore;

import java.io.InputStream;
import java.util.Properties;

public class Tester {
    DataStore dataStore;
    private static Tester instance;

    // edit storage property in testing.properties file to change storage implementation tested:
    //   storage=database - database storage will be tested (using test database)
    //   storage=memory   - memory storage will be tested
    DaoImplementations daoImplementation;

    public static Tester getInstance() {
        if( instance == null ) {
            instance = new Tester();
        }
        return instance;
    }

    private void setProperties() {
        Properties properties = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("testing.properties");

        try {
            properties.load(inputStream);
        } catch (Exception exception) {
            System.err.println("ERROR: Tester initialization error => " + exception.getMessage());
        }

        String storage = properties.getProperty("storage");
        if( storage.equals("database") ) {
            daoImplementation = DaoImplementations.DATABASE;
        } else if( storage.equals("memory") ) {
            daoImplementation = DaoImplementations.MEMORY;
        } else {
            System.err.println("ERROR: Incorrect option in testing.properties file. Valid options: database, memory.");
        }
    }

    private Tester() {
        dataStore = DataStore.getInstance();
        setProperties();
        dataStore.SetDaoImplementation(daoImplementation);
        if (dataStore.getDaoImplementation() == DaoImplementations.DATABASE) {
            dataStore.SetDatabase("test_connection.properties");
            dataStore.getDatabaseManager().initDatabase(); //reset test database
        }
    }
}
