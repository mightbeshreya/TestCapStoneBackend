package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Autowired
    CategoryBusinessService categoryBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        List<RestaurantEntity> listOfRestaurants = restaurantBusinessService.getAllRestaurants();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity r : listOfRestaurants) {
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
            for (RestaurantCategoryEntity c : restaurantCategories) {
                stringCategories.add(c.getCategory().getCategoryName());
            }
            Collections.sort(stringCategories);

            restaurantDetails.setCategories(String.join(", ", stringCategories));

            restaurantListResponse.addRestaurantsItem(restaurantDetails);
        }
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{reastaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurantByName(@PathVariable("reastaurant_name") final String restaurantName) throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantEntities = restaurantBusinessService.restaurantByName(restaurantName);
        if (!restaurantEntities.isEmpty()) {
            List<RestaurantList> restaurantLists = new LinkedList<>();
            for (RestaurantEntity restaurantEntity : restaurantEntities) {
                List<CategoryEntity> categoryEntities = categoryBusinessService.getCategoriesByRestaurant(restaurantEntity.getUuid());
                String categories = new String();
                ListIterator<CategoryEntity> listIterator = categoryEntities.listIterator();

                while (listIterator.hasNext()) {
                    categories = categories + listIterator.next().getCategoryName();
                    if (listIterator.hasNext()) {
                        categories = categories + ", ";
                    }
                }
                AddressEntity restaurantAddress = addressBusinessService.getAddressById(restaurantEntity.getAddress().getUuid());

                RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
                responseAddress.setId(UUID.fromString(restaurantAddress.getUuid()));
                responseAddress.setFlatBuildingName(restaurantAddress.getFlat_buil_number());
                responseAddress.setLocality(restaurantAddress.getLocality());
                responseAddress.setCity(restaurantAddress.getCity());
                responseAddress.setPincode(restaurantAddress.getPincode());

                RestaurantList restaurantDetails = new RestaurantList();
                restaurantDetails.setId(UUID.fromString(restaurantEntity.getUuid()));
                restaurantDetails.setRestaurantName(restaurantEntity.getRestaurantName());
                restaurantDetails.setPhotoURL(restaurantEntity.getPhotoUrl());
                restaurantDetails.setCustomerRating(restaurantEntity.getCustomerRating());
                restaurantDetails.setAveragePrice(restaurantEntity.getAvgPriceForTwo());
                restaurantDetails.setNumberCustomersRated(restaurantEntity.getNumOfCustomersRated());

                restaurantLists.add(restaurantDetails);
            }

            RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<RestaurantListResponse>(new RestaurantListResponse(), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategoryId(
            @PathVariable(value = "category_id") final String categoryId) throws CategoryNotFoundException {
        List<RestaurantEntity> listOfCategoryRestaurants = restaurantCategoryService.getRestaurantsByCategoryId(categoryId);

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity r : listOfCategoryRestaurants) {
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
            for (RestaurantCategoryEntity c : restaurantCategories) {
                stringCategories.add(c.getCategory().getCategoryName());
            }
            Collections.sort(stringCategories);

            restaurantDetails.setCategories(String.join(", ", stringCategories));

            restaurantListResponse.addRestaurantsItem(restaurantDetails);
        }
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);

    }
}

