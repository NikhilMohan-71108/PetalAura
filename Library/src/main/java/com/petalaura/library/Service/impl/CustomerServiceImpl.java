package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.CustomerRepository;
import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.dto.CustomerDto;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

@Autowired
private CustomerRepository customerRepository;

    @Override
    public  void save(CustomerDto customerDto) {

        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(customerDto.getPassword());
        customer.setMobile(customerDto.getMobile());
        customer.setRole("User");
        customer.setActivated(true);
        customer.setBlocked(false);

        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);
        wallet.setCustomer(customer);
        customer.setWallet(wallet);
        customerRepository.save(customer);
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> findAllActivatedByTrue() {
        return customerRepository.findAllByActivatedTrue();
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public void blockById(Long id) {
        Customer customer = customerRepository.findById(id).get();
        customer.setBlocked(true);
        customer.setActivated(false);
        customerRepository.save(customer);
    }

    @Override
    public void enableById(Long id) {
        Customer customer = customerRepository.findById(id).get();
        customer.setBlocked(false);
        customer.setActivated(true);
        customerRepository.save(customer);
    }

    @Override
    public Customer update(String email, String name, Long mobile) {
        Customer customer = customerRepository.findByEmail(email);
        customer.setName(name);
        customer.setMobile(mobile);
        return customerRepository.save(customer);
    }

    @Override
    public Customer getByResetPasswordToken(String token) {
        return null;
    }

    @Override
    public void updatePassword(Customer customer, String newPassword) {
        customer.setPassword(newPassword);

        customer.setResetPasswordToken(null);
        customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerOptional.orElse(null);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
        }
    }


//    @Override
//    public Customer updateResetPasswordToken(String token) {
//        Customer customer = customerRepository.findByEmail(email);
//        if (customer != null) {
//            customer.setResetPasswordToken(token);
//            customerRepository.save(customer);
//        }
//    }
}
