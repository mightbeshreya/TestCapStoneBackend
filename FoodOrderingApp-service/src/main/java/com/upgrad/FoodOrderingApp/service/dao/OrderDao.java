package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrdersEntity> getOrderByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant", OrdersEntity.class).setParameter("restaurant", restaurantEntity).getResultList();
            return ordersEntities;
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<OrdersEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        try {
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByCustomer", OrdersEntity.class).setParameter("customer", customerEntity).getResultList();
            return ordersEntities;
        } catch (NoResultException nre){
            return null;
        }
    }

    public OrdersEntity saveOrder(OrdersEntity ordersEntity) {
        try {
            entityManager.persist(ordersEntity);
            return ordersEntity;
        }catch (Exception e) {
            return null;
        }
    }
}
