package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.entity.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderApi {
    @Autowired
    private OrderDao dao;

    public void add(OrderPojo orderPojo) {
        dao.insert(orderPojo);
    }

    public OrderPojo get(Integer orderId) {
        return dao.select(orderId);
    }

    public List<OrderPojo> get() {
        return dao.selectAll();
    }
}
