package com.increff.pos.jobs;

import com.increff.pos.api.ApiException;
import com.increff.pos.dao.DailySalesDao;
import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.entity.DailySalesPojo;
import com.increff.pos.entity.InvoicePojo;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.utils.TestObjectUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class DailySalesTaskTest extends AbstractUnitTest {
    @Autowired
    private DailySalesTask task;

    @Autowired
    private DailySalesDao dailySalesDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private OrderItemDao orderItemDao;

    @Test
    public void testRun() throws ApiException {
        ZonedDateTime today = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC"));

        task.run();
        DailySalesPojo pojo1 = dailySalesDao.selectByDate(today);
        assertNotNull(pojo1);
        Integer count1 = pojo1.getInvoicedOrdersCount();
        Double revenue1 = pojo1.getTotalRevenue();
        Integer version1 = pojo1.getVersion();

        OrderPojo orderPojo = new OrderPojo();
        orderDao.insert(orderPojo);
        List<OrderItemPojo> orderItemPojos = TestObjectUtils.getNewOrderItemPojoList(orderPojo.getId());
        orderItemPojos.forEach(orderItemDao::insert);
        InvoicePojo invoicePojo = TestObjectUtils.getNewInvoicePojo(orderPojo.getId());
        invoiceDao.insert(invoicePojo);

        task.run();
        DailySalesPojo pojo2 = dailySalesDao.selectByDate(today);
        assertNotNull(pojo2);
        Integer count2 = pojo2.getInvoicedOrdersCount();
        Double revenue2 = pojo2.getTotalRevenue();
        Integer version2 = pojo2.getVersion();

        assertNotEquals(count1, count2);
        assertNotEquals(revenue1, revenue2);
        assertNotEquals(version1, version2);
    }
}
