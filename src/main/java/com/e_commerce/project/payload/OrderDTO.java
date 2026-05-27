package com.e_commerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItemDTOS;
    private PaymentDTO paymentDTO;
    private LocalDate orderDate;
    private String status;
    private Double totalAmount;
    private Long addressId;
}
