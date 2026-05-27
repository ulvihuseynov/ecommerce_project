package com.e_commerce.project.service;

import com.e_commerce.project.exception.APIException;
import com.e_commerce.project.exception.ResourceNotFoundException;
import com.e_commerce.project.model.Cart;
import com.e_commerce.project.model.CartItem;
import com.e_commerce.project.model.Product;
import com.e_commerce.project.payload.CartDTO;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.repository.CartItemRepository;
import com.e_commerce.project.repository.CartRepository;
import com.e_commerce.project.repository.ProductRepository;
import com.e_commerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, ModelMapper modelMapper, AuthUtil authUtil) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
        this.authUtil=authUtil;
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart=createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),
                productId);

        if (cartItem!=null){
            throw new APIException("Product "+ product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity()==0){
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity()<quantity){
            throw new APIException("Please make an order of the " + product.getProductName()
            + " less than or equal to the quantity " + product.getQuantity());
        }

        CartItem newCartItem=new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getPrice());

       cartItemRepository.save(newCartItem);

       product.setQuantity(product.getQuantity()-quantity);
       cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
       cartRepository.save(cart);
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productDTOS = cartItems.stream().map(item ->{
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        }).toList();

        cartDTO.setProductDTOS(productDTOS);
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()){
            throw new APIException("No cart exist");
        }
       carts.forEach(c->c.getCartItems().forEach(cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity())));

        return carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS= cart.getCartItems().stream().map(cartItem -> modelMapper.map(cartItem.getProduct(),
                    ProductDTO.class)).toList();
            cartDTO.setProductDTOS(productDTOS);
            return cartDTO;
        }).toList();
    }

    @Override
    public CartDTO getCart(Long cartId,String emailId) {

        Cart cart=cartRepository.findCartByEmailAndCartId(emailId,cartId);

        if (cart==null){
            throw new ResourceNotFoundException("Cart","cartId",cartId);
        }
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setProductDTOS(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity()==0){
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity()<quantity){
            throw new APIException("Please make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity());
        }

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(productId,cartId);
        if (cartItem==null){
            throw new APIException("Product "+ product.getProductName()+ " not available in the cart!!");
        }

        int newQuantity=cartItem.getQuantity()+quantity;
        if (newQuantity<0){
            throw new APIException("The resulting quantity be negative ");
        }

        if (newQuantity==0){
          deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());

            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
        }
        CartItem updateCartItem = cartItemRepository.save(cartItem);

        if (updateCartItem.getQuantity()==0){
            cartItemRepository.deleteById(updateCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productDTOS = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        }).toList();
        cartDTO.setProductDTOS(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {

       Cart cart= cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem==null){
            throw new ResourceNotFoundException("Product","productId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(productId,cartId);
        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

        Cart cart= cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem==null){
            throw new APIException("Product " +product.getProductName() +" not available in the cart!!");
        }
        double cartPrice=cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItemRepository.save(cartItem);

    }

    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
            if (userCart!=null){
                return userCart;
            }
            Cart cart=new Cart();
            cart.setTotalPrice(0.0);
            cart.setUser(authUtil.loggedInUser());

            return cartRepository.save(cart);
    }
}
