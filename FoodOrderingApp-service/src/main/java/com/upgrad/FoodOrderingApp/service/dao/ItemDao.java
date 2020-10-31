package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class ItemDao {

    @PersistenceContext
    EntityManager entityManager;

    public ItemEntity getItemByUuid(String uuid){
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("itemByUuid", ItemEntity.class).setParameter("uuid", uuid).getSingleResult();
            return itemEntity;
        } catch (NoResultException nre){
            return null;
        }
    }
}
