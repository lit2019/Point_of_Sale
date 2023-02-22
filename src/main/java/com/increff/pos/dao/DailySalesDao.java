package com.increff.pos.dao;

import com.increff.pos.entity.DailySalesPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class DailySalesDao extends AbstractDao<DailySalesPojo> {
    private static final String SELECT_BY_DATE = "select p from DailySalesPojo p where date=:date";
    private static final String SELECT_BY_DATE_RANGE = "select p from DailySalesPojo p where (p.createdAt between :startDate AND :endDate)";

    public List<DailySalesPojo> selectByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        TypedQuery<DailySalesPojo> query = createQuery(SELECT_BY_DATE_RANGE);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return getResultList(query);
    }

    public DailySalesPojo selectByDate(ZonedDateTime date) {
        TypedQuery<DailySalesPojo> query = createQuery(SELECT_BY_DATE);
        query.setParameter("date", date);
        return getSingleResult(query);
    }
}
