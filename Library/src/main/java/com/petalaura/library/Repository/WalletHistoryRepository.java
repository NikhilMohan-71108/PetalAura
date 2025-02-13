package com.petalaura.library.Repository;


import com.petalaura.library.model.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory,Long>  {
    @Query("select wh from WalletHistory  wh where wh.wallet.customer.customer_id=?1 ")
    List<WalletHistory> findAllByCustomer(Long id);

    @Query("select wh from WalletHistory  wh where wh.wallet.customer.email=?1 ")
    List<WalletHistory> findAllByCustomerName(String username);

    WalletHistory findById(long id);
    @Query(value = "select * from wallets_history where wallet_id=:id",nativeQuery = true)
    List<WalletHistory> findAllById(@Param("id") long id);
}

