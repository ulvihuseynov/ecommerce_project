package com.e_commerce.project.controller;

import com.e_commerce.project.model.Cart;
import com.e_commerce.project.payload.CartDTO;
import com.e_commerce.project.repository.CartRepository;
import com.e_commerce.project.service.CartService;
import com.e_commerce.project.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final AuthUtil authUtil;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public CartController(AuthUtil authUtil, CartRepository cartRepository, CartService cartService) {
        this.authUtil = authUtil;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity){
        CartDTO savedCartDTO=cartService.addProductToCart(productId,quantity);

        return new ResponseEntity<>(savedCartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){

       List<CartDTO> cartDTOS= cartService.getAllCarts();
       return new ResponseEntity<>(cartDTOS,HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO>getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO= cartService.getCart(cartId,emailId);
        return new ResponseEntity<>(cartDTO,HttpStatus.FOUND);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation){

        CartDTO cartDTO=cartService.updateProductQuantityInCart(productId,operation.equalsIgnoreCase
                ("delete")? -1:1);

        return new ResponseEntity<>(cartDTO,HttpStatus.OK);

    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId){

        String status=cartService.deleteProductFromCart(cartId,productId);

        return new ResponseEntity<>(status,HttpStatus.OK);

    }
}
