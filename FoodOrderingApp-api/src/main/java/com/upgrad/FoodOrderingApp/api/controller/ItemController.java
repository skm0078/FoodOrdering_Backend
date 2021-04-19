package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RestaurantService restaurantService;

    /**
     * This method receives restaurant Id as path variable.
     * This method is used to get top 5 items by popularity.
     */
    /**
     * @param restaurantId - restaurant UUID
     * @return -  ResponseEntity object
     * @exception - RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ItemList>> getTopFiveItemsByPopularity(@PathVariable("restaurant_id") final String restaurantId) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantByUUID(restaurantId);

        List<ItemEntity> itemEntities = itemService.getItemsByPopularity(restaurantEntity);

        List<ItemList> itemLists = new ArrayList<>();

        for (ItemEntity itemEntity: itemEntities) {
            itemLists.add(new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getItemName())
                    .price(itemEntity.getPrice())
                    .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().equals("0") ? "VEG" : "NON_VEG")));
        }

        return new ResponseEntity<List<ItemList>>(itemLists, HttpStatus.OK);
    }
}
