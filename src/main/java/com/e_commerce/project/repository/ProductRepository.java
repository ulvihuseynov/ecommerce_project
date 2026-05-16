package com.e_commerce.project.repository;

import com.e_commerce.project.model.Category;
import com.e_commerce.project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByCategoryOrderByPriceAsc(Category category);
}
