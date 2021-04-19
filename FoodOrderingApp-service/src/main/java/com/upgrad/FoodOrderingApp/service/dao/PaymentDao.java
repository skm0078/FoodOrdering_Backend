package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaymentDao {

    @PersistenceContext EntityManager entityManager;

    /**
     * Fetch all payments methods
     *
     * @return List<PaymentEntity>
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        try {
            return entityManager
                    .createNamedQuery("GetPaymentMethods", PaymentEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch payment entity based pn payment uuid
     *
     * @param uuid: payment uuid
     * @return PaymentEntity
     */
    public PaymentEntity getPaymentByUUID(String uuid) {
        try {
            return entityManager
                    .createNamedQuery("getPaymentByUUID", PaymentEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
