package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.CustomerRepository;
import com.petalaura.library.Repository.ShoppingCartRepository;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.Service.ShoppingCartService;
import com.petalaura.library.exception.CartItemLimitExceededException;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Product;
import com.petalaura.library.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public ShoppingCart addItemToCart(Long productId, int quantity, Long id) {
        ShoppingCart shoppingCarts = shoppingCartRepository.findByUsersProduct(id, productId);
        Product product = productService.getProductById(productId);

        if (shoppingCarts != null) {
            int oldQuantity = shoppingCarts.getQuantity();
            if (oldQuantity < oldQuantity + quantity) {
                System.out.println("Exceeded");
            } else {
                shoppingCarts.setQuantity(oldQuantity + quantity);
                shoppingCarts.setUnitPrice(product.getSalePrice());
                double totalPrice = product.getSalePrice() * (oldQuantity + quantity);
                shoppingCarts.setTotalPrice(totalPrice);
                shoppingCarts.setDeleted(false);
            }

        } else {

            shoppingCarts = new ShoppingCart();

            shoppingCarts.setProduct(product);
            shoppingCarts.setCustomer(customerRepository.getById(id));
            shoppingCarts.setQuantity(quantity);
            shoppingCarts.setUnitPrice(product.getSalePrice());
            double totalPrice = shoppingCarts.getUnitPrice() * shoppingCarts.getQuantity();
            shoppingCarts.setTotalPrice(totalPrice);
            shoppingCarts.setDeleted(false);

        }
        return shoppingCartRepository.save(shoppingCarts);
    }

    @Override
    public List<ShoppingCart> findShoppingCartByCustomer(String email) {
        return shoppingCartRepository.findShoppingCartByCustomer(email);
    }

    @Override
    public Double grandTotal(String username) {
        Customer customer = customerRepository.findByEmail(username);

        if (customer != null) {
            Long customerId = customer.getCustomer_id();
            return shoppingCartRepository.findGrandTotal(customerId);
        }

        return 0.00;
    }

    @Override
    public void deleteById(long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.getReferenceById((int) id);
        shoppingCartRepository.deleteShoppingCartItemById(id);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void increment(Long id, Long productId) {
        ShoppingCart shoppingCart1 = shoppingCartRepository.getReferenceById(Math.toIntExact(id));
        Product product = productService.getProductById(productId);
        if (shoppingCart1.getQuantity() >= 5) {
            throw new CartItemLimitExceededException("You cannot have more than 5 items of this product in your cart.");
        }
        if (product.getCurrentQuantity() > shoppingCart1.getQuantity()) {
            shoppingCart1.setQuantity(shoppingCart1.getQuantity() + 1);
            shoppingCart1.setTotalPrice(shoppingCart1.getQuantity() * shoppingCart1.getUnitPrice());
            shoppingCartRepository.save(shoppingCart1);
        } else {
            throw new IllegalArgumentException("You selected more items than are available. Please check the selected quantity and available stock!");
        }
    }


    @Override
    public void decrement(Long id) {
        ShoppingCart shoppingCart1 = shoppingCartRepository.getReferenceById(Math.toIntExact(id));
        if (shoppingCart1.getQuantity() > 1) {
            shoppingCart1.setQuantity(shoppingCart1.getQuantity() - 1);
            shoppingCart1.setTotalPrice(shoppingCart1.getQuantity() * shoppingCart1.getUnitPrice());
            shoppingCartRepository.save(shoppingCart1);
        }

    }


}



