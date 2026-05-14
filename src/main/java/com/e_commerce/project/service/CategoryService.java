package com.e_commerce.project.service;

import com.e_commerce.project.model.Category;
import com.e_commerce.project.payload.CategoryDTO;
import com.e_commerce.project.payload.CategoryResponse;
import org.springframework.web.bind.annotation.RequestParam;


public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long id);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
