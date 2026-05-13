package com.e_commerce.project.service;

import com.e_commerce.project.model.Category;
import com.e_commerce.project.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {

    categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long id) {

        Optional<Category> optionalCategory = categoryRepository.findById(id);

        Category categoryDB = optionalCategory.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found"));

        categoryRepository.delete(categoryDB);


        return "successfully deleted " + id;
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);

        Category categoryDB = optionalCategory.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));

        categoryDB.setCategoryName(category.getCategoryName());

        return categoryRepository.save(categoryDB);
    }
}
