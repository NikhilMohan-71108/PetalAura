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
        Customer customer=customerService.findByEmail(principal.getName());

        Wallet wallet=walletService.findByCustomer(customer.getCustomer_id());


        List<WalletHistoryDto> walletHistoryDtoList=walletService.findAllById(wallet.getId());


        model.addAttribute("walletHistoryList",walletHistoryDtoList);

        model.addAttribute("wallet",wallet);

        return "wallet";
    }

    @RequestMapping(value = "/add-wallet",method = RequestMethod.POST)
    @ResponseBody
    public String addWallet(@RequestBody Map<String,Object> data, Principal principal,
                            HttpSession session, Model model) throws RazorpayException {

//        if(principal==null){
//            return "redirect:/login";
//        }


        Customer customer=customerService.findByEmail(principal.getName());

        double amount = Double.parseDouble(data.get("amount").toString());
        WalletHistory walletHistory=walletService.save(amount,customer);

        Long walletHistoryId = walletHistory.getId();

        session.setAttribute("walletHistoryId",walletHistory.getId());

        model.addAttribute("success","Money Added Successfully");
        RazorpayClient razorpayClient=new RazorpayClient("rzp_test_HmcSu0044VxCip", "wW6RUy1AClT2WQjLycqKoCGU");

        JSONObject options = new JSONObject();
        options.put("amount",amount*100);
        options.put("currency","INR");
        options.put("receipt",walletHistoryId.toString());
        com.razorpay.Order orderRazorPay = razorpayClient.orders.create(options);
        return orderRazorPay.toString();

    }

    @RequestMapping(value = "/verify-wallet",method = RequestMethod.POST)
    @ResponseBody
    public String verifyWalletPayment(@RequestBody Map<String,Object> data,HttpSession session,Principal principal) throws RazorpayException {

        String secret= "wW6RUy1AClT2WQjLycqKoCGU";
        String order_id= data.get("razorpay_order_id").toString();
        String payment_id=data.get("razorpay_payment_id").toString();
        String signature=data.get("razorpay_signature").toString();
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", order_id);
        options.put("razorpay_payment_id", payment_id);
        options.put("razorpay_signature", signature);

        boolean status =  Utils.verifyPaymentSignature(options, secret);
        System.out.println(status);
        WalletHistory walletHistory=walletService.findById((Long)session.getAttribute("walletHistoryId"));
        Customer customer=customerService.findByEmail(principal.getName());
        if(status){
            walletService.updateWallet(walletHistory,customer,status);
        }else {
            walletService.updateWallet(walletHistory,customer,status);
        }
        JSONObject response = new JSONObject();
        response.put("status",status);

        session.removeAttribute("walletHistoryId");

        return response.toString();
    }
}
