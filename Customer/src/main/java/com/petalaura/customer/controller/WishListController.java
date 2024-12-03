package com.petalaura.customer.controller;

import com.petalaura.library.Service.WishListService;
import com.petalaura.library.model.WishList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishListController {
    @Autowired
    private WishListService wishListService;

   @GetMapping("/wishList")

    public String wishList(Model model, Principal principal) {
       if(principal == null) {
             return "redirect:/login";
       }
       String username = principal.getName();
       List<WishList> wishLists = wishListService.findWishListByCustomer(username);
       model.addAttribute("wishlists", wishLists);
       return "wishlist";
   }

    @GetMapping("/addToWishList/{productId}")
    public String addToWishList(@PathVariable("productId") Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        wishListService.addToWishList(username, id);
        return "redirect:/wishlist/wishList";
    }

  @GetMapping("/removeFromWishList/{id}")
  public String removeFromWishList(@PathVariable("id") Long id, Model model, Principal principal) {
      wishListService.removeFromWishList(id);
      return "redirect:/wishlist/wishList";
  }

}
