package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.UtilityProvider;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    UtilityProvider utilityProvider;


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
    public ItemEntity getItemByUUID(String itemId) {
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
        List<OrderEntity> orderEntities = orderDao.getOrdersByRestaurant(restaurantEntity);
        List<ItemEntity> itemEntities = new ArrayList<>();

        orderEntities.forEach(ordersEntity -> {
            List <OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> {
                itemEntities.add(orderItemEntity.getItem());
            });
        });

        Map<String,Integer> itemCountMap = new HashMap<String,Integer>();
        itemEntities.forEach(itemEntity -> {
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(),(count == null) ? 1 : count+1);
        });

        Map<String,Integer> sortedItemCountMap = utilityProvider.sortMapByValues(itemCountMap);

        //Get top 5 items
        List<ItemEntity> topItemEntites = new ArrayList<>();
        Integer count = 0;
        for(Map.Entry<String,Integer> item:sortedItemCountMap.entrySet()){
            if(count < 5) {
                topItemEntites.add(itemDao.getItemById(item.getKey()));
                count = count+1;
            }else{
                break;
            }
        }

        return topItemEntites;
    }
}
