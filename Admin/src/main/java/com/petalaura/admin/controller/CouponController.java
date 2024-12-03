package com.petalaura.admin.controller;

import com.petalaura.library.Service.CouponService;
import com.petalaura.library.model.Coupon;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CouponController {
    @Autowired
    private CouponService couponService;
    @GetMapping("/coupons")
   public String showCoupon(Model model) {
    List<Coupon> coupons = couponService.findAll();
    model.addAttribute("coupons", coupons);
    return "coupons";

   }
   @GetMapping("/add-coupon")
   public String showAddCoupon(Model model) {
     Coupon coupon = new Coupon();
     model.addAttribute("coupon", coupon);
     return "add-coupon";
   }

   @PostMapping("/saveCoupon")
   public String showAddCoupon(@Valid @ModelAttribute("coupon")Coupon coupon, BindingResult result) {

       if (result.hasErrors()) {
           return "add-coupon";
       }
       couponService.save(coupon);
       return "redirect:/coupons";
   }
   @GetMapping("/disableCoupon")
    public String disableCoupon(@RequestParam("couponId")Long id) {
      couponService.disableCoupon(id);
      return "redirect:/coupons";
    }
    @GetMapping("/enableCoupon")
    public String showEnableCoupon(@RequestParam("couponId")Long id){

        couponService.enableCoupon(id);
        return "redirect:/coupons";
    }
    @GetMapping("/editCoupon")
    public String showCouponUpdate(@RequestParam("couponId")Long id,Model model){
        Coupon coupon=couponService.findByid(id);
        model.addAttribute("coupon",coupon);
        return "update-coupon";
    }
    @PostMapping("/update-coupon")
    public String updateCoupon(@ModelAttribute("couponId")Coupon coupon){
        couponService.updateCoupon(coupon);
        return "redirect:/coupons";
    }

    @GetMapping("/deleteCoupon")
    public String showCouponDelete (@RequestParam("couponId")Long id){
        couponService.deleteCoupon(id);
        return "redirect:/coupons";
    }

}
