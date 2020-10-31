package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantCategoryService {
    @Autowired
    private RestaurantCategoryDao restaurantCategoryDao;

    @Autowired
    private CategoryDao categoryDao;

    public List<RestaurantCategoryEntity> getRestaurantCategories(RestaurantEntity restaurantEntity) {
        return restaurantCategoryDao.getRestaurantCategories(restaurantEntity);
    }

    public List<RestaurantEntity> getRestaurantsByCategoryId(final String categoryId)
            throws CategoryNotFoundException {
        if(categoryId == null || categoryId == "") {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryId);
        if(categoryEntity==null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getRestaurantsByCategoryId(categoryEntity);

        if(restaurantCategoryEntities.isEmpty()) {
            return null;
        }
        List<RestaurantEntity> restaurantEntities = new ArrayList<RestaurantEntity>();
        for(RestaurantCategoryEntity rc: restaurantCategoryEntities) {
            restaurantEntities.add(rc.getRestaurant());
        }
        return restaurantEntities;
    }
}
