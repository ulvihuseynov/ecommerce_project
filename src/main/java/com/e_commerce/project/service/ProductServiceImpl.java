package com.e_commerce.project.service;

import com.e_commerce.project.exception.ResourceNotFoundException;
import com.e_commerce.project.model.Category;
import com.e_commerce.project.model.Product;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;
import com.e_commerce.project.repository.CategoryRepository;
import com.e_commerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()->new ResourceNotFoundException("Category","categoryId",categoryId)
        );

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice= product.getPrice() - ((product.getDiscount()*0.01)* product.getPrice());

        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        ProductResponse productResponse=new ProductResponse();
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product, ProductDTO.class)).toList();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        ProductResponse productResponse=new ProductResponse();

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> products=productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        productResponse.setContent(productDTOS);
        return productResponse;
    }
}
