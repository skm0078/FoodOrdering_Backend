package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponBusinessService {
    @Autowired CouponDao couponDao;

    /**
     * This method receives couponName.
     * This method is to fetch coupons based on couponName.
     */
    /**
     * @param couponName - coupon name
     * @return -  CouponEntity
     * @exception - none.
     */
    public CouponEntity getCouponByCouponName(String couponName) {
        return couponDao.getCouponByCouponName(couponName);
    }

    //    public CustomerAuthEntity getCustomerByAccessToken(String access_token) {
    //        return couponDao.getCustomerByAccessToken(access_token);
    //    }

    /**
     * This method receives coupon uuid.
     * This method is to fetch coupons based on uuid.
     */
    /**
     * @param uuid - coupon uuid
     * @return -  CouponEntity
     * @exception - none.
     */
    public CouponEntity getCouponByUUID(String uuid) {
        return couponDao.getCouponByCouponId(uuid);
    }
}
