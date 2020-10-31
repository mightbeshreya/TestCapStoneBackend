package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants() {
        try {
            List<RestaurantEntity> allRestaurants = entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class)
                    .getResultList();
            return allRestaurants;
        }catch (NoResultException nre) {
            return null;
        }
    }
}
