package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordResponse;
import com.upgrad.FoodOrderingApp.service.businness.CouponBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("")
@CrossOrigin
public class OrderController {
    @Autowired
    private CouponBusinessService couponBusinessService;

    //@Autowired
    //private OrderBusinessService orderBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader("authorization") final String authorization,
                           @PathVariable("coupon_name") final String couponName)
                            throws AuthorizationFailedException, CouponNotFoundException {
        String[] authorizedData = authorization.split(" ");
        String accessToken;
        try {
            accessToken = authorizedData[1];
        }catch (ArrayIndexOutOfBoundsException e) {
            accessToken = authorizedData[0];
        }
        CouponEntity couponEntity =  couponBusinessService.getCouponByName(accessToken, couponName);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName())
                .percent(couponEntity.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }
}
