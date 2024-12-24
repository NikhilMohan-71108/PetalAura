package com.petalaura.library.Service.impl;


import com.petalaura.library.Repository.OrderRepository;
import com.petalaura.library.Repository.WalletHistoryRepository;
import com.petalaura.library.Repository.WalletRepository;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.dto.WalletHistoryDto;
import com.petalaura.library.enums.WalletTransactionHistory;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.Wallet;
import com.petalaura.library.model.WalletHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class WalletServiceImpl  implements WalletService {
    private WalletRepository walletRepository;

    private WalletHistoryRepository walletHistoryRepository;

    @Autowired
    OrderRepository orderRepository;

    public WalletServiceImpl(WalletRepository walletRepository,
                             WalletHistoryRepository walletHistoryRepository) {
        this.walletHistoryRepository=walletHistoryRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public List<WalletHistoryDto> findAllById(long id) {
        List<WalletHistory> walletHistory =walletHistoryRepository.findAllById(id);
        List<WalletHistoryDto> walletHistoryDtoList=transferData(walletHistory);

        return walletHistoryDtoList;
    }

    @Override
    public Wallet   findByCustomer(Long customerId) {
        return walletRepository.findByCustomer(customerId);
    }

//    @Override
//    public Wallet findByCustomer(Customer customer) {
//        Wallet wallet;
//        if(customer.getWallet()==null){
//            wallet=new Wallet();
//            wallet.setBalance(0.0);
//            wallet.setCustomer(customer);
//            walletRepository.save(wallet);
//        }else{
//            wallet=customer.getWallet();
//
//        }
//        return wallet;
//    }

    @Override
    public WalletHistory save(double amount, Customer customer) {
        Wallet wallet=customer.getWallet();
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionHistory.CREDITED);
        walletHistory.setAmount(amount);
        walletHistoryRepository.save(walletHistory);

        return walletHistory;
    }

    @Override
    public WalletHistory findById(long id) {

        WalletHistory walletHistory=walletHistoryRepository.findById(id);
        return walletHistory;
    }

    @Override
    public void updateWallet(WalletHistory walletHistory, Customer customer,boolean status) {
        Wallet wallet=customer.getWallet();
        if(status) {
            walletHistory.setTransactionStatus("Success");
            walletHistoryRepository.save(walletHistory);
            wallet.setBalance(wallet.getBalance()+ walletHistory.getAmount());
            walletRepository.save(wallet);
        }else{
            walletHistory.setTransactionStatus("Failed");
            walletHistoryRepository.save(walletHistory);
        }


    }

    @Override
    public void debit(Wallet wallet,double totalPrice) {
        wallet.setBalance(wallet.getBalance()-totalPrice);
        walletRepository.save(wallet);
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionHistory.DEBITED);
        walletHistory.setAmount(totalPrice);
        walletHistory.setTransactionStatus("Success");
        walletHistoryRepository.save(walletHistory);
    }

    @Override
    public void returnCredit(Order order,Customer customer) {
        Wallet wallet=customer.getWallet();
        wallet.setBalance(wallet.getBalance()+order.getGrandTotalPrize());
        walletRepository.save(wallet);
        WalletHistory walletHistory=new WalletHistory();
        walletHistory.setWallet(wallet);
        walletHistory.setType(WalletTransactionHistory.CREDITED);
        walletHistory.setTransactionStatus("Success");
        walletHistory.setAmount(order.getGrandTotalPrize());
        walletHistoryRepository.save(walletHistory);

    }

    @Override
    public void addToRefundAmount(Long id) {
        Order order = orderRepository.getReferenceById(id);
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


    public List<WalletHistoryDto> transferData(List<WalletHistory> walletHistoryList){

        List<WalletHistoryDto>walletHistoryDtoList=new ArrayList<>();

        for(WalletHistory walletHistory : walletHistoryList){
            WalletHistoryDto walletHistoryDto=new WalletHistoryDto();
            walletHistoryDto.setId(walletHistory.getId());
            walletHistoryDto.setType(walletHistory.getType());
            walletHistoryDto.setAmount(walletHistory.getAmount());
            walletHistoryDto.setWallet(walletHistory.getWallet());
            walletHistoryDto.setTransactionStatus(walletHistory.getTransactionStatus());
            walletHistoryDtoList.add(walletHistoryDto);
        }


        return walletHistoryDtoList;
    }

}
