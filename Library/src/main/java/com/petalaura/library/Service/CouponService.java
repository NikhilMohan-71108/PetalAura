package com.petalaura.library.Service;


import com.petalaura.library.model.Coupon;

import java.util.List;

public interface CouponService
{
    List<Coupon> findAll();
    void save(Coupon coupon);

    void disableCoupon(Long id);

    void enableCoupon(Long id);

    Coupon findByid(Long id);

    void updateCoupon(Coupon coupon);

    void deleteCoupon(Long id);

    Coupon findByCouponCode(String couponcode);

    void decreaseCoupon(long id);
}
