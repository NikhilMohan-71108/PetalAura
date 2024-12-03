package com.petalaura.customer.controller;

import com.petalaura.library.Repository.ShoppingCartRepository;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.Service.ShoppingCartService;
import com.petalaura.library.dto.CustomerDto;
import com.petalaura.library.exception.CartItemLimitExceededException;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.ShoppingCart;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    CustomerService customerService;
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    ShoppingCartRepository shoppingCartRepository;
    @Autowired
    ProductService productService;

    @GetMapping("/addToCart")
    public String showAddToCart(@RequestParam("productId")Long id,
                                CustomerDto customerDto, Principal principal,
                                Model model, HttpSession session) {

        if(principal == null) {
            return "redirect:/login";
        }
        int maxQuantity = 10;
        int quantity=1;


         String username=principal.getName();
       //  if( quantity > maxQuantity) {
             Customer customer = customerService.findByEmail(username);
             ShoppingCart shoppingCart = shoppingCartService.addItemToCart(id, quantity, customer.getCustomer_id());
             model.addAttribute("shoppingCart", shoppingCart);

             return "redirect:/cart";
        // }

       // return "redirect:/cart";
    }
    @GetMapping("/cart")
    public String showCart(Model model, Principal principal, HttpSession session){
        if (principal==null){
            return "redirect:/login";
        }
        String username=principal.getName();
        List<ShoppingCart> shoppingCart=shoppingCartService.findShoppingCartByCustomer(username);

        double total=shoppingCartService.grandTotal(username);
        model.addAttribute("total",total);
        model.addAttribute("title", "Cart");
        model.addAttribute("shoppingCart",shoppingCart);
//

        return "cart";
    }
      @GetMapping("/deleteCartItem")
       public String showDelete(@RequestParam("cartId")long id,Principal principal){
          shoppingCartService.deleteById(id);
          return "redirect:/cart";
      }
    @GetMapping("/incrementQuantity")
    public String showQuantityIncrement(@RequestParam("cartId")Long id, @RequestParam("productId") Long pId, RedirectAttributes redirectAttributes){

        try {
            shoppingCartService.increment(id, pId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("quantityError", e.getMessage());
            return "redirect:/cart"; // Redirect back to the shopping cart page
        }
        catch (CartItemLimitExceededException e) {
            redirectAttributes.addFlashAttribute("cartquantityError", e.getMessage());
            return "redirect:/cart"; // Redirect back to the shopping cart page
        }

        return "redirect:/cart"; // Redirect back to the shopping cart page after success
    }




    @GetMapping("/decrementQuantity")
    public String showQuantityDecrement(@RequestParam("cartId")Long id){
        shoppingCartService.decrement(id);
        return "redirect:/cart";
    }



}
