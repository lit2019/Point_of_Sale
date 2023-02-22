package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.OrderFilterForm;
import com.increff.pos.model.OrderStatus;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.increff.pos.utils.TestObjectUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrderApiTest extends AbstractUnitTest {
    @Autowired
    private OrderApi orderApi;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private OrderItemDao orderItemDao;

    private List<InventoryPojo> inventoryPojos;


    @Before
    public void setup() {
        List<ProductPojo> productPojos = getNewProductPojoList();
        productPojos.forEach(productDao::insert);

        inventoryPojos = getNewInventoryPojoList(productPojos);
        inventoryPojos.forEach(inventoryDao::insert);
    }

    @Test
    public void testAdd() throws ApiException {
        List<OrderItemPojo> orderItemPojos = new ArrayList<>();
        orderItemPojos.add(getNewOrderItemPojo(null, inventoryPojos.get(0).getProductId(), 100, 100.0));
        orderItemPojos.add(getNewOrderItemPojo(null, inventoryPojos.get(1).getProductId(), 100, 100.0));
        orderItemPojos.add(getNewOrderItemPojo(null, inventoryPojos.get(2).getProductId(), 100, 100.0));

        OrderPojo orderPojo = new OrderPojo();
        orderApi.add(orderPojo, orderItemPojos);

        assertEquals(1, orderDao.selectAll().size());
        assertEquals(3, orderItemDao.selectAll().size());
    }

    @Test
    public void testGet() {
        OrderPojo orderPojo = new OrderPojo();
        orderDao.insert(orderPojo);
        assertEquals(orderPojo.getId(), orderApi.get(orderPojo.getId()).getId());
    }

    @Test
    public void testGetOrderItemsByOrderIds() {
        OrderPojo orderPojo1 = new OrderPojo();
        orderDao.insert(orderPojo1);

        OrderPojo orderPojo2 = new OrderPojo();
        orderDao.insert(orderPojo2);

        List<OrderItemPojo> orderItemPojos = new ArrayList<>();
        orderItemPojos.add(getNewOrderItemPojo(orderPojo1.getId(), inventoryPojos.get(0).getProductId(), 100, 100.0));
        orderItemPojos.add(getNewOrderItemPojo(orderPojo2.getId(), inventoryPojos.get(1).getProductId(), 100, 100.0));
        orderItemPojos.add(getNewOrderItemPojo(orderPojo1.getId(), inventoryPojos.get(2).getProductId(), 100, 100.0));
        orderItemPojos.forEach(orderItemDao::insert);

        assertEquals(2, orderApi.getOrderItemsByOrderIds(Collections.singletonList(orderPojo1.getId())).size());
        assertEquals(1, orderApi.getOrderItemsByOrderIds(Collections.singletonList(orderPojo2.getId())).size());
        assertEquals(3, orderApi.getOrderItemsByOrderIds(Arrays.asList(new Integer[]{orderPojo1.getId(), orderPojo2.getId()})).size());
    }

    @Test
    public void testGetByFilter() throws ApiException {
        OrderFilterForm filterForm = new OrderFilterForm();
        filterForm.setStartDate(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC")));
        filterForm.setEndDate(filterForm.getStartDate().plusDays(200));
        try {
            orderApi.getByFilter(filterForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("start date and end date cannot be more than 100 days apart", e.getMessage());
        }

        filterForm.setEndDate(filterForm.getStartDate().plusDays(1));
        orderDao.insert(new OrderPojo());
        orderDao.insert(new OrderPojo());
        assertEquals(2, orderApi.getByFilter(filterForm).size());

    }

    @Test
    public void testSetStatus() {
        OrderPojo orderPojo = new OrderPojo();
        orderDao.insert(orderPojo);

        orderApi.setStatus(orderPojo.getId(), OrderStatus.INVOICED);
        assertEquals(OrderStatus.INVOICED, orderDao.select(orderPojo.getId()).getOrderStatus());
    }


}
