package com.petalaura.library.Service.impl;


import com.petalaura.library.Repository.OrderRepository;
import com.petalaura.library.Service.DashBoardService;
import com.petalaura.library.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashBoardService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    public DashboardServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public double findCurrentMonthOrder(Date startDate, Date endDate) {
        List<Order> orderList=orderRepository.findByOrderDateBetween(startDate,endDate);
        double ordersTotalPrice=0;
        if(!orderList.isEmpty()){
            for(Order orders:orderList) {
                ordersTotalPrice = ordersTotalPrice + orders.getGrandTotalPrize();
            }
        }
        return ordersTotalPrice;
    }

    @Override
    public long findOrdersTotal() {
        return orderRepository.count();
    }

    @Override
    public int findOrdersPending() {
        return orderRepository.countByIsAcceptIsFalse();
    }



    @Override
    public List<Object[]> retrieveDailyEarnings(int currentYr, int currentMnt) {
        return orderRepository.dailyReport(currentYr,currentMnt);
    }
    @Override
    public List<Object[]> retriveMontlyEarning(int currentYr){
        return orderRepository.monthlyReport(currentYr);
    }

    @Override
    public List<Object[]> findTotalPricesByPayment() {
        return orderRepository.findTotalPricesByPaymentMethod();
    }
}
