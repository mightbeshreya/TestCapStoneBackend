package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderBusinessService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrdersEntity> getOrdersByCustomers(String accessToken,String customerUuid){

        CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerUuid);

        List<OrdersEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }

    public List<OrderItemEntity> getOrderItemsByOrder(OrdersEntity ordersEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(ordersEntity);
        return orderItemEntities;
    }
    }

