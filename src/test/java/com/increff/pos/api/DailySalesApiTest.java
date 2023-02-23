package com.increff.pos.api;

import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.entity.DailySalesPojo;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.util.TestObjectUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class DailySalesApiTest extends AbstractUnitTest {

    @Autowired
    private DailySalesApi dailySalesApi;
    @Autowired
    private DailySalesDao dailySalesDao;

    @Test
    public void testUpsertInvalidDate() {
        DailySalesPojo dailySalesPojo = TestObjectUtils.getNewDailySalesPojo(100.0, 10, 3, null);
        try {
            dailySalesApi.upsert(dailySalesPojo);
        } catch (ApiException e) {
            assertEquals("date cannot be null", e.getMessage());
        }
    }

    @Test
    public void testUpsert() throws ApiException {
        ZonedDateTime today = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        DailySalesPojo dailySalesPojo = TestObjectUtils.getNewDailySalesPojo(100.0, 10, 3, today);
        dailySalesApi.upsert(dailySalesPojo);

        DailySalesPojo dailySalesPojo2 = dailySalesDao.selectByDate(today);
        assertNotNull(dailySalesPojo2);
        assertEquals(dailySalesPojo.getId(), dailySalesPojo2.getId());
        assertEquals(dailySalesPojo.getTotalRevenue(), dailySalesPojo2.getTotalRevenue());
        assertEquals(dailySalesPojo.getInvoicedOrdersCount(), dailySalesPojo2.getInvoicedOrdersCount());
        assertEquals(dailySalesPojo.getInvoicedItemsCount(), dailySalesPojo2.getInvoicedItemsCount());
    }

    @Test
    public void testGetByDateRange() throws ApiException {
        ZonedDateTime today = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        DailySalesPojo dailySalesPojo = TestObjectUtils.getNewDailySalesPojo(100.0, 10, 3, today);
        dailySalesDao.insert(dailySalesPojo);

        try {
            dailySalesApi.getByDateRange(today, today.plusDays(102));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("start date and end date cannot be more than 100 days apart", e.getMessage());
        }
        List<DailySalesPojo> pojos = dailySalesApi.getByDateRange(today, today.plusDays(1));
        assertNotNull(pojos);
        assertEquals(1, pojos.size());
    }
}
