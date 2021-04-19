package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class StateDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch a state by state id
     *
     * @param stateUuid : to retch details
     * @return User details
     */
    public StateEntity getStateById(final String stateUuid) {
        try {
            return entityManager
                    .createNamedQuery("stateById", StateEntity.class)
                    .setParameter("id", stateUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * to all addresses posted by a user
     *
     * @param
     * @return list of addresses
     */
    public List<StateEntity> getAllStates() {
        return entityManager
                .createNamedQuery("stateAll", StateEntity.class)
                .getResultList();
    }
}
