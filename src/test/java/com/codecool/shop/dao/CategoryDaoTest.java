package com.codecool.shop.dao;

import com.codecool.shop.Tester;
import com.codecool.shop.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class CategoryDaoTest {
    private static DataStore dataStore;

    @BeforeAll
    static void setUp() {
        Tester.getInstance();
        dataStore = DataStore.getInstance();
    }

    @Test
    void testAddCategory() {
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an ornament");
        dataStore.categoryDao.add(ring);
        Assertions.assertTrue(ring.getId() > 0);
    }

    @Test
    void testFindCategory() {
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an ornament");
        dataStore.categoryDao.add(ring);
        Category dbRing = dataStore.categoryDao.find(ring.getId());
        Assertions.assertAll("Category:",
                () -> Assertions.assertEquals(ring.getId(), dbRing.getId()),
                () -> Assertions.assertEquals(ring.getName(), dbRing.getName()),
                () -> Assertions.assertEquals(ring.getDepartment(), dbRing.getDepartment()),
                () -> Assertions.assertEquals(ring.getDescription(), dbRing.getDescription())
        );
    }

    @Test
    void testRemoveCategory() {
        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an ornament");
        dataStore.categoryDao.add(ring);
        int before = dataStore.categoryDao.getAll().size();
        dataStore.categoryDao.remove(ring.getId());
        Assertions.assertEquals(dataStore.categoryDao.getAll().size(), before - 1);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categoryList = dataStore.categoryDao.getAll();
        int before = categoryList.size();

        Category ring = new Category("Ring", "Jewelry", "Circular band, often set with gems, for wearing as an " +
                "ornament");
        Category necklace = new Category("Necklace", "Jewelry", "String of stones, beads, jewels, or the like, for " +
                "wearing");
        Category earrings = new Category("Earrings", "Jewelry", "Ornaments worn as accessories");
        dataStore.categoryDao.add(ring);
        dataStore.categoryDao.add(necklace);
        dataStore.categoryDao.add(earrings);

        categoryList = dataStore.categoryDao.getAll();
        int after = categoryList.size();

        Assertions.assertEquals(after, before + 3);
    }
}