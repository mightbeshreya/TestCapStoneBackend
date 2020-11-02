package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
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
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private StateBusinessService stateBusinessService;

    @Autowired
    private RestaurantCategoryService restaurantCategoryService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    @Autowired
    CustomerService customerService;
/*
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

            AddressEntity restaurantAddress = addressService.getAddressById(r.getAddress().getUuid());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
            responseAddress.setId(UUID.fromString(restaurantAddress.getUuid()));
            responseAddress.setFlatBuildingName(restaurantAddress.getFlat_buil_number());
            responseAddress.setLocality(restaurantAddress.getLocality());
            responseAddress.setCity(restaurantAddress.getCity());
            responseAddress.setPincode(restaurantAddress.getPincode());

            StateEntity restaurantStateEntity = stateBusinessService.getStateById(restaurantAddress.getState_id().getUuid());

            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
            responseAddressState.setId(UUID.fromString(restaurantStateEntity.getUuid()));
            responseAddressState.setStateName(restaurantStateEntity.getStateName());

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
                AddressEntity restaurantAddress = addressService.getAddressById(restaurantEntity.getAddress().getUuid());

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

            AddressEntity restaurantAddress = addressService.getAddressById(r.getAddress().getUuid());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
            responseAddress.setId(UUID.fromString(restaurantAddress.getUuid()));
            responseAddress.setFlatBuildingName(restaurantAddress.getFlat_buil_number());
            responseAddress.setLocality(restaurantAddress.getLocality());
            responseAddress.setCity(restaurantAddress.getCity());
            responseAddress.setPincode(restaurantAddress.getPincode());

            StateEntity restaurantStateEntity = stateBusinessService.getStateById(restaurantAddress.getState_id().getUuid());

            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
            responseAddressState.setId(UUID.fromString(restaurantStateEntity.getUuid()));
            responseAddressState.setStateName(restaurantStateEntity.getStateName());

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
*/

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantID(
            @PathVariable("restaurant_id") final String restaurantUuid)
            throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        //RestaurantDetailsResponse restaurant = new RestaurantDetailsResponse();

        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantUuid);

        List<CategoryList> categoryLists = new LinkedList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {

            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryEntity.getUuid());
            List<ItemList> itemLists = new LinkedList<>();
            itemEntities.forEach(itemEntity -> {
                ItemList itemList = new ItemList().id(UUID.fromString(itemEntity.getUuid()))
                        .itemName(itemEntity.getItemName()).price(itemEntity.getPrice())
                        .itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().getValue()));

                itemLists.add(itemList);
            });
            CategoryList categoryList = new CategoryList()
                    .itemList(itemLists)
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());

            categoryLists.add(categoryList);
        }

        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState();
        restaurantDetailsResponseAddressState.setId(UUID.fromString(restaurantEntity.getAddress().getState_id().getUuid()));
        restaurantDetailsResponseAddressState.setStateName(restaurantEntity.getAddress().getState_id().getStateName());

        //Creating the RestaurantDetailsResponseAddress for the RestaurantList
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                .city(restaurantEntity.getAddress().getCity())
                .flatBuildingName(restaurantEntity.getAddress().getFlat_buil_number())
                .locality(restaurantEntity.getAddress().getLocality())
                .pincode(restaurantEntity.getAddress().getPincode())
                .state(restaurantDetailsResponseAddressState);

        //Creating the RestaurantDetailsResponse by adding the list of categoryList and other details.
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                .restaurantName(restaurantEntity.getRestaurantName())
                .address(restaurantDetailsResponseAddress)
                .averagePrice(restaurantEntity.getAvgPriceForTwo())
                .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                .numberCustomersRated(restaurantEntity.getNumOfCustomersRated())
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .photoURL(restaurantEntity.getPhotoUrl())
                .categories(categoryLists);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    /*
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestParam Double customerRating , @PathVariable("restaurant_id") final String restaurant_id, @RequestHeader("authorization") final String authorization) throws RestaurantNotFoundException, InvalidRatingException, AuthorizationFailedException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setUuid(restaurant_id);
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }
        restaurantEntity.setCustomerRating(BigDecimal.valueOf(customerRating));
        RestaurantEntity updatedRestaurantEntity = restaurantBusinessService.updateRestaurantDetails(restaurantEntity,bearerToken);
        RestaurantUpdatedResponse restUpdateResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(updatedRestaurantEntity.getUuid()))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restUpdateResponse, HttpStatus.OK);
    }
*/
}

