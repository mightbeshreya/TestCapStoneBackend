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

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;
    @PersistenceUnit
    private EntityManagerFactory emf;

    public AddressEntity saveAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public AddressEntity getAddressByUuid(String uuid) {
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

    public StateEntity getState(String uuid) {
        StateEntity s = null;
        EntityManager em = emf.createEntityManager();
        s = em.find(StateEntity.class, uuid);
        em.close();
        return s;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity save(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAddress(String address_uuid) {
        try {
            AddressEntity addressEntity = getAddressByUuid(address_uuid);
            entityManager.remove(addressEntity);
            return addressEntity.getUuid();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AddressEntity getAddressById(Integer id) {
        try {
            return entityManager.createNamedQuery("getAddressById", AddressEntity.class)
                    .setParameter("id", id).getSingleResult();
        }catch(NoResultException nre) {
            return null;
        }
    }
}
