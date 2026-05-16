package com.e_commerce.project.service;

import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;

public interface ProductService {

    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse getProductsByCategory(Long categoryId);
}
