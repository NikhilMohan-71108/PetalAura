package com.petalaura.library.Service;

import com.petalaura.library.model.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    ShoppingCart  addItemToCart(Long productId, int quantity,Long id);
    List<ShoppingCart> findShoppingCartByCustomer(String email);
    Double grandTotal(String username);
    void deleteById(long id);
    void increment(Long id, Long productId);
    void decrement(Long id);
}
