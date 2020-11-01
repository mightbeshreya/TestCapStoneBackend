package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class CouponBusinessService {
    @Autowired
    private CouponDao couponDao;

    @Autowired
    private CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByName(final String accessToken, final String couponName) throws AuthorizationFailedException,
            CouponNotFoundException {
        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.checkAuthToken(accessToken);
        if(customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if(customerAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        if(customerAuthTokenEntity.getExpiresAt().isBefore(now) || customerAuthTokenEntity.getExpiresAt().isEqual(now)){
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        if(couponName == null || couponName == "") {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity couponEntity = couponDao.getCouponByName(couponName);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return couponEntity;
    }
}
