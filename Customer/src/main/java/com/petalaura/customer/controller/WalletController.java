package com.petalaura.customer.controller;

import com.petalaura.customer.config.CustomerDetails;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.dto.WalletHistoryDto;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Wallet;
import com.petalaura.library.model.WalletHistory;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class WalletController {

    @Autowired
    WalletService walletService;
    @Autowired
    CustomerService customerService;

    @GetMapping("/wallets")
    public String getWalletDetails(Principal principal, Model model, Authentication authentication) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(principal.getName());

        Wallet wallet = walletService.findByCustomer(customer.getCustomer_id());


        List<WalletHistoryDto> walletHistoryDtoList = walletService.findAllById(wallet.getId());


        model.addAttribute("walletHistoryList", walletHistoryDtoList);

        model.addAttribute("wallet", wallet);
        model.addAttribute("wallet", customer.getWallet());  // Assuming the wallet is part of the customer entity


        return "wallet";
    }

    @RequestMapping(value = "/add-wallet", method = RequestMethod.POST)
    @ResponseBody
    public String addWallet(@RequestBody Map<String, Object> data, Principal principal,
                            HttpSession session, Model model) throws RazorpayException {

        // Fetch customer details based on the principal (logged-in user)
        Customer customer = customerService.findByEmail(principal.getName());

        // Parse the amount to be added to the wallet
        double amount = Double.parseDouble(data.get("amount").toString());

        // Create a WalletHistory record (without the payment_id for now)
        WalletHistory walletHistory = walletService.save(amount, customer);

        // Store the WalletHistory ID in the session for later reference
        Long walletHistoryId = walletHistory.getId();
        session.setAttribute("walletHistoryId", walletHistoryId);

        model.addAttribute("success", "Money Added Successfully");

        // Initialize Razorpay client and create an order for the payment
        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_HmcSu0044VxCip", "wW6RUy1AClT2WQjLycqKoCGU");
        JSONObject options = new JSONObject();
        options.put("amount", amount * 100);  // Amount in paise
        options.put("currency", "INR");
        options.put("receipt", walletHistoryId.toString());  // Use walletHistoryId as receipt

        // Create an order on Razorpay
        com.razorpay.Order orderRazorPay = razorpayClient.orders.create(options);

        // Return Razorpay order details (for frontend to handle the payment)
        return orderRazorPay.toString();
    }


    @RequestMapping(value = "/verify-wallet", method = RequestMethod.POST)
    @ResponseBody
    public String verifyWalletPayment(@RequestBody Map<String,Object> data, HttpSession session, Principal principal) throws RazorpayException {

        String secret = "wW6RUy1AClT2WQjLycqKoCGU";
        String order_id = data.get("razorpay_order_id").toString();
        String payment_id = data.get("razorpay_payment_id").toString();
        String signature = data.get("razorpay_signature").toString();
          WalletHistory history =new WalletHistory();
           history.setPaymentId(payment_id);
        // Prepare data for signature verification
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", order_id);
        options.put("razorpay_payment_id", payment_id);
        options.put("razorpay_signature", signature);

        // Verify the payment signature with Razorpay
        boolean status = Utils.verifyPaymentSignature(options, secret);
        System.out.println("Payment Verification Status: " + status);

        // Retrieve the WalletHistory object from the session (walletHistoryId)
        WalletHistory walletHistory = walletService.findById((Long) session.getAttribute("walletHistoryId"));
        if (walletHistory == null) {
            return "Error: WalletHistory not found";
        }

        // Fetch customer details (to update the wallet)
        Customer customer = customerService.findByEmail(principal.getName());

        // If the payment is successful, save the payment_id in the WalletHistory
        if (status) {
            walletHistory.setPaymentId(payment_id);  // Store the Razorpay payment ID here
            walletService.updateWallet(walletHistory, customer, status);  // Update wallet details
        } else {
            walletService.updateWallet(walletHistory, customer, status);  // Handle failed payment case
        }

        // Prepare the response (payment status)
        JSONObject response = new JSONObject();
        response.put("status", status);

        // Remove the walletHistoryId from session after processing
        session.removeAttribute("walletHistoryId");

        return response.toString();
    }

}
