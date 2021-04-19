package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    /**
     * This method is used to get all the restaurant categories.
     */
    /**
     * @return -  ResponseEntity object
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryService.getAllCategories();

        List<CategoryListResponse> categoryListResponses = new ArrayList<>();

        for (CategoryEntity categoryEntity: categoryEntities) {
            categoryListResponses.add(new CategoryListResponse()
                                            .id(UUID.fromString(categoryEntity.getUuid()))
                                            .categoryName(categoryEntity.getCategoryName()));
        }

        return new ResponseEntity<List<CategoryListResponse>>(categoryListResponses, HttpStatus.OK);
    }

    /**
     * This method receives category Id as path variable.
     * This method is used to get the category as per category uuid and it fetches all the items belonging to a category.
     */
    /**
     * @param categoryId - category UUID
     * @return -  ResponseEntity object
     * @exception - CategoryNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryList> getCategoryById(@PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);

        List<ItemEntity> itemEntities = itemService.getItemsForCategory(categoryEntity);

        List<ItemList> itemLists = new ArrayList<>();

        for (ItemEntity itemEntity: itemEntities) {
            itemLists.add(new ItemList()
                               .id(UUID.fromString(itemEntity.getUuid()))
                               .itemName(itemEntity.getItemName())
                               .price(itemEntity.getPrice())
                               .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().equals("0") ? "VEG" : "NON_VEG")));
        }

        CategoryList categoryList = new CategoryList()
                                        .id(UUID.fromString(categoryEntity.getUuid()))
                                        .categoryName(categoryEntity.getCategoryName())
                                        .itemList(itemLists);

        return new ResponseEntity<CategoryList>(categoryList, HttpStatus.OK);
    }

}
