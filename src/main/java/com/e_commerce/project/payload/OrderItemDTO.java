package com.e_commerce.project.payload;

import com.e_commerce.project.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;
}
