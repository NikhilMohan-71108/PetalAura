package com.petalaura.customer.controller;

import com.petalaura.library.Service.AddressService;
import com.petalaura.library.Service.CouponService;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.ShoppingCartService;
import com.petalaura.library.dto.AddressDto;
import com.petalaura.library.model.Address;
import com.petalaura.library.model.Coupon;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.ShoppingCart;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckOutController {
@Autowired
CustomerService customerService;
@Autowired
ShoppingCartService shopCartService;
@Autowired
CouponService couponService;

@Autowired
AddressService addressService;

@GetMapping("/checkOut")
public String showCheckOutPage(Model model, Principal principal, HttpSession session) {
  String username = principal.getName();

if(principal == null) {
    return "redirect:/login";
}

Customer customer=customerService.findByEmail(username);
List<ShoppingCart> shoppingCarts=shopCartService.findShoppingCartByCustomer(username);

    List<Coupon> coupons = couponService.findAll();
    model.addAttribute("coupons",coupons);


    if(shoppingCarts.isEmpty()) {
    return "redirect:/cart?empties";
}

for(ShoppingCart shoppingCart:shoppingCarts) {
int quantity=shoppingCart.getQuantity();
int productQuantity=shoppingCart.getProduct().getCurrentQuantity();
if(productQuantity<quantity) {
    return "redirect:/cart?quantityError";
}
}
    double total=shopCartService.grandTotal(username);
    List<Address> addresses=addressService.findAddressByCustomer(username);
    model.addAttribute("customer",customer);
    model.addAttribute("addresses",addresses);
    model.addAttribute("cartItem",shoppingCarts);
    model.addAttribute("total",total);
    model.addAttribute("payable",total);
   // model.addAttribute("coupons", coupons);

    return "checkOut";
}

    @GetMapping("/addAddress")
 public String showAddAddressPage(Model model, Principal principal, HttpSession session) {
        if(principal==null){
            return "redirect:/login";
        }
   AddressDto addressDto=new AddressDto();
   model.addAttribute("title","Add address");
   model.addAttribute("address",addressDto);
       return "add-address";
 }

// @PostMapping("/saveAddress")
//  public String saveAddress(Model model, Principal principal, HttpSession session,AddressDto addressDto) {
//     if(principal==null) {
//         return "redirect:/login";
//     }
//
//     String username = principal.getName();
//     addressService.save(addressDto,username);
//     return "redirect:/checkOut";
//
//  }
@GetMapping("/editAddress")
public String showEditAddress(@RequestParam("addressId")Long id,Model model){
   Optional<Address> address=addressService.findByid(id);
    model.addAttribute("address",address);
    return "edit-address";

}
    @PostMapping("/updateAddress")
    public String showUpdateAddress(@ModelAttribute("address")AddressDto addressDto){
        addressService.update(addressDto);
        return "redirect:/checkOut";
    }

}
