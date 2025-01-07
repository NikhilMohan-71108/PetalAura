package com.petalaura.customer.controller;

import com.petalaura.customer.config.CustomerDetails;
import com.petalaura.library.Repository.OrderRepository;
import com.petalaura.library.Service.OrderService;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import com.petalaura.library.model.ShoppingCart;
import com.petalaura.library.model.Wallet;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WalletService walletService;




    @GetMapping("/orderConfirm")
    public String orderConfirm() {
        return "order-confirm";
    }

    @GetMapping("/orderFailure")
    public String showOrderFailure() {
        return "order-failure";

    }

   @GetMapping("/order")
    public String showOrder(@RequestParam("pageNO") int pageNo, Model model, Principal principal) {

        if(principal == null) {
           return "redirect:/login";
        }
        String username = principal.getName();
          Page<Order> orders=orderService.findOrderByCustomerPagable(pageNo, username);
          model.addAttribute("orders", orders);
          model.addAttribute("currentPage", pageNo);
          model.addAttribute("totalPage", orders.getTotalPages());
           return "orders";
   }

          @GetMapping("/orderDetails")
          public String showOrderDetails(@RequestParam("orderId") Long id, Model model){
               List<OrderDetails> orderDetails=orderService.findByOrderId(id);
              model.addAttribute("orderDetails", orderDetails);
              return "order-details";
          }


    @PostMapping("/createPayment")
    @ResponseBody
    public String showOnlinePayment(Principal principal, Authentication authentication,
                                    @RequestBody Map<String, Object> data) throws RazorpayException {
        CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
        long id = customerDetails.getCustomer_id();
        String username = principal.getName();
        String paymentMethod = data.get("paymentMethod").toString();
        Long address_id = Long.parseLong(data.get("addressId").toString());
        Double amount = Double.valueOf(data.get("amount").toString());

        if (!orderService.isCodAllowed(amount) && paymentMethod.equalsIgnoreCase("cash_on_delivery")) {
            JSONObject option = new JSONObject();
            option.put("status", "COD not allowed for orders above Rs 1000");
            return option.toString();
        }
       if (paymentMethod.equals("wallet")) {
            Wallet wallet = walletService.findByCustomer(id);
           if (wallet.getBalance() < amount) {
                JSONObject option = new JSONObject();
                option.put("status", "noWallet");
               return option.toString();
          }
        }


        ShoppingCart shoppingCart = new ShoppingCart();
        Order order = orderService.saveOrder(shoppingCart, username, address_id, paymentMethod, amount);

        if (paymentMethod.equalsIgnoreCase("Online Payment")) {
            RazorpayClient client = new RazorpayClient("rzp_test_HmcSu0044VxCip", "wW6RUy1AClT2WQjLycqKoCGU");
            JSONObject object = new JSONObject();
            object.put("amount", amount * 100);
            object.put("currency", "INR");
            object.put("receipt", "receipt#1");
            com.razorpay.Order razorpayOrder = client.orders.create(object);
            // Update payment status to Pending
            order.setPaymentStatus("Success");
           // order.setPaymentId(razorpayOrder.get("id"));
            Order newOrder = orderRepository.save(order);

          //  orderService.deleteCart(username);
            JSONObject response = new JSONObject(razorpayOrder.toString());
            response.put("status", "created");
            response.put("newOrderId", newOrder.getId().toString());
            return response.toString();
        } else if (paymentMethod.equals("wallet")) {

            Wallet wallet = walletService.findByCustomer(id);
            orderService.saveOrder(shoppingCart, username, address_id, paymentMethod, amount);
            walletService.debit(wallet, amount);
            order.setPaymentStatus("Success");
            orderRepository.save(order);
            orderService.deleteCart(username);//added2

            JSONObject option = new JSONObject();
            option.put("status", "wallet");
            return option.toString();

        } else {
            order.setPaymentStatus("Success");
            orderRepository.save(order);
            orderService.deleteCart(username);//added3

            JSONObject option = new JSONObject();
            option.put("status", "cash");
            return option.toString();
        }
    }

    @PostMapping("/verify-payment")
    @ResponseBody
    public String showVerifyPayment(@RequestBody Map<String, Object> data) {

        String paymentStatus = data.get("status").toString();
        var orderId = data.get("order_id").toString();

        Order order = orderService.findById(Long.parseLong(orderId));

        String payment_id = data.get("razorpay_payment_id").toString();


        if (paymentStatus.equalsIgnoreCase("success")) {
            order.setPaymentStatus("Success");
            order.setId(Long.valueOf(orderId));
            order.setPaymentId(data.get("razorpay_payment_id").toString());
            orderService.deleteCart(order.getCustomer().getEmail());
        } else {
            order.setPaymentStatus("Payment Pending");

        }

        orderRepository.save(order);

        return "{\"status\":\"done\"}";
    }

}
