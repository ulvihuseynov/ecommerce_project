package com.e_commerce.project.service;

import com.e_commerce.project.payload.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(String emailId, String paymentMethod, Long paymentId, String pgName, String getPgPaymentId,String pgResponseMessage, String pgStatus, Long addressId);
}
