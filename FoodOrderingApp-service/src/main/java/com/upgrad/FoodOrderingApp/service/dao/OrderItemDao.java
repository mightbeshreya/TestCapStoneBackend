package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrderItemEntity> getItemsByOrders(OrdersEntity ordersEntity) {
        try{
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getItemsByOrders", OrderItemEntity.class).setParameter("ordersEntity",ordersEntity).getResultList();
            return orderItemEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }
}
