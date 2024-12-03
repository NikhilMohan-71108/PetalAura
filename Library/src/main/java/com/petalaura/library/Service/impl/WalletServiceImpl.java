package com.petalaura.library.Service.impl;


import com.petalaura.library.Repository.OrderRepository;
import com.petalaura.library.Repository.WalletHistoryRepository;
import com.petalaura.library.Repository.WalletRepository;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.enums.WalletTransactionHistory;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.Wallet;
import com.petalaura.library.model.WalletHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WalletServiceImpl  implements WalletService {
    @Autowired
    WalletRepository walletRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WalletHistoryRepository walletHistoryRepository;

    @Override
    public void addToRefundAmount(Long orderId) {
        Order order = orderRepository.getReferenceById(orderId);
        Wallet wallet = walletRepository.findByCustomer(order.getCustomer().getCustomer_id());


        if (wallet != null) {
            double newBalance = Math.round((wallet.getBalance() + order.getGrandTotalPrize()) * 100.0) / 100.0;
            wallet.setBalance(newBalance);
        } else {
            wallet = new Wallet();
            wallet.setCustomer(order.getCustomer());
            double newBalance = Math.round(order.getGrandTotalPrize() * 100.0) / 100.0;
            wallet.setBalance(newBalance);
        }
        walletRepository.save(wallet);


        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setAmount(order.getGrandTotalPrize());
        walletHistory.setTransactionStatus("Refunded Amount");
        walletHistory.setType(WalletTransactionHistory.CREDITED);

        walletHistoryRepository.save(walletHistory);
    }

    @Override
    public Wallet findByCustomer(Long customerId) {
        return walletRepository.findByCustomer(customerId);
    }

    @Override
    public List<WalletHistory> findAllById(Long id) {
        return List.of();
    }

    @Override
    public void debit(Wallet wallet, Double amount) {
        if(wallet.getBalance()>=amount);{
            wallet.setBalance(wallet.getBalance()-amount);
        }
        walletRepository.save(wallet);
    }

    @Override
    public Wallet findByCustomerByUsername(String username) {
        return walletRepository.findByCustomerByUsername(username);
    }


    @Override
    public List<WalletHistory> findAllByCustomerName(String username) {
        return walletHistoryRepository.findAllByCustomerName(username);
    }
}
