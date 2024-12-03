package com.petalaura.library.Service;


import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import com.petalaura.library.model.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    List<OrderDetails> findOrderDetailsByCustomer(String email);
    List<Long> findOrderIdsByAddressId(Long addressId);
    void cancelOrder(long orderId);
    void returnOrder(long orderId);
    boolean isCodAllowed(Double  grandTotal);
    Order saveOrder(ShoppingCart shoppingCart, String email, Long addressId, String paymentMethod, Double grandTotal);
    void deleteCart(String email);
    List<OrderDetails> findByOrderId(long orderId);
    Page<Order> findOrderByCustomerPagable(int pageNo, String email);
    List<OrderDetails> findAllOrders();
    Page<Order> findAllOrdersPagable(int page, int size);
    void deleteOrderDetailsById(Long orderId);
     Order findById(Long orderId);
     void updateOrderStatus(Order order);
     Page<Order> findOrderByOrderStatusPagable(int pageNo, String status);
     void returnOrder(Long id);
     Order findOrderByIdAndCustomerEmail(Long orderId, String email);
     List<Long> findAllOrderCountForEachMonth();
     List<Double> getTotalAmountForEachMonth();
     Double getTotalOrderAmount();
     Double getTotalAmountForMonth();
     void acceptReturnOrder(long orderId);
     void rejectReturnOrder(long orderId);
     void updateReturnMessage(Long orderId, String returnMessage);
}
