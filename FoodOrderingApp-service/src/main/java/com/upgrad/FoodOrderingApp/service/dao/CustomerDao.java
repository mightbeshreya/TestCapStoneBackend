package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
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
            return entityManager.createNamedQuery("userByUuid", CustomerEntity.class).setParameter("uuid", uuid)
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

    public CustomerAuthEntity createAuthToken(CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    public CustomerAuthEntity checkAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("getToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateCustomerAuthToken(CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.merge(customerAuthTokenEntity);
    }

    public CustomerEntity updateCustomerDetails(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
        return customerEntity;
    }

    public CustomerEntity updatePassword(CustomerEntity updatedCustomerPassword) {
        entityManager.merge(updatedCustomerPassword);
        return updatedCustomerPassword;
    }

    public CustomerEntity getCustomerByUuid(String customerUuid) {
        try {
            CustomerEntity customer = entityManager.createNamedQuery("userByUuid", CustomerEntity.class).setParameter("uuid", customerUuid).getSingleResult();
            return customer;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateCustomerLogoutAt(final CustomerAuthEntity customerAuthToken) {
        entityManager.merge(customerAuthToken);
    }
}
