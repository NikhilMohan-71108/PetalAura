package com.petalaura.library.Service;


import com.petalaura.library.model.Wallet;
import com.petalaura.library.model.WalletHistory;

import java.util.List;

public interface WalletService {
    Wallet findByCustomerByUsername(String username);
    List<WalletHistory> findAllByCustomerName(String username);
    void addToRefundAmount(Long id);
    Wallet  findByCustomer(Long id);
    List<WalletHistory> findAllById(Long id);
    void debit(Wallet wallet, Double amount);
}
