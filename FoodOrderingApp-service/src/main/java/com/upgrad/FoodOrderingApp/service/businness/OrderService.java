package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

;

@Service
public class OrderService {

    @Autowired OrderDao orderDao;

    @Autowired CouponDao couponDao;

    @Autowired OrderItemDao orderItemDao;

    @Autowired CustomerDao customerDao;

    /**
     * This method receives couponName.
     * This method is to fetch coupons based on couponName.
     */
    /**
     * @param couponName - coupon name
     * @return -  CouponEntity
     * @exception - none.
     */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if (couponName == null || couponName == "") { // Checking if Coupon Name is Null
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        CouponEntity couponEntity = couponDao.getCouponByCouponName(couponName);
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        return couponEntity;
    }

    /**
     * This method receives coupon uuid.
     * This method is to fetch coupons based on uuid.
     */
    /**
     * @param couponUuid - coupon uuid
     * @return -  CouponEntity
     * @exception - none.
     */
    public CouponEntity getCouponByCouponId(String couponUuid) throws CouponNotFoundException {

        CouponEntity couponEntity = couponDao.getCouponByCouponId(couponUuid);
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        return couponEntity;
    }

    /**
     * This method receives OrderEntity.
     * This method is to save orders in db.
     */
    /**
     * @param ordersEntity - OrderEntity
     * @return -  OrderEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(OrderEntity ordersEntity) {

        OrderEntity savedOrderEntity = orderDao.saveOrder(ordersEntity);
        return savedOrderEntity;
    }

    /**
     * This method receives OrderItemEntity.
     * This method is to save order item in db.
     */
    /**
     * @param orderItemEntity - orderItemEntity
     * @return -  OrderItemEntity
     * @exception - none.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {

        OrderItemEntity savedOrderItemEntity = orderItemDao.saveOrderItem(orderItemEntity);
        return savedOrderItemEntity;
    }

    /**
     * This method receives customerUuid.
     * This method is to fetch orders by customers.
     */
    /**
     * @param customerUuid - customer uuid
     * @return -  List<OrderEntity>
     * @exception - none.
     */
    public List<OrderEntity> getOrdersByCustomers(String customerUuid) {

        CustomerEntity customerEntity = customerDao.getCustomerById(customerUuid);

        List<OrderEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }

    /**
     * This method receives order entity.
     * This method is to fetch order items based on orders.
     */
    /**
     * @param ordersEntity - OrderEntity
     * @return -  List<OrderItemEntity>
     * @exception - none.
     */
    public List<OrderItemEntity> getOrderItemsByOrder(OrderEntity ordersEntity) {

        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(ordersEntity);
        return orderItemEntities;
    }
}
