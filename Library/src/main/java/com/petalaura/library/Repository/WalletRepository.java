package com.petalaura.library.Repository;

import com.petalaura.library.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {


    @Query("select w from Wallet w where w.customer.customer_id=?1")
    Wallet findByCustomer(@Param("customerId") Long customerId);

    @Query("select w from Wallet w where w.customer.email=?1")
    Wallet findByCustomerByUsername(String username);
}