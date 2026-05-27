package com.e_commerce.project.repository;

import com.e_commerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query("SELECT ci From CartItem ci WHERE ci.product.id=?1 AND ci.cart.id=?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("Delete  From CartItem ci Where ci.product.id=?1 And ci.cart.id=?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
