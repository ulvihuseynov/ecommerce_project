package com.e_commerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemDTO {

    private Long cartItemId;
    private CartDTO cartDTO;
    private ProductDTO productDTO;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
