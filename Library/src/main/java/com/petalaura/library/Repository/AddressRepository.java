package com.petalaura.library.Repository;

import com.petalaura.library.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository  extends JpaRepository<Address, Long> {
    @Query("select a from Address a where a.customer.email=?1")
    List<Address> findAddressByCustomer(String email);
}
