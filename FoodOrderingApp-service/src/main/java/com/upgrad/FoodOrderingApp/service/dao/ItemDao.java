package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method receives item uuid.
     * This method is used to fetch item depending on item uuid the item table in the database.
     */
    /**
     * @param itemId - item uuid
     * @return - ItemEntity
     */
    public ItemEntity getItemById(String itemId) {
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("getItemById", ItemEntity.class)
                                                 .setParameter("uuid", itemId)
                                                 .getSingleResult();
            return itemEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives CategoryEntity object.
     * This method is used to fetch items belonging to particular category from the category_item table in the database.
     */
    /**
     * @param categoryEntity - CategoryEntity object
     * @return - List<CategoryItemEntity>
     */
    public List<CategoryItemEntity> getItemsByCategoryId(CategoryEntity categoryEntity) {
        try {
            List<CategoryItemEntity> categoryItemEntities = entityManager.createNamedQuery("getItemsByCategoryId", CategoryItemEntity.class)
                                                                         .setParameter("categoryId", categoryEntity)
                                                                         .getResultList();
            return categoryItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives RestaurantEntity object.
     * This method is used to fetch items belonging to particular restaurant from the restaurant_item table in the database.
     */
    /**
     * @param restaurantEntity - RestaurantEntity object
     * @return - List<RestaurantItemEntity>
     */
    public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<RestaurantItemEntity> restaurantItemEntities = entityManager.createNamedQuery("getItemsByRestaurant", RestaurantItemEntity.class)
                                                                             .setParameter("restaurantId", restaurantEntity)
                                                                             .getResultList();
            return restaurantItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
