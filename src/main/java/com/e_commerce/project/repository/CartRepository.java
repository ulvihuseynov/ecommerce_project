package com.e_commerce.project.repository;

import com.e_commerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("SELECT c From Cart c WHERE c.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("Select c From Cart c Where c.user.email=?1 And c.id=?2")
    Cart findCartByEmailAndCartId(String  emailId,Long cartId);


    @Query("Select c From Cart c Join fetch c.cartItems ci join fetch ci.product p where p.id=?1")
    List<Cart> findCartsByProductId(Long productId);
}
