package com.e_commerce.project.model;

import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private String description;
    private String image;

    private Integer quantity;

    private double price;

    private double discount;
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            orphanRemoval = true,fetch = FetchType.EAGER)
    private List<CartItem> cartItems=new ArrayList<>();
}
