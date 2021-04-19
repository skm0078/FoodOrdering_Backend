package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

// This Class is created to access DB with respect to Order entity

@Repository
public class OrderDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     * Presist order in db
     *
     * @param ordersEntity: OrderEntity
     * @return OrderEntity
     */
    public OrderEntity saveOrder(OrderEntity ordersEntity) {
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }

    /**
     * Fetch orders belonging to a customer
     *
     * @param customerEntity: CustomerEntity
     * @return List<OrderEntity>
     */
    public List<OrderEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        try {
            List<OrderEntity> ordersEntities =
                    entityManager
                            .createNamedQuery("getOrdersByCustomers", OrderEntity.class)
                            .setParameter("customer", customerEntity)
                            .getResultList();
            return ordersEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch order by restaurants
     *
     * @param restaurantEntity: RestaurantEntity
     * @return List<OrderEntity>
     */
    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<OrderEntity> ordersEntities =
                    entityManager
                            .createNamedQuery("getOrdersByRestaurant", OrderEntity.class)
                            .setParameter("restaurant", restaurantEntity)
                            .getResultList();
            return ordersEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Fetch order by address
     *
     * @param addressEntity: AddressEntity
     * @return List<OrderEntity>
     */
    public List<OrderEntity> getOrdersByAddress(AddressEntity addressEntity) {
        try {
            List<OrderEntity> ordersEntities =
                    entityManager
                            .createNamedQuery("getOrdersByAddress", OrderEntity.class)
                            .setParameter("address", addressEntity)
                            .getResultList();
            return ordersEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
