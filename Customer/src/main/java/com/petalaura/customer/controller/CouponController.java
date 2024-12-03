package com.petalaura.customer.controller;


import com.petalaura.library.Service.AddressService;
import com.petalaura.library.Service.CouponService;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.ShoppingCartService;
import com.petalaura.library.model.Address;
import com.petalaura.library.model.Coupon;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class CouponController {
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    CouponService couponService;
    @Autowired
    AddressService addressService;
    @Autowired
    CustomerService customerService;




    @PostMapping("/applyCoupon")
    public String applyCoupon(Coupon coupon, Principal principal, Model model) {
        String username = principal.getName();
        Customer customer = customerService.findByEmail(username);
        List<ShoppingCart> shoppingCarts = shoppingCartService.findShoppingCartByCustomer(username);
        Coupon coupons = couponService.findByCouponCode(coupon.getCouponcode());
        model.addAttribute("coupons", coupons);
        System.out.println(coupons);

        if (coupons == null || coupons.isExpired() ) {
            return "redirect:/checkOut?expired";
        }

        if(coupons.getCount() < 1){
            model.addAttribute("error", "Coupon already Used");
        }
        double grandTotal = shoppingCartService.grandTotal(username);
        List<Address> addresses = addressService.findAddressByCustomer(username);
        double payableAmount;

        if (grandTotal < coupons.getMinimumOrderAmount()) {
            model.addAttribute("errorMessage", "Order amount is less");
            model.addAttribute("addresses", addresses);
            model.addAttribute("cartItem", shoppingCarts);
            model.addAttribute("total", grandTotal);
            model.addAttribute("customer", customer);
            model.addAttribute("payable", grandTotal);
            return "redirect:/checkOut";
        }
        double offerPercentage = Double.parseDouble(coupons.getOfferPercentage());
        double offer = (grandTotal * offerPercentage) / 100;


        offer = Math.min(offer, coupons.getMaximumOfferAmount());


        payableAmount = grandTotal - offer;


        if (grandTotal >= coupons.getMinimumOrderAmount()) {
            couponService.decreaseCoupon(coupons.getId());
        } else {
            model.addAttribute("error", "Order does not meet minimum amount for coupon usage.");
            payableAmount = grandTotal;
        }


        model.addAttribute("addresses", addresses);
        model.addAttribute("cartItem", shoppingCarts);
        model.addAttribute("total", payableAmount);
        model.addAttribute("customer", customer);
        model.addAttribute("payable", payableAmount);

        return "checkOut";

    }

    @PostMapping("/removeCoupon")
    public String removeCoupon(Coupon coupon, Principal principal, Model model) {
        String username = principal.getName();
        Customer customer = customerService.findByEmail(username);
        List<ShoppingCart> shoppingCarts = shoppingCartService.findShoppingCartByCustomer(username);
        double grandTotal = shoppingCartService.grandTotal(username);
        List<Address> addresses = addressService.findAddressByCustomer(username);
        List<Coupon> availableCoupons = couponService.findAll();
        model.addAttribute("addresses", addresses);
        model.addAttribute("cartItem", shoppingCarts);
        model.addAttribute("total", grandTotal);
        model.addAttribute("customer", customer);
        model.addAttribute("payable", grandTotal);
        model.addAttribute("coupons", availableCoupons);

        return "checkOut";

    }

    @GetMapping("/coupons")
    public String showCoupon(Model model) {
        List<Coupon> coupons = couponService.findAll();
        model.addAttribute("coupons", coupons);
        return "checkOut";

    }
}
