package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

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
return (List<CustomerAddressEntity>) entityManager.createNamedQuery("getCustomerAddress", CustomerAddressEntity.class).setParameter("customer", customerEntity).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}
