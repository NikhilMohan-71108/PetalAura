package com.petalaura.customer.controller;

import com.petalaura.customer.config.CustomerDetails;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.model.Wallet;
import com.petalaura.library.model.WalletHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class WalletController {

    @Autowired
    WalletService walletService;
    @Autowired
    CustomerService customerService;

    @GetMapping("/wallets")
    public String getWalletDetails(Principal principal, Model model, Authentication authentication) {
        if (principal == null) {
            return "redirect:/loginPage";
        }
        CustomerDetails customUser = (CustomerDetails) authentication.getPrincipal();
        Wallet wallet = walletService.findByCustomerByUsername(customUser.getUsername());
        List<WalletHistory> walletHistories = walletService.findAllByCustomerName(customUser.getUsername());
        String name = customUser.getUsername();
        model.addAttribute("wallet", wallet);
        model.addAttribute("walletHistory", walletHistories);
        model.addAttribute("name", name);

        return "wallet";
    }
}
