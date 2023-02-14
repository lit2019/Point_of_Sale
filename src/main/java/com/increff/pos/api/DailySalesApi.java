package com.increff.pos.api;


import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.entity.DailySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = Exception.class)
public class DailySalesApi extends AbstractApi {
    @Autowired
    private DailySalesDao dao;

    public void upsert(DailySalesPojo dailySalesPojo) throws ApiException {
        validate(dailySalesPojo);
        DailySalesPojo existingPojo = dao.selectByDate(dailySalesPojo.getDate());
        if (Objects.isNull(existingPojo)) {
            dao.insert(dailySalesPojo);
        } else {
            existingPojo.setInvoicedItemsCount(dailySalesPojo.getInvoicedItemsCount());
            existingPojo.setInvoicedOrdersCount(dailySalesPojo.getInvoicedOrdersCount());
            existingPojo.setTotalRevenue(dailySalesPojo.getTotalRevenue());
        }
    }

    public List<DailySalesPojo> get(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        if (ChronoUnit.DAYS.between(startDate, endDate) > 100) {
            throw new ApiException("start date and end date cannot be more than 100 days apart");
        }
        return dao.selectByDateRange(startDate, endDate);
    }

    private void validate(DailySalesPojo dailySalesPojo) throws ApiException {
        checkNull(dailySalesPojo.getDate(), "date cannot be null");
    }
}