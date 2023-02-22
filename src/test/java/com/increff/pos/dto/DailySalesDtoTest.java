package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.entity.DailySalesPojo;
import com.increff.pos.model.DailySalesData;
import com.increff.pos.model.DailySalesFilterForm;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.utils.TestObjectUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class DailySalesDtoTest extends AbstractUnitTest {
    @Autowired
    private DailySalesDto dailySalesDto;

    @Autowired
    private DailySalesDao dailySalesDao;

    @Test
    public void testGetByFilterInvalidForm() {
        DailySalesFilterForm filterForm = new DailySalesFilterForm();
        filterForm.setStartDate(null);
        filterForm.setEndDate(ZonedDateTime.now());
        try {
            dailySalesDto.getByFilter(filterForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("start date cannot be null", e.getMessage());
        }

        filterForm.setStartDate(ZonedDateTime.now());
        filterForm.setEndDate(null);
        try {
            dailySalesDto.getByFilter(filterForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("end date cannot be null", e.getMessage());
        }
    }

    @Test
    public void testGetByFilter() throws ApiException {
        ZonedDateTime today = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        DailySalesPojo dailySalesPojo = TestObjectUtils.getNewDailySalesPojo(100.0, 10, 3, today);
        dailySalesDao.insert(dailySalesPojo);

        DailySalesFilterForm filterForm = new DailySalesFilterForm();
        filterForm.setStartDate(today);
        filterForm.setEndDate(today.plusDays(1));
        List<DailySalesData> dataList = dailySalesDto.getByFilter(filterForm);
        assertNotNull(dataList);
        assertEquals(1, dataList.size());
    }


}
