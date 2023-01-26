package com.increff.pos.api;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.model.OrderItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderItemApi {
    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private InventoryApi inventoryApi;

    public void add(OrderItemPojo orderItemPojo) throws ApiException {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(orderItemPojo.getProductId());
        inventoryPojo.setQuantity(inventoryApi.get(orderItemPojo.getProductId()).getQuantity() - orderItemPojo.getQuantity());
        inventoryApi.update(orderItemPojo.getProductId(), inventoryPojo);
        orderItemDao.insert(orderItemPojo);
    }

    public List<OrderItemPojo> get(Integer orderId) {
        return orderItemDao.getByOrderId(orderId);
    }

    public void update(Integer id, OrderItemForm orderItemForm) {
    }
}
