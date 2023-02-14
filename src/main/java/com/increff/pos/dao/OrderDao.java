package com.increff.pos.dao;

import com.increff.pos.entity.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {
    private static final String SELECT_BY_DATE = "select p from OrderPojo p where (p.createdAt between :startDate AND :endDate)";

    public List<OrderPojo> selectByDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        TypedQuery<OrderPojo> query = createQuery(SELECT_BY_DATE);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return getResultList(query);
    }
}
