package com.e_commerce.project.controller;

import com.e_commerce.project.model.Category;
import com.e_commerce.project.payload.CategoryDTO;
import com.e_commerce.project.payload.CategoryResponse;
import com.e_commerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/public/categories")
    //@RequestMapping(value = "/public/categories",method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse>getAllCategories (@RequestParam(name="pageNumber") Integer pageNumber,
                                                             @RequestParam(name="pageSize")Integer pageSize){
        return new ResponseEntity<>(categoryService.getAllCategories(pageNumber,pageSize),HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory (@Valid @RequestBody CategoryDTO categoryDTO){

        CategoryDTO savedDategoryDto = categoryService.createCategory(categoryDTO);

        return new ResponseEntity<>(savedDategoryDto,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){

          return ResponseEntity.ok().body( categoryService.deleteCategory(categoryId));


    }
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){

            CategoryDTO savedCategory=categoryService.updateCategory(categoryDTO,categoryId);

            return new ResponseEntity<>(savedCategory,HttpStatus.OK);

    }
}
