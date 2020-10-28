package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;


    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    public CustomerEntity IsContactNumberExists(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class).setParameter("contactNumber", contactNumber).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity checkUUID(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUUID", CustomerEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity getCustomerByEmail(String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", CustomerEntity.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthTokenEntity createAuthToken(CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    public CustomerAuthTokenEntity checkAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("getToken", CustomerAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateCustomerAuthToken(CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.merge(customerAuthTokenEntity);
    }

    public CustomerEntity updateCustomerDetails(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
        return customerEntity;
    }
}