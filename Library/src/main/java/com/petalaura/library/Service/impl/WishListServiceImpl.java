package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.CustomerRepository;
import com.petalaura.library.Repository.ProductRepository;
import com.petalaura.library.Repository.WishListRepository;
import com.petalaura.library.Service.WishListService;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Product;
import com.petalaura.library.model.WishList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListServiceImpl implements WishListService {
   @Autowired
   private WishListRepository wishListRepository;
   @Autowired
   private CustomerRepository customerRepository;
   @Autowired
   private ProductRepository productRepository;

    @Override
    public void addToWishList(String username, Long productId) {
      Customer customer = customerRepository.findByEmail(username);
      Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

      WishList wishList = new WishList();
        wishList.setCustomer(customer);
        wishList.setProduct(product);
        wishList.setDeleted(false);
        wishListRepository.save(wishList);


    }
    @Override
    public void removeFromWishList(Long productId) {
         wishListRepository.deleteById(productId);
    }

    @Override
    public List<WishList> findWishListByCustomer(String username) {
        Customer customer = customerRepository.findByEmail(username);
        return wishListRepository.findByCustomer(customer);
    }


}
