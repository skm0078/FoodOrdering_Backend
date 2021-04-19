package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch a single customer by id
     *
     * @param customerId : to retch details
     * @return Customer details
     */
    public CustomerEntity getCustomerById(final String customerId) {
        try {
            return entityManager
                    .createNamedQuery("customerByCustomerId", CustomerEntity.class)
                    .setParameter("customerId", customerId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch a single customer by customer Number
     *
     * @param customerContact : to retch details
     * @return User details
     */
    public CustomerEntity getContactByContactNumber(final String customerContact) {
        try {
            return entityManager
                    .createNamedQuery("customerByCustomerContact", CustomerEntity.class)
                    .setParameter("contactNumber", customerContact)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * create customer in database.
     *
     * @param customerEntity : the customerEntity body
     * @return Customer details
     */
    public CustomerEntity createUser(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /**
     * Method to get user by email
     *
     * @param email : email for which to be pulled
     * @return user details
     */
    public CustomerEntity getUserByEmail(final String email) {
        try {
            return entityManager
                    .createNamedQuery("customerByEmail", CustomerEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to update customer in database
     *
     * @param updatedCustomerEntity : UserEntity body
     * @return updated response
     */
    public CustomerEntity updateCustomerEntity(CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
        return updatedCustomerEntity;
    }

    /**
     * Method to delete customer by id
     *
     * @param customerId : customer which we want to delete
     * @return deleted response
     */
    public CustomerEntity deleteUser(final String customerId) {
        CustomerEntity deleteUser = getCustomerById(customerId);
        if (deleteUser != null) {
            this.entityManager.remove(deleteUser);
        }
        return deleteUser;
    }
}
