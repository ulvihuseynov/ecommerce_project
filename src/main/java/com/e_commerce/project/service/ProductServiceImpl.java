package com.e_commerce.project.service;

import com.e_commerce.project.exception.APIException;
import com.e_commerce.project.exception.ResourceNotFoundException;
import com.e_commerce.project.model.Category;
import com.e_commerce.project.model.Product;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;
import com.e_commerce.project.repository.CategoryRepository;
import com.e_commerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()->new ResourceNotFoundException("Category","categoryId",categoryId)
        );

        boolean isProductNotPresent=true;
        List<Product> products = category.getProducts();
        for (Product product: products)
             {
                 if (product.getProductName().equals(productDTO.getProductName())){
                     isProductNotPresent=false;
                     break;
                 }

             }

       if (isProductNotPresent){
           Product product = modelMapper.map(productDTO, Product.class);


           product.setCategory(category);
           product.setImage("default.png");

           double specialPrice= product.getPrice() - ((product.getDiscount()*0.01)* product.getPrice());

           product.setSpecialPrice(specialPrice);

           Product savedProduct = productRepository.save(product);
           return modelMapper.map(savedProduct, ProductDTO.class);
       }else {
           throw new APIException("Product already exist");
       }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {

        ProductResponse productResponse=new ProductResponse();

       Sort sort= sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,sort);

        Page<Product> page = productRepository.findAll(pageRequest);
        List<Product> products=page.getContent();

        if (products.isEmpty())
            throw new APIException("No product created till now");

        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product, ProductDTO.class)).toList();

        productResponse.setContent(productDTOS);

        productResponse.setPage(page.getNumber());
        productResponse.setSize(page.getSize());
        productResponse.setTotalPages(page.getTotalPages());
        productResponse.setTotalElements(page.getTotalElements());
        productResponse.setLast(page.isLast());



        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        ProductResponse productResponse=new ProductResponse();

        Sort sort= sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,sort);


        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Page<Product>page= productRepository.findByCategoryOrderByPriceAsc(category, pageRequest);

        List<Product> products = page.getContent();

        if (products.isEmpty())
            throw new APIException("No product created till now");

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        productResponse.setContent(productDTOS);

        productResponse.setPage(page.getNumber());
        productResponse.setSize(page.getSize());
        productResponse.setTotalPages(page.getTotalPages());
        productResponse.setTotalElements(page.getTotalElements());
        productResponse.setLast(page.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword,Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        ProductResponse productResponse=new ProductResponse();
       Sort sort= sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> page= productRepository.findByProductNameLikeIgnoreCase("%"+keyword+"%", pageRequest);
        List<Product> products = page.getContent();

        if (products.isEmpty()){
            throw new APIException("Products not found with keyword  " + keyword);
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        productResponse.setContent(productDTOS);

        productResponse.setPage(page.getNumber());
        productResponse.setSize(page.getSize());
        productResponse.setTotalPages(page.getTotalPages());
        productResponse.setTotalElements(page.getTotalElements());
        productResponse.setLast(page.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product product = modelMapper.map(productDTO, Product.class);

        Product productDB = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "productId", productId));

        productDB.setProductName(product.getProductName());
        productDB.setDescription(product.getDescription());
        productDB.setQuantity(product.getQuantity());
        productDB.setDiscount(product.getDiscount());
        productDB.setPrice(product.getPrice());
        productDB.setSpecialPrice(product.getSpecialPrice());


        return modelMapper.map(productRepository.save(productDB), ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile multipartFile) throws Exception {

        Product productDB = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );


        String fileName=fileService.uploadImage(path,multipartFile);

        productDB.setImage(fileName);


        return modelMapper.map(productRepository.save(productDB), ProductDTO.class);
    }


}
