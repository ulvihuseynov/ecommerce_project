package com.e_commerce.project.service;

import com.e_commerce.project.exception.APIException;
import com.e_commerce.project.exception.ResourceNotFoundException;
import com.e_commerce.project.model.*;
import com.e_commerce.project.payload.OrderDTO;
import com.e_commerce.project.payload.OrderItemDTO;
import com.e_commerce.project.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;


    public OrderServiceImpl(CartRepository cartRepository, AddressRepository addressRepository, PaymentRepository paymentRepository,
                            OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository,
                            CartService cartService, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId, String paymentMethod, Long paymentId,String pgPaymentId,
                               String pgName, String pgResponseMessage, String pgStatus,
                               Long addressId) {

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart==null){
            throw new ResourceNotFoundException("Cart","email",emailId);
        }

        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId)
        );
        Order order=new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus("Order Accepted");
        order.setAddress(address);

        Payment payment=new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);
        paymentRepository.save(payment);

        order.setPayment(payment);
        Order savedOrder=orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }
        List<OrderItem>orderItems=new ArrayList<>();

        for (CartItem cartItem:cartItems){
            OrderItem orderItem=new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(item->{
            Integer quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(orderItem -> orderDTO.getOrderItemDTOS().add(modelMapper.map(orderDTO, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
