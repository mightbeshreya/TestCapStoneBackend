package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationServiceNotRegisteredException;
import java.util.HashMap;
import java.util.LinkedList;
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

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) throws RestaurantNotFoundException {

        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        List<OrdersEntity> ordersEntities = orderDao.getOrderByRestaurant(restaurantEntity);

        List<ItemEntity> itemEntities = new LinkedList<>();

        ordersEntities.forEach(ordersEntity -> {
            List<OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> {
                itemEntities.add(orderItemEntity.getItem());
            });
        });
        Map<String, Integer> itemCountMap = new HashMap<>();
        itemEntities.forEach(itemEntity -> {
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
        });
        Map<String, Integer> sortedItemCountMap = itemDao.sortMapByValues(itemCountMap);

        List<ItemEntity> sortedItemEntites = new LinkedList<>();
        Integer count = 0;
        for (Map.Entry<String, Integer> item : sortedItemCountMap.entrySet()) {
            if (count < 5) {
                sortedItemEntites.add(itemDao.getItemByUuid(item.getKey()));
                count = count + 1;
            } else {
                break;
            }
        }

        return sortedItemEntites;
    }

}
