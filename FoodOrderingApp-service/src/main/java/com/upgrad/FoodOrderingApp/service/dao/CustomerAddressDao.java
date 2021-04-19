package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAddressDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch a address by customer
     *
     * @param customerEntity : to retch details
     * @return address Entity
     */
    public AddressEntity getAddressByCustomer(final CustomerEntity customerEntity) {
        try {
            return entityManager
                    .createNamedQuery("addressByCustomer", CustomerAddressEntity.class)
                    .setParameter("customerEntity", customerEntity)
                    .getSingleResult().getAddressEntity();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch a customer by address
     *
     * @param addressEntity : to retch details
     * @return Customer Entity
     */
    public CustomerEntity getCustomerByAddress(final AddressEntity addressEntity) {
        try {
            return entityManager
                    .createNamedQuery("customerByAddress", CustomerAddressEntity.class)
                    .setParameter("addressEntity", addressEntity)
                    .getSingleResult().getCustomerEntity();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch a Customer Address by customerAddress Id
     *
     * @param id : to retch details
     * @return Customer Address details
     */
    public CustomerAddressEntity getStateById(final String id) {
        try {
            return entityManager
                    .createNamedQuery("customerAddById", CustomerAddressEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch customer address by address
     *
     * @param addressEntity: AddressEntity
     * @return CustomerAddressEntity
     */
    public CustomerAddressEntity getCustomerAddressByAddress(AddressEntity addressEntity) {
        try {
            CustomerAddressEntity customerAddressEntity =
                    entityManager
                            .createNamedQuery("getCustomerAddressByAddress", CustomerAddressEntity.class)
                            .setParameter("addressEntity", addressEntity)
                            .getSingleResult();
            return customerAddressEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

}
