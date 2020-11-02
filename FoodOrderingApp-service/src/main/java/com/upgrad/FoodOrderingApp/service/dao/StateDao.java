package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StateDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<StateEntity> getAllStates() {
        try {
            return entityManager.createNamedQuery("getAllStates", StateEntity.class).getResultList();
            /*Query query = entityManager.createNamedQuery("getStateByUUID");
            return new ArrayList<StateEntity>(query.getResultList()); */
        } catch (NoResultException nre) {
            return null;
        }
    }

    public StateEntity getStateByUuid(final String stateUuid) {
        try {
            return entityManager.createNamedQuery("getStateByUUID", StateEntity.class).setParameter("uuid", stateUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public StateEntity getStateById(Integer id) {
        try {
            return entityManager.createNamedQuery("getStateById", StateEntity.class)
                    .setParameter("id", id).getSingleResult();
        }catch(NoResultException nre) {
            return null;
        }
    }
}
