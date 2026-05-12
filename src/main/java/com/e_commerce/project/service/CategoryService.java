package com.e_commerce.project.service;

import com.e_commerce.project.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long id);

    Category updateCategory(Category category, Long categoryId);
}
