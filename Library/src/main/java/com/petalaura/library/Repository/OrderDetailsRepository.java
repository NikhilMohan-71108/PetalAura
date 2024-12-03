package com.petalaura.library.Repository;

import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails,Long> {
   @Query("select od from OrderDetails od where od.order.customer.email=?1")
    List<OrderDetails> findOrderDetailsByCustomer(String email);
    @Query("SELECT o FROM Order o WHERE o.shippingAddress.id = :addressId")
    List<Order> findByShippingAddressId(@Param("addressId") Long addressId);
    List<OrderDetails> findByOrderId(Long orderId);
    @Query("SELECT od from OrderDetails od" )
    List<OrderDetails> findAllOrder();
   @Transactional
   @Modifying
   @Query("DELETE FROM OrderDetails od WHERE od.id = :id")
   void deleteOrderDetailsById(Long id);

}
