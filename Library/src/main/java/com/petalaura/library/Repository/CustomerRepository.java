package com.petalaura.library.Repository;

import com.petalaura.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
Customer findByEmail(String email);
@Query(value="select * from customer where is_activated = true", nativeQuery=true)
 List<Customer> findAllByActivatedTrue();
 List<Customer> findAll();
 public Customer findByResetPasswordToken(String token);
 public Customer findByReferralToken(String token);
}
