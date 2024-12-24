package com.petalaura.library.Service.impl;


import com.petalaura.library.Repository.CouponRepository;
import com.petalaura.library.Service.CouponService;
import com.petalaura.library.exception.CouponLimitExceededException;
import com.petalaura.library.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    CouponRepository couponRepository;
    @Override
    public List<Coupon> findAll() {
        List<Coupon> coupons=couponRepository.findAll();
        return coupons;
    }

    @Override
    public void save(Coupon coupon) {
        couponRepository.save(coupon);
    }

    @Override
    public void disableCoupon(Long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        coupon.setEnabled(false);
        couponRepository.save(coupon);
    }

    @Override
    public void enableCoupon(Long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        coupon.setEnabled(true);
        couponRepository.save(coupon);
    }

    @Override
    public Coupon findByid(Long id) {
        return couponRepository.getReferenceById(id);
    }

    @Override
    public void updateCoupon(Coupon coupon) {
        Coupon coupons=couponRepository.getReferenceById(coupon.getId());
        if(coupon.getStartDate()!=null){
            coupons.setStartDate(coupon.getStartDate());
        }
        else{
            coupons.setStartDate(coupons.getStartDate());
        }
        if(coupon.getExpireDate()!=null){
            coupons.setExpireDate(coupon.getExpireDate());
        }
        else{
            coupons.setExpireDate(coupons.getExpireDate());
        }

        coupons.setCouponcode(coupons.getCouponcode());
        coupons.setCouponDescription(coupons.getCouponDescription());
        coupons.setOfferPercentage(coupons.getOfferPercentage());
        coupons.setMinimumOrderAmount(coupons.getMinimumOrderAmount());
        coupons.setMaximumOfferAmount(coupons.getMaximumOfferAmount());
    

        coupons.setCount(coupons.getCount());
        couponRepository.save(coupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        couponRepository.delete(coupon);
    }

    @Override
    public Coupon findByCouponCode(String couponcode) {
        return couponRepository.findByCouponCode(couponcode);
    }

    @Override
    public void decreaseCoupon(long id) {
        Coupon coupon =couponRepository.getReferenceById(id);
        if (coupon.getCount() <=0) {
            throw new CouponLimitExceededException("Coupon limit exceeded");
        }
        coupon.setCount(coupon.getCount()-1);
      couponRepository.save(coupon);
    }
}
