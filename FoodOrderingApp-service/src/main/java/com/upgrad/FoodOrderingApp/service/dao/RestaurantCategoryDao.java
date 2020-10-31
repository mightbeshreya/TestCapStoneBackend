package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantCategoryDao {
    @PersistenceContext
    EntityManager entityManager;

    public List<RestaurantCategoryEntity> getRestaurantCategories(RestaurantEntity restaurantEntity) {
        try{
            return entityManager.createNamedQuery("getRestaurantCategories", RestaurantCategoryEntity.class)
                    .setParameter("restaurant", restaurantEntity).getResultList();
        }catch(NoResultException nre) {
            return null;
        }
    }
}
