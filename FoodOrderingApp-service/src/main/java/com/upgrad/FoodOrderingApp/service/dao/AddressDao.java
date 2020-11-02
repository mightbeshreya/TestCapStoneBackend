package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This Class is created to access DB with respect to Address entity
@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;
    @PersistenceUnit
    private EntityManagerFactory emf;
    
    //To save the address
    public AddressEntity saveAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    //To get address by UUID if no results null is returned.
    public AddressEntity getAddressByUUID(String uuid) {
        try {
            AddressEntity addressEntity = entityManager.createNamedQuery("getAddressByUuid", AddressEntity.class).setParameter("uuid", uuid).getSingleResult();
            return addressEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public boolean IsPinCodeValid(String pinCode) {
        Pattern p = Pattern.compile("[0-9]{6}");
        if (pinCode.length() != 6)
            return false;
        return p.matcher(pinCode).matches();
    }

    //To get the State.
    public StateEntity getState(String uuid) {
        StateEntity s = null;
        EntityManager em = emf.createEntityManager();
        s = em.find(StateEntity.class, uuid);
        em.close();
        return s;
    }

    //To save the Address.
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity save(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    //To delete the Address.
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        try {
            entityManager.remove(addressEntity);
            return addressEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    //To get the Address By ID.
    public AddressEntity getAddressById(Integer id) {
        try {
            return entityManager.createNamedQuery("getAddressById", AddressEntity.class)
                    .setParameter("id", id).getSingleResult();
        }catch(NoResultException nre) {
            return null;
        }
    }

}
