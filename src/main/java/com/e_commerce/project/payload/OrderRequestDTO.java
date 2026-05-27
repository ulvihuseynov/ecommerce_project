package com.e_commerce.project.payload;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderRequestDTO {

    private Long paymentId;
    @NotBlank
    @Size(min = 4,message = "Payment method must contain at least 4 characters")
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
    private Long addressId;
}
