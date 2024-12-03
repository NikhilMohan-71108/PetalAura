package com.petalaura.library.Repository;

import com.petalaura.library.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends  JpaRepository<Coupon,Long>{
    @Query("Select c  from Coupon c where c.couponcode =?1")
    Coupon findByCouponCode(String couponcode);
    Coupon findById(long id);
}
