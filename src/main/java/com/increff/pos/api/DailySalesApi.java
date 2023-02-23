package com.increff.pos.api;


import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.entity.DailySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.maxDateRange}")
    private Integer maxDateRange;

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

    public List<DailySalesPojo> getByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        endDate = endDate.plusDays(1);
        if (!startDate.isBefore(endDate)) throw new ApiException("Start Date must be Before End Date");
        if (ChronoUnit.DAYS.between(startDate, endDate) > maxDateRange)
            throw new ApiException(String.format("start date and end date cannot be more than %s days apart", maxDateRange));

        return dao.selectByDateRange(startDate, endDate);
    }

    private void validate(DailySalesPojo dailySalesPojo) throws ApiException {
        checkNull(dailySalesPojo.getDate(), "date cannot be null");
    }
}