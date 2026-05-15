package com.e_commerce.project.service;

import com.e_commerce.project.exception.APIException;
import com.e_commerce.project.exception.ResourceNotFoundException;
import com.e_commerce.project.model.Category;
import com.e_commerce.project.payload.CategoryDTO;
import com.e_commerce.project.payload.CategoryResponse;
import com.e_commerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortDirection) {

        Sort sort=sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,sort);


        CategoryResponse categoryResponse=new CategoryResponse();
        Page<Category> categoryPage = categoryRepository.findAll(pageRequest);

        List<Category> categories=categoryPage.getContent();
        if(categories.isEmpty())
            throw  new APIException("No category created till now");


        List<CategoryDTO> categoryDTOS = categories.stream().map(
                c -> modelMapper.map(c, CategoryDTO.class)).toList();

        categoryResponse.setContent(categoryDTOS);

        categoryResponse.setPage(categoryPage.getNumber());
        categoryResponse.setSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLast(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        Category categoryDB= categoryRepository.findByCategoryName(category.getCategoryName());

        if (categoryDB !=null)
            throw new APIException("Category with the name " + category.getCategoryName()+ " already exists");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {

        Optional<Category> optionalCategory = categoryRepository.findById(id);

        Category categoryDB = optionalCategory.orElseThrow(
                () -> new ResourceNotFoundException("Category","categoryId",id));

        categoryRepository.delete(categoryDB);


        return modelMapper.map(categoryDB,CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);

        Category categoryDB = optionalCategory.orElseThrow(() ->
                new ResourceNotFoundException("Category","categoryId",categoryId));

        categoryDB.setCategoryName(category.getCategoryName());

        return modelMapper.map(categoryRepository.save(categoryDB),CategoryDTO.class);
    }
}
