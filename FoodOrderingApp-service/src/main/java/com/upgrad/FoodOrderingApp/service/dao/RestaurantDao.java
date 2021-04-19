package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method is used to fetch list of all restaurants from restaurant table in the database.
     */
    /**
     * @return - List<RestaurantEntity>
     */
    public List<RestaurantEntity> getAllRestaurants() {
        try {
            List<RestaurantEntity> restaurantList = entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class).getResultList();
            return restaurantList;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives restaurant Name.
     * This method is used to fetch restaurants depending on name from the restaurant table in the database.
     */
    /**
     * @param restaurantName - restaurant Name
     * @return - List<RestaurantEntity>
     */
    public List<RestaurantEntity> getRestaurantsByName(String restaurantName) {
        try {
            List<RestaurantEntity> restaurantList = entityManager.createNamedQuery("getRestaurantsByName", RestaurantEntity.class)
                                                                 .setParameter("restaurantName", restaurantName.toLowerCase())
                                                                 .getResultList();
            return restaurantList;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives restaurant uuid.
     * This method is used to fetch restaurant based on restaurant uuid from restaurant table in the database.
     */
    /**
     * @param restaurantId - Restaurant uuid
     * @return - RestaurantEntity
     */
    public RestaurantEntity getRestaurantById(String restaurantId) {
        try {
            RestaurantEntity restaurantEntity = entityManager.createNamedQuery("getRestaurantById", RestaurantEntity.class)
                                                             .setParameter("uuid", restaurantId)
                                                             .getSingleResult();
            return restaurantEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives CategoryEntity object.
     * This method is used to fetch restaurant belonging to particular category from the restaurant_category table in the database.
     */
    /**
     * @param categoryEntity - CategoryEntity object
     * @return - List<RestaurantCategoryEntity>
     */
    public List<RestaurantCategoryEntity> getRestaurantsByCategoryId(CategoryEntity categoryEntity) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities = entityManager.createNamedQuery("getRestaurantsByCategoryId", RestaurantCategoryEntity.class)
                    .setParameter("categoryId", categoryEntity)
                    .getResultList();
            return restaurantCategoryEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives RestaurantEntity object.
     * This method is used to update restaurant rating and save the updated entity in Restaurant table.
     */
    /**
     * @param restaurantEntity - RestaurantEntity object
     * @return - RestaurantEntity
     */
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }
}
