package com.petalaura.library.Service;


import com.petalaura.library.dto.DailyEarning;
import com.petalaura.library.dto.Monthlyearning;
import com.petalaura.library.dto.WeeklyEarnings;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import com.petalaura.library.model.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public interface OrderService {
    Order saveOrder(ShoppingCart shoppingCart, String email, Long addressId, String paymentMethod, Double grandTotal);
    List<OrderDetails> findAllOrder();

    List<OrderDetails> findOrderDetailsByCustomer(String email);

    List<OrderDetails> findByOrderId(Long orderid);

    Order findById(long id);

    void updateOrderStatus(Order order);

    List<Order> findAll();

    void cancelOrder(Long id);

    void returnOrder(Long id);

    List<Order> findOrderByCustomer(String email);

    Order findOrderByIdAndCustomerEmail(Long orderId, String email);

    public List<Order> getDailyOrders(LocalDate date);

    Page<Order> findOrderByPageble(int page, int size);

    Page<Order> findOrderByCustomerPagable(int pageNo, String email);

    Page<Order> findOrderByOrderStatusPagable(int pageNo, String status);

    List<Order> getDailyReport(Date date);

    void deleteOrderDetailsById(Long id);

    List<Order> findOrdersByAddressId(Long addressId);

    List<Long> findOrderIdsByAddressId(Long addressId);

    List<Monthlyearning> getMonthlyReport(int year);

    List<DailyEarning> dailyReport(int year, int month);

    List<WeeklyEarnings> findWeeklyEarnings(int year);

    boolean isCodAllowed(Double grandTotal);


    Double getTotalOrderAmount();

    Double getTotalAmountForMonth();

    List<Long> findAllOrderCountForEachMonth();

    List<Double> getTotalAmountForEachMonth();

    void deleteCart(String email);
}
