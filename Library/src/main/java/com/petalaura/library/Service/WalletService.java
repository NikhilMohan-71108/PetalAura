package com.petalaura.library.Service;


import com.petalaura.library.dto.WalletHistoryDto;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.Wallet;
import com.petalaura.library.model.WalletHistory;

import java.util.List;

public interface WalletService {
    List<WalletHistoryDto> findAllById(long id);

    Wallet findByCustomer(Long customerId);

    WalletHistory save(double amount, Customer customer);

    WalletHistory findById(long id);

    void updateWallet(WalletHistory walletHistory,Customer customer,boolean status);

    void debit(Wallet wallet,double totalPrice);

    void returnCredit(Order order, Customer customer);

    void addToRefundAmount(Long id);

    void addWalletToReferalEarn(Long customerId);
}
