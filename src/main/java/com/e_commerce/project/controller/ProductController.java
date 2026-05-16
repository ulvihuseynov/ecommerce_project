package com.e_commerce.project.controller;

import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;
import com.e_commerce.project.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){

        ProductDTO savedProductDTO=productService.addProduct(productDTO,categoryId);

        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);

    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){

        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
    }
    @GetMapping("/public/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId){

       return new ResponseEntity<>( productService.getProductsByCategory(categoryId),HttpStatus.OK);
    }
}
