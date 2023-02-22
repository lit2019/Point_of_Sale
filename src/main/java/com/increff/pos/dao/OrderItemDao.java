package com.increff.pos.dao;

import com.increff.pos.entity.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao<OrderItemPojo> {
    private static final String SELECT_BY_ORDER_ID = "select p from OrderItemPojo p where p.orderId IN :orderIds";

    public List<OrderItemPojo> getByOrderIds(List<Integer> orderIds) {
        TypedQuery<OrderItemPojo> query = createQuery(SELECT_BY_ORDER_ID);
        query.setParameter("orderIds", orderIds);
        return getResultList(query);
    }
}
