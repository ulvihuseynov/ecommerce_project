package com.e_commerce.project.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    @NotBlank
    @Size(min = 5,message = "Product name must contain at least 5 characters")
    private String productName;

    @NotBlank
    @Size(min = 5,max = 40, message = "Product description name must contain at least 5 and 40 characters")
    private String description;
    private String image;

    @Min(value = 0,message = "Quantity is not negative")
    private Integer quantity;

    @Min(value = 0,message = "Price is not negative")
    private double price;

    @Min(value = 0,message = "Discount is not negative")
    private double discount;
    private double specialPrice;
}
