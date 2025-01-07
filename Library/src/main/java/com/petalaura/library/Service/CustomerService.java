package com.petalaura.library.Service;

import com.petalaura.library.dto.CustomerDto;
import com.petalaura.library.model.Customer;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    void updatePassword(Customer customer, String newPassword);
    // Declare the method to fetch a customer by ID
    Customer getCustomerById(long id);
    void updateResetPasswordToken(String token, String email);
    Customer findById(Long id);
    void updateReferalCodeToken(String token,String email);
    Customer getByReferalToken(String token);
    void saveCustomer(@Valid CustomerDto customerDto);
    //Page<Customer> getCustomersPage(Pageable pageable);
}
