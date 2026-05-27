package com.e_commerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Order order;

    private String paymentMethod;

    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

    public Payment(String paymentMethod,String  pgPaymentId,String  pgStatus,String pgResponseMessage,String pgName){
        this.paymentMethod=paymentMethod;
        this.pgPaymentId=pgPaymentId;
        this.pgName=pgName;
        this.pgStatus=pgStatus;
        this.pgResponseMessage=pgResponseMessage;
    }
}
