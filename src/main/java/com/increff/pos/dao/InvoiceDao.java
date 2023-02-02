package com.increff.pos.dao;

import com.increff.pos.entity.InvoicePojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class InvoiceDao extends AbstractDao<InvoicePojo> {
    private static final String SELECT_BY_DATE = "select p from InvoicePojo p where (p.createdAt between :startDate AND :endDate)";

    public List<InvoicePojo> selectByDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        TypedQuery<InvoicePojo> query = getQuery(SELECT_BY_DATE);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return getResultList(query);
    }
}
