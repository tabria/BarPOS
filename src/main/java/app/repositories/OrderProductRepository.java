package app.repositories;

import app.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query(value = "SELECT op FROM OrderProduct op WHERE op.id.order.id = :id")
    List<OrderProduct> findProductsInOrder(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE OrderProduct op SET op.quantity = :q WHERE op.id.order.id = :orderId AND op.id.product.id = :productId")
    void updateProductQuantity(@Param("q") Integer quantity, @Param("orderId") Long orderId, @Param("productId") Long productId);
}