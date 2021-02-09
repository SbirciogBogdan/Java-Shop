package com.codecool.shop.dao.memory;


import com.codecool.shop.dao.CategoryDao;
import com.codecool.shop.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDaoMem implements CategoryDao {

    private final List<Category> data = new ArrayList<>();
    private static CategoryDaoMem instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    private CategoryDaoMem() {
    }

    public static CategoryDaoMem getInstance() {
        if (instance == null) {
            instance = new CategoryDaoMem();
        }
        return instance;
    }

    @Override
    public void add(Category category) {
        category.setId(data.size() + 1);
        data.add(category);
    }

    @Override
    public Category find(int id) {
        return data.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        data.remove(find(id));
    }

    @Override
    public List<Category> getAll() {
        return data;
    }
}
