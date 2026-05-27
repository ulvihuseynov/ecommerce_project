package com.e_commerce.project.controller;

import com.e_commerce.project.payload.OrderDTO;
import com.e_commerce.project.payload.OrderRequestDTO;
import com.e_commerce.project.service.OrderService;
import com.e_commerce.project.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;

    public OrderController(OrderService orderService, AuthUtil authUtil) {
        this.orderService = orderService;
        this.authUtil = authUtil;
    }

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDTO orderRequestDTO){

        String emailId = authUtil.loggedInEmail();
      OrderDTO orderDTO=  orderService.placeOrder(
                emailId,
                paymentMethod,
              orderRequestDTO.getPaymentId(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgResponseMessage(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getAddressId()

        );
      return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);

    }
}
