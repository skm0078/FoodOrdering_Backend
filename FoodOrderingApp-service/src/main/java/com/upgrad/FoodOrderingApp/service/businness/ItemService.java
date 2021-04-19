package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    /**
     * This method receives category entity.
     * This method returns list of food items belonging to a category.
     */
    /**
     * @param categoryEntity - category entity
     * @return -  List<ItemEntity>
     * @exception - none.
     */
    public List<ItemEntity> getItemsForCategory(CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItemEntities = itemDao.getItemsByCategoryId(categoryEntity);
        List<ItemEntity> itemEntities = new ArrayList<>();

        for (CategoryItemEntity categoryItemEntity: categoryItemEntities) {
            itemEntities.add(getItemByUUID(categoryItemEntity.getItemId().getUuid()));
        }
        return itemEntities;
    }

    /**
     * This method receives item Uuid.
     * This method is used to return an item based on item uuid.
     */
    /**
     * @param itemId - item uuid
     * @return -  ItemEntity object
     * @exception - none.
     */
    private ItemEntity getItemByUUID(String itemId) {
        return itemDao.getItemById(itemId);
    }

    /**
     * This method receives restaurantEntity and categoryEntity.
     * This method fetches the food items belonging to a particular category and particular restaurant.
     */
    /**
     * @param restaurantEntity - restaurantEntity
     * @param categoryEntity - categoryEntity
     * @return -  List<ItemEntity>
     * @exception - none.
     */
    public List<ItemEntity> getItemsByRestaurantAndCategory(RestaurantEntity restaurantEntity, CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItemEntities = itemDao.getItemsByCategoryId(categoryEntity);
        List<RestaurantItemEntity> restaurantItemEntities = itemDao.getItemsByRestaurant(restaurantEntity);

        List<ItemEntity> itemEntities = new ArrayList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if (restaurantItemEntity.getItemId().getUuid().equals(categoryItemEntity.getItemId().getUuid())) {
                    itemEntities.add(restaurantItemEntity.getItemId());
                }
            });
        });

        return itemEntities;
    }

    /**
     * This method receives restaurantEntity.
     * This method fetches top 5 popular items in a restaurant.
     */
    /**
     * @param restaurantEntity - restaurantEntity
     * @return -  List<ItemEntity>
     * @exception - none.
     */
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List<ItemEntity> itemEntities = new ArrayList<>();
        return itemEntities;
    }
}
