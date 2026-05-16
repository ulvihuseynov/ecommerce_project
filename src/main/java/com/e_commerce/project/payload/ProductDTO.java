package com.e_commerce.project.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String description;
    private String image;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;
}
