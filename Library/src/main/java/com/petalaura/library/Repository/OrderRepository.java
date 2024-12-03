package com.petalaura.library.Repository;

import com.petalaura.library.dto.OrderDto;
import com.petalaura.library.model.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.shippingAddress.id = :addressId")
    List<Order> findByShippingAddressId(@Param("addressId") Long addressId);
    @Query("select o from Order o where o.customer.email=?1 order by o.orderDate desc ")
    List<Order> findOrderByCustomer(String email);
    @Query("select o from Order o order by o.orderDate desc")
    Page<Order> findOrderByPagable(Pageable pageable);
    @Query("select o from Order o where o.orderStatus=?1 order by o.orderDate desc ")
    Page<Order> findOrderByOrderStatusPagable(Pageable pageable,String orderStatus);

    List<Order> findByOrderDateBetween(Date startDate, Date endDate);

    int countByIsAcceptIsFalse();

    @Query(value = "SELECT DATE_FORMAT(o.order_date, '%Y-%m') AS month, " +
            "SUM(o.grand_total_prize) AS earnings, " +
            "COUNT(o.order_id) AS totalOrder, " +
            "COUNT(CASE WHEN o.order_status = 'Delivered' THEN o.order_id END) AS delivered_orders, " +
            "COUNT(CASE WHEN o.order_status = 'Cancel' THEN o.order_id END) AS cancelled_orders " +
            "FROM orders o " +
            "WHERE YEAR(o.order_date) = :year " +
            "GROUP BY DATE_FORMAT(o.order_date, '%Y-%m') " +
            "ORDER BY month",
            nativeQuery = true)
    List<Object[]> monthlyReport(@Param("year") int year);



    @Query(value = "SELECT DATE(o.order_date) AS date, " +
            "SUM(o.grand_total_prize) AS earnings, " +
            "COUNT(o.order_id) AS totalOrder " +
            "FROM orders o " +
            "WHERE o.order_status = 'Delivered' " +
            "AND YEAR(o.order_date) = :year " +
            "AND MONTH(o.order_date) = :month " +
            "GROUP BY DATE(o.order_date)", nativeQuery = true)
    List<Object[]> dailyReport(@Param("year") int year, @Param("month") int month);


    @Query("SELECT o.paymentMethod, SUM(o.grandTotalPrize) FROM Order o WHERE o.orderStatus='Delivered' AND o.paymentMethod IN ('online_payment', 'cash_on_Delivery', 'wallet') GROUP BY o.paymentMethod")
    List<Object[]> findTotalPricesByPaymentMethod();
    @Query("select o from Order o where o.id = :orderId and o.customer.email = :email")
    Order findOrderByIdAndCustomerEmail(@Param("orderId") Long orderId, @Param("email") String email);

    @Query(value = "SELECT COALESCE(SUM(grand_total_prize), 0) " +
            "FROM orders " +
            "WHERE order_date BETWEEN :startDate AND :endDate " +
            "AND order_status = :orderStatus", nativeQuery = true)
    long countByOrderDateBetweenAndOrderStatus(@Param("startDate") LocalDate localStartDate,
                                               @Param("endDate") LocalDate localEndDate,
                                               @Param("orderStatus") String delivered);


    @Query(value = "SELECT COALESCE(SUM(grand_total_prize), 0) " +
            "FROM orders " +
            "WHERE order_date BETWEEN :startDate AND :endDate " +
            "AND order_status = :orderStatus", nativeQuery = true)
    Double getTotalConfirmedOrdersAmountForMonth(
            @Param("startDate") LocalDate localStartDate,
            @Param("endDate") LocalDate localEndDate,
            @Param("orderStatus") String delivered);


    @Query(value = "select COALESCE(SUM(o.grandTotalPrize),0) FROM Order o where o.orderStatus = 'Delivered'")
    Double getTotalConfirmedOrdersAmount();

    @Query("SELECT new com.petalaura.library.dto.OrderDto(o.orderStatus, o.returnMessage) FROM Order o WHERE o.id = :orderId")
    OrderDto findOrderStatusAndReturnMessageByOrderId(@Param("orderId") Long orderId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.returnMessage = :returnMessage WHERE o.id = :orderId")
    int updateReturnMessage(Long orderId, String returnMessage);


}
