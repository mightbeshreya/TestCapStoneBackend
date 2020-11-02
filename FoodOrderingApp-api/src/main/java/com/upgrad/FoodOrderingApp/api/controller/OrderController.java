package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CouponService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("")
@CrossOrigin
public class OrderController {
    @Autowired
    private CouponService couponBusinessService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader("authorization") final String authorization,
                           @PathVariable("coupon_name") final String couponName)
                            throws AuthorizationFailedException, CouponNotFoundException {
        /*String[] authorizedData = authorization.split(" ");
        String accessToken;
        try {
            accessToken = authorizedData[1];
        }catch (ArrayIndexOutOfBoundsException e) {
            accessToken = authorizedData[0];
        } */

        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        CouponEntity couponEntity =  orderService.getCouponByCouponName(couponName);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName())
                .percent(couponEntity.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrdersOfUser(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, AuthorizationFailedException {

        /*String accessToken = authorization.split("Bearer ")[1];

        CustomerAuthEntity customerEntity = customerService.getCustomer(accessToken);

        if (accessToken.equals(null)) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (customerEntity.getLogoutAt() != null && accessToken != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        final ZonedDateTime customerSessionExpireTime = ZonedDateTime.now();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        if (customerSessionExpireTime.compareTo(customerEntity.getExpiresAt()) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        } */

        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        List<OrderEntity> ordersEntities = orderService.getOrdersByCustomers(customerEntity.getUuid());

        List<OrderList> orderLists = new LinkedList<>();

        if (ordersEntities != null) {
            for (OrderEntity orderEntity : ordersEntities) {

                List<OrderItemEntity> orderItemEntities = orderService.getOrderItemsByOrder(orderEntity);

                List<ItemQuantityResponse> itemQuantityResponseList = new LinkedList<>();
                orderItemEntities.forEach(orderItemEntity -> {
                    ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                            .itemName(orderItemEntity.getItem().getItemName())
                            .itemPrice(orderItemEntity.getItem().getPrice())
                            .id(UUID.fromString(orderItemEntity.getItem().getUuid()))
                            .type(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().getValue()));
                    //Creating ItemQuantityResponse which will be added to the list
                    ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
                            .item(itemQuantityResponseItem).quantity(orderItemEntity.getQuantity())
                            .price(orderItemEntity.getPrice());
                    itemQuantityResponseList.add(itemQuantityResponse);
                });
                OrderListAddressState orderListAddressState = new OrderListAddressState()
                        .id(UUID.fromString(orderEntity.getAddress().getState().getUuid()))
                        .stateName(orderEntity.getAddress().getState().getStateName());

                OrderListAddress orderListAddress = new OrderListAddress().id(UUID.fromString(orderEntity.getAddress().getUuid()))
                        .flatBuildingName(orderEntity.getAddress().getFlat_buil_number())
                        .locality(orderEntity.getAddress().getLocality()).city(orderEntity.getAddress().getCity())
                        .pincode(orderEntity.getAddress().getPincode()).state(orderListAddressState);
                OrderListCoupon orderListCoupon = new OrderListCoupon().couponName(orderEntity.getCoupon().getCouponName())
                        .id(UUID.fromString(orderEntity.getCoupon().getUuid())).percent(orderEntity.getCoupon().getPercent());

                OrderListCustomer orderListCustomer = new OrderListCustomer()
                        .id(UUID.fromString(orderEntity.getCustomer().getUuid()))
                        .firstName(orderEntity.getCustomer().getFirstname())
                        .lastName(orderEntity.getCustomer().getLastname())
                        .emailAddress(orderEntity.getCustomer().getEmail())
                        .contactNumber(orderEntity.getCustomer().getContactNumber());

                OrderListPayment orderListPayment = new OrderListPayment()
                        .id(UUID.fromString(orderEntity.getPayment().getUuid()))
                        .paymentName(orderEntity.getPayment().getPaymentName());

                OrderList orderList = new OrderList().id(UUID.fromString(orderEntity.getUuid())).itemQuantities(itemQuantityResponseList)
                        .address(orderListAddress).bill(BigDecimal.valueOf(orderEntity.getBill()))
                        .date(String.valueOf(orderEntity.getDate())).discount(BigDecimal.valueOf(orderEntity.getDiscount()))
                        .coupon(orderListCoupon).customer(orderListCustomer).payment(orderListPayment);
                orderLists.add(orderList);
            }
            CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse()
                    .orders(orderLists);
            return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<CustomerOrderResponse>(new CustomerOrderResponse(), HttpStatus.OK);
        }

    }
/*
    @RequestMapping(method = RequestMethod.POST, path = "/order", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody(required = false) final SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException
    {
        String[] authorizedData = authorization.split(" ");
        String accessToken;
        try {
            accessToken = authorizedData[1];
        }catch (ArrayIndexOutOfBoundsException e) {
            accessToken = authorizedData[0];
        }

        String couponUuid = saveOrderRequest.getCouponId().toString();
        String addressUuid = saveOrderRequest.getAddressId();
        String paymentUuid = saveOrderRequest.getPaymentId().toString();
        String restaurantUuid = saveOrderRequest.getRestaurantId().toString();
        BigDecimal bill = saveOrderRequest.getBill();

        List<ItemQuantity> itemList = saveOrderRequest.getItemQuantities();
        String itemUuid = itemList.get(0).getItemId().toString();
        OrderList orderList = new OrderList();
        OrderEntity orderEntity = orderBusinessService.saveOrderList(accessToken,couponUuid,
                addressUuid,paymentUuid,restaurantUuid,itemUuid, bill);
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse().id(orderEntity.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
    }

    */
}
