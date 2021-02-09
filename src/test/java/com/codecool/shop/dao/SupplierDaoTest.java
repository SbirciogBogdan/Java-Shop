package com.codecool.shop.dao;

import com.codecool.shop.Tester;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class SupplierDaoTest {
    private static DataStore dataStore;

    @BeforeAll
    static void setUp() {
        Tester.getInstance();
        dataStore = DataStore.getInstance();
    }

    @Test
    void testAddSupplier() {
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Assertions.assertTrue(prada.getId() > 0);
    }

    @Test
    void testFindSupplier() {
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        Supplier dbPrada = dataStore.supplierDao.find(prada.getId());
        Assertions.assertAll("Supplier:",
                () -> Assertions.assertEquals(prada.getId(), dbPrada.getId()),
                () -> Assertions.assertEquals(prada.getName(), dbPrada.getName()),
                () -> Assertions.assertEquals(prada.getDescription(), dbPrada.getDescription())
        );
    }

    @Test
    void testRemoveSupplier() {
        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        dataStore.supplierDao.add(prada);
        int before = dataStore.supplierDao.getAll().size();
        dataStore.supplierDao.remove(prada.getId());
        Assertions.assertEquals(dataStore.supplierDao.getAll().size(), before - 1);
    }

    @Test
    void testGetAllSuppliers() {
        List<Supplier> supplierList = dataStore.supplierDao.getAll();
        int before = supplierList.size();

        Supplier prada = new Supplier("Prada", "Luxury fashion house, specializing in leather handbags, travel " +
                "accessories, shoes, ready-to-wear, perfumes ");
        Supplier bvlgari = new Supplier("Bvlgari", "Luxury brand known for its jewellery, watches, fragrances, " +
                "accessories and leather goods");
        dataStore.supplierDao.add(prada);
        dataStore.supplierDao.add(bvlgari);

        supplierList = dataStore.supplierDao.getAll();
        int after = supplierList.size();

        Assertions.assertEquals(after, before + 2);
    }
}