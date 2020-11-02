package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CustomerAddressDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<CustomerAddressEntity> getAddressByCustomer(final CustomerEntity customerEntity){
        try {
            return  entityManager.createNamedQuery("getCustomerAddress", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }
    public CustomerAddressEntity getSingleAddress(final AddressEntity address){
        try {
            return entityManager.createNamedQuery("getAddress", CustomerAddressEntity.class).setParameter("address", address).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity getEntityByCustomerID(CustomerEntity customerEntity) {
        try {
            return entityManager.createNamedQuery("getCustomerAddress", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }
}
