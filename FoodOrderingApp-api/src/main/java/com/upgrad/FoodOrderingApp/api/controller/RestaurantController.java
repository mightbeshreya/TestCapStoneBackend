package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class RestaurantController {

    @Autowired
    RestaurantBusinessService restaurantBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantListResponse>> getAllRestaurants() {

        List<RestaurantListResponse> restaurantList = new ArrayList<>();
        final List<RestaurantEntity> allRestaurants = restaurantBusinessService.getAllRestaurants();

        for (RestaurantEntity restaurantEntity : allRestaurants){
    }
}
