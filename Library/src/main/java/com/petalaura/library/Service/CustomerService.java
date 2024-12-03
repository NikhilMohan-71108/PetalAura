package com.petalaura.library.Service;

import com.petalaura.library.dto.CustomerDto;
import com.petalaura.library.model.Customer;

import java.util.List;

public interface    CustomerService {

    void save(CustomerDto customerDto);
    Customer findByEmail(String email);
    List<Customer> findAllActivatedByTrue();
    List<Customer> findAll();
    void  blockById(Long id);
    void enableById(Long id);
    Customer update(String email, String name, Long mobile);
    Customer getByResetPasswordToken(String token);
    //
    // Declare the method to fetch a customer by ID
    Customer getCustomerById(long id);

}
