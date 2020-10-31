package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantCategoryService;
import com.upgrad.FoodOrderingApp.service.businness.StateBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("")
@CrossOrigin
public class RestaurantController {
    @Autowired
    private RestaurantBusinessService restaurantBusinessService;

    @Autowired
    private AddressBusinessService addressBusinessService;

    @Autowired
    private StateBusinessService stateBusinessService;

    @Autowired
    private RestaurantCategoryService restaurantCategoryService;

    @RequestMapping(method = RequestMethod.GET,path = "/restaurant",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        List<RestaurantEntity> listOfRestaurants = restaurantBusinessService.getAllRestaurants();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for(RestaurantEntity r: listOfRestaurants) {
            RestaurantList restaurantDetails = new RestaurantList();
            restaurantDetails.setId(UUID.fromString(r.getUuid()));
            restaurantDetails.setRestaurantName(r.getRestaurantName());
            restaurantDetails.setPhotoURL(r.getPhotoUrl());
            restaurantDetails.setCustomerRating(r.getCustomerRating());
            restaurantDetails.setAveragePrice(r.getAvgPriceForTwo());
            restaurantDetails.setNumberCustomersRated(r.getNumOfCustomersRated());

            AddressEntity restaurantAddress = addressBusinessService.getAddressById(r.getAddress().getUuid());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
            responseAddress.setId(UUID.fromString(restaurantAddress.getUuid()));
            responseAddress.setFlatBuildingName(restaurantAddress.getFlat_buil_number());
            responseAddress.setLocality(restaurantAddress.getLocality());
            responseAddress.setCity(restaurantAddress.getCity());
            responseAddress.setPincode(restaurantAddress.getPincode());

            StateEntity restaurantStateEntity = stateBusinessService.getStateById(restaurantAddress.getState_id().getUuid());

            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
            responseAddressState.setId(UUID.fromString(restaurantStateEntity.getUuid()));
            responseAddressState.setStateName(restaurantStateEntity.getState_name());

            responseAddress.setState(responseAddressState);

            restaurantDetails.setAddress(responseAddress);

            List<RestaurantCategoryEntity> restaurantCategories = restaurantCategoryService.getRestaurantCategories(r);
            List<String> stringCategories = new ArrayList<>();
            for(RestaurantCategoryEntity c: restaurantCategories) {
                stringCategories.add(c.getCategory().getCategoryName());
            }
            Collections.sort(stringCategories);

            restaurantDetails.setCategories(String.join(", ", stringCategories));

            restaurantListResponse.addRestaurantsItem(restaurantDetails);
        }
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }
}
