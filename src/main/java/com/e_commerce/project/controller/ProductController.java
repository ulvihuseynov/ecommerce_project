package com.e_commerce.project.controller;

import com.e_commerce.project.config.AppConstants;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;
import com.e_commerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){

        ProductDTO savedProductDTO=productService.addProduct(productDTO,categoryId);

        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);

    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                          @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                          @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCT_BY,required = false) String sortBy,
                                                          @RequestParam(name="sortDirection",defaultValue = AppConstants.SORT_DIR,required = false) String sortDirection
                                                          ){

        return new ResponseEntity<>(productService.getAllProducts(pageNumber,pageSize,sortBy,sortDirection),HttpStatus.OK);
    }
    @GetMapping("/public/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                 @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                                 @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCT_BY,required = false) String sortBy,
                                                                 @RequestParam(name="sortDirection",defaultValue = AppConstants.SORT_DIR,required = false) String sortDirection){

       return new ResponseEntity<>( productService.getProductsByCategory(categoryId,pageNumber,pageSize,sortBy,sortDirection),HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                                @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCT_BY,required = false) String sortBy,
                                                                @RequestParam(name="sortDirection",defaultValue = AppConstants.SORT_DIR,required = false) String sortDirection){

        return new ResponseEntity<>( productService.getProductsByKeyword(keyword,pageNumber,pageSize,sortBy,sortDirection),HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Long productId){

           ProductDTO savedProductDTO= productService.updateProduct(productDTO,productId);

           return new ResponseEntity<>(savedProductDTO,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){

       ProductDTO deleteProductDTO= productService.deleteProduct(productId);
       return new ResponseEntity<>(deleteProductDTO,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam(name = "image")MultipartFile multipartFile) throws Exception {

        ProductDTO productDTO=productService.updateProductImage(productId,multipartFile);

        return new ResponseEntity<>(productDTO,HttpStatus.OK);

    }
}
