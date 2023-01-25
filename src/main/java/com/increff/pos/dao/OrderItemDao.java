package com.increff.pos.dao;

import com.increff.pos.entity.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao<OrderItemPojo> {
    private static final String SELECT_BY_ORDER_ID = "select p from OrderItemPojo p where (p.orderId=:orderId)";

    public List<OrderItemPojo> getByOrderId(Integer orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ORDER_ID);
        query.setParameter("orderId", orderId);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<OrderItemPojo>();
        }
    }
}
