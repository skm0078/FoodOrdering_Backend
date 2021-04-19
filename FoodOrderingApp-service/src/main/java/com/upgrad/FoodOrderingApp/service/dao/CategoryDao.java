package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method is used to fetch all categories from the category table in the database.
     */
    /**
     * @return - List<CategoryEntity>
     */
    public List<CategoryEntity> getAllCategories() {
        try {
            List<CategoryEntity> categoryList = entityManager.createNamedQuery("getAllCategoriesSortedByName", CategoryEntity.class)
                                                             .getResultList();
            return categoryList;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives category uuid.
     * This method is used to fetch category based on category uuid from the category table in the database.
     */
    /**
     * @param uuid - category uuid
     * @return - CategoryEntity
     */
    public CategoryEntity getCategoryById(String uuid) {
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryById", CategoryEntity.class)
                                                         .setParameter("uuid", uuid)
                                                         .getSingleResult();
            return categoryEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives RestaurantEntity object.
     * This method is used to fetch categories depending on restaurant from the restaurant_category table in the database.
     */
    /**
     * @param restaurantEntity - RestaurantEntity object
     * @return - List<RestaurantCategoryEntity>
     */
    public List<RestaurantCategoryEntity> getCategoriesByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities = entityManager.createNamedQuery("getCategoriesByRestaurant", RestaurantCategoryEntity.class)
                                                                                     .setParameter("restaurantId", restaurantEntity)
                                                                                     .getResultList();
            return restaurantCategoryEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

}
