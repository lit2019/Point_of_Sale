package com.increff.pos.dao;

import com.increff.pos.entity.OrderPojo;
import com.increff.pos.model.OrderStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {
    private static final String SELECT_BY_DATE = "select p from OrderPojo p where (p.createdAt between :startDate AND :endDate)" +
            " and (p.orderStatus=:orderStatus or :orderStatus is null) order by p.createdAt desc";

    public List<OrderPojo> selectByFilter(ZonedDateTime startDate, ZonedDateTime endDate, OrderStatus orderStatus) {
        TypedQuery<OrderPojo> query = createQuery(SELECT_BY_DATE);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("orderStatus", orderStatus);
        return getResultList(query);
    }

    public List<OrderPojo> selectByFilter(ZonedDateTime startDate, ZonedDateTime endDate, OrderStatus orderStatus, Integer pageNo, Integer pageSize) {

        TypedQuery<OrderPojo> query = createQuery(SELECT_BY_DATE)
                .setMaxResults(pageSize)
                .setFirstResult((pageNo - 1) * pageSize);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("orderStatus", orderStatus);
        return getResultList(query);
    }
}
