package com.e_commerce.project.service;

import com.e_commerce.project.payload.CartDTO;

import java.util.List;

public interface CartService {

   CartDTO addProductToCart(Long productId, Integer quantity);

   List<CartDTO> getAllCarts();

   CartDTO getCart(Long cartId,String emailId);


   CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

   String deleteProductFromCart(Long cartId, Long productId);

   void updateProductInCarts(Long cartId, Long productId);
}
