package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    /**
     * This method returns the list of all categories.
     */
    /**
     * @return -  List<CategoryEntity>
     * @exception - none
     */
    public List<CategoryEntity> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    /**
     * This method receives category Uuid.
     * This method is verifies the category uuid and accessToken, and returns category based on its category uuid.
     */
    /**
     * @param categoryId - category uuid
     * @return -  CategoryEntity object
     * @exception - CategoryNotFoundException, category field is empty or no category is found for that particular id.
     */
    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {
        if (categoryId.isEmpty() || categoryId == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryId);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;
    }

    /**
     * This method receives restaurant entity.
     * This method is returns categories for a given restaurant.
     */
    /**
     * @param restaurantEntity - Restaurant entity
     * @return -  List<CategoryEntity>
     * @exception - none.
     */
    public List<CategoryEntity> getCategoriesForRestaurant(RestaurantEntity restaurantEntity) {
        List<RestaurantCategoryEntity> restaurantCategoryEntities = categoryDao.getCategoriesByRestaurant(restaurantEntity);

        List<CategoryEntity> categoryEntities = new ArrayList<>();

        for (RestaurantCategoryEntity restaurantCategoryEntity: restaurantCategoryEntities) {
            categoryEntities.add(categoryDao.getCategoryById(restaurantCategoryEntity.getCategoryId().getUuid()));
        }
        return categoryEntities;
    }
}
