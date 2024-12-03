package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.*;
import com.petalaura.library.Service.OrderService;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import com.petalaura.library.model.Product;
import com.petalaura.library.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = Logger.getLogger(ShoppingCart.class.getName());
    @Autowired
    private  OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WalletService walletService;
    @Override
    public List<OrderDetails> findOrderDetailsByCustomer(String email) {
        return orderDetailsRepository.findOrderDetailsByCustomer(email);
    }

    @Override
    public List<Long> findOrderIdsByAddressId(Long addressId) {
        List<Order> orders = orderRepository.findByShippingAddressId(addressId);
        List<Long> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        return orderIds;
    }

    @Override
    public void cancelOrder(long id) {
    Order order=orderRepository.getReferenceById(id);
     order.setOrderStatus("cancelled");
     order.setPaymentStatus("OrderCancelled");
     List<OrderDetails> orderDetails = orderDetailsRepository.findByOrderId(id);
        for(OrderDetails orders:orderDetails){
            Long productId=orders.getProduct().getId();
            Product product=productRepository.getReferenceById(productId);
            product.setCurrentQuantity(product.getCurrentQuantity()+orders.getQuantity());
            productRepository.save(product);
        }


    }

    @Override
    public void returnOrder(long orderId) {
        Order order=orderRepository.getReferenceById(orderId);
        order.setOrderStatus("Return Initalized");
        orderRepository.save(order);
    }

    @Override
    public boolean isCodAllowed(Double grandTotal) {
            return  grandTotal <=5000;
    }

    @Override
    public Order saveOrder(ShoppingCart shoppingCart, String email, Long addressId, String paymentMethod, Double grandTotal) {
         if(paymentMethod.equalsIgnoreCase("cash_on_delivery") && grandTotal >5000){
            throw new IllegalArgumentException("COD not allowed for orders above Rs 5000");
        }
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findShoppingCartByCustomer(email);


            Order order = new Order();
            order.setOrderDate(new Date());
            order.setOrderStatus("Pending");
            order.setPaymentStatus("Pending");  // Initial payment status
            order.setCustomer(customerRepository.findByEmail(email));
            order.setGrandTotalPrize(grandTotal);
            order.setPaymentMethod(paymentMethod);
            order.setShippingAddress(addressRepository.getReferenceById(addressId));
            //order.setQuantity(totalQuantity);
        orderRepository.save(order);

        for (ShoppingCart cart : shoppingCarts) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setProduct(cart.getProduct());
            orderDetails.setOrder(order);
            orderDetails.setQuantity(cart.getQuantity());
            orderDetails.setUnitPrice(cart.getUnitPrice());
            orderDetails.setTotalPrice(cart.getTotalPrice());
            orderDetailsRepository.save(orderDetails);

            Product product = cart.getProduct();
            int quantity = product.getCurrentQuantity() - cart.getQuantity();
            product.setCurrentQuantity(quantity);
            productRepository.save(product);

//            cart.setDeleted(true);
            shoppingCartRepository.save(cart);
        }
         return order;
    }

    @Override
    public void deleteCart(String email) {
        List<ShoppingCart> shoppingCarts=shoppingCartRepository.findShoppingCartByCustomer(email);
        ShoppingCart cart = null;
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            cart = shoppingCarts.get(0);
        } else{
            logger.warning("ShoppingCarts list is either null or empty. Returning a new ShoppingCart instance.");
            cart = new ShoppingCart();
        }
           cart.setDeleted(true);
           shoppingCartRepository.save(cart);
    }

    @Override
    public List<OrderDetails> findByOrderId(long orderId) {
       return orderDetailsRepository.findByOrderId(orderId);
    }

    @Override
    public Page<Order> findOrderByCustomerPagable(int pageNo, String email) {
        return null;
    }

    @Override
    public List<OrderDetails> findAllOrders() {
       return orderDetailsRepository.findAll();
    }

    @Override
    public Page<Order> findAllOrdersPagable(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = this.orderRepository.findOrderByPagable(pageable);
        return orders;
    }

    @Override
    public void deleteOrderDetailsById(Long orderId) {
        orderDetailsRepository.deleteById(orderId);
    }

    @Override
    public Order findById(Long orderId) {
            return  orderRepository.getReferenceById(orderId);
    }

    @Override
    public void updateOrderStatus(Order order) {

        Order order1 = orderRepository.getReferenceById(order.getId());

        order1.setOrderStatus(order.getOrderStatus());
        orderRepository.save(order1);
        if(order.getOrderStatus().equals("Return Accept")){
            walletService.addToRefundAmount(order.getId());
            List<OrderDetails> orderDetails=orderDetailsRepository.findByOrderId(order.getId());
            for(OrderDetails orders:orderDetails){
                Long productId=orders.getProduct().getId();
                Product product=productRepository.getReferenceById(productId);
                product.setCurrentQuantity(product.getCurrentQuantity()+orders.getQuantity());
                productRepository.save(product);
            }
        }else if(order.getOrderStatus().equals("Delivered")){
            Order order2 = orderRepository.getReferenceById(order.getId());
            order2.setDeliveryDate(new Date());
            orderRepository.save(order2);
        }
    }

    @Override
    public Page<Order> findOrderByOrderStatusPagable(int pageNo, String status) {
        return null;
    }

    @Override
    public void returnOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setOrderStatus("Return Pending");
        orderRepository.save(order);
    }

    @Override
    public Order findOrderByIdAndCustomerEmail(Long orderId, String email) {
        return  orderRepository.findOrderByIdAndCustomerEmail(orderId, email);
    }

    @Override
    public List<Long> findAllOrderCountForEachMonth() {
        List<Long>orderCounts=new ArrayList<>();
        LocalDate currentDate = LocalDate.now().withMonth(1);

        for(int i=0 ; i < 12 ; i++){
            LocalDate localStartDate=currentDate.withDayOfMonth(1);
            LocalDate localEndDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());

            long orderCount= orderRepository.countByOrderDateBetweenAndOrderStatus(localStartDate,localEndDate,"Delivered");
            orderCounts.add(orderCount);
            currentDate = currentDate.plusMonths(1);

        }


        return orderCounts;
    }

    @Override
    public List<Double> getTotalAmountForEachMonth() {
        List<Double> totalRevenuePerMonth = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().withMonth(1);

        for(int i=0 ; i < 12 ; i++){
            LocalDate localStartDate=currentDate.withDayOfMonth(1);
            LocalDate localEndDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());

            Double totalRevenue = orderRepository.getTotalConfirmedOrdersAmountForMonth(localStartDate,localEndDate,"Delivered");
            totalRevenuePerMonth.add(totalRevenue);
            currentDate = currentDate.plusMonths(1);

        }

        return totalRevenuePerMonth;
    }

    @Override
    public Double getTotalOrderAmount() {
        return orderRepository.getTotalConfirmedOrdersAmount();
    }

    @Override
    public Double getTotalAmountForMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate=currentDate.withDayOfMonth(1);
        LocalDate endDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        Double totalAmount = orderRepository.getTotalConfirmedOrdersAmountForMonth(startDate,endDate,"Delivered");

        return totalAmount;
    }

    @Override
    public void acceptReturnOrder(long orderId) {
        Order order=orderRepository.getReferenceById(orderId);
        order.setOrderStatus("Return Accepted");
        orderRepository.save(order);
    }

    @Override
    public void rejectReturnOrder(long orderId) {
        Order order=orderRepository.getReferenceById(orderId);
        order.setOrderStatus("Return Declined");
        orderRepository.save(order);
    }

    @Override
    public void updateReturnMessage(Long orderId, String returnMessage) {
        // Use the repository to update the return message
        int updatedCount = orderRepository.updateReturnMessage(orderId, returnMessage);

        if (updatedCount == 0) {
            throw new RuntimeException("Order not found or failed to update return message");
        }
    }


}
