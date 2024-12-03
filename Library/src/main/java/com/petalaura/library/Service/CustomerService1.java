package com.petalaura.library.Service;

import com.petalaura.library.Repository.CustomerRepository;
import com.petalaura.library.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService1 {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer getCustomerById(long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional.orElse(null);  // Return null if customer not found
    }
}
