package com.petalaura.library.Repository;


import com.petalaura.library.model.Customer;
import com.petalaura.library.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByCustomer(Customer customer);;

}
