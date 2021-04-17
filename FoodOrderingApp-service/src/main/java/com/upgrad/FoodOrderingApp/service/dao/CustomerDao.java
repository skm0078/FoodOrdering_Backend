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
     * Fetch a single user by id
     *
     * @param userId : to retch details
     * @return User details
     */
    public CustomerEntity getUserById(final String userId) {
        try {
            return entityManager
                    .createNamedQuery("userByUserId", CustomerEntity.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    /**
     * create user in database.
     *
     * @param userEntity : the userEntity body
     * @return User details
     */
    public CustomerEntity createUser(CustomerEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Method to get user by name
     *
     * @param userName : username for which to be pulled
     * @return user details
     */
    public CustomerEntity getUserByUserName(final String userName) {
        try {
            return entityManager
                    .createNamedQuery("userByUserName", CustomerEntity.class)
                    .setParameter("userName", userName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
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
                    .createNamedQuery("userByEmail", CustomerEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to update user in database
     *
     * @param updatedUserEntity : UserEntity body
     * @return updated response
     */
    public void updateUserEntity(final CustomerEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    /**
     * Method to delete user by id
     *
     * @param userId : username which we want to delete
     * @return deleted response
     */
    public CustomerEntity deleteUser(final String userId) {
        CustomerEntity deleteUser = getUserById(userId);
        if (deleteUser != null) {
            this.entityManager.remove(deleteUser);
        }
        return deleteUser;
    }
}
