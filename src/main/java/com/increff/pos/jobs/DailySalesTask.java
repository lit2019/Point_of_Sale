package com.increff.pos.jobs;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.DailySalesApi;
import com.increff.pos.api.OrderApi;
import com.increff.pos.entity.DailySalesPojo;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DailySalesTask {

    @Autowired
    private OrderApi orderApi;
    @Autowired
    private DailySalesApi dailySalesApi;

    @Scheduled(initialDelay = 60000, fixedDelayString = "${app.job.delay:60000}")
    public void run() throws ApiException {
        ZonedDateTime today = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime tomorrow = today.plusDays(1);
        List<OrderPojo> orderPojos = orderApi.getByFilter(today, tomorrow, OrderStatus.INVOICED);

        ArrayList<Integer> orderIds = new ArrayList<>();
        orderPojos.forEach(pojo -> orderIds.add(pojo.getId()));

        List<OrderItemPojo> orderItems = orderApi.getOrderItemsByOrderIds(orderIds);

        DailySalesPojo dailySalesPojo = new DailySalesPojo();
        dailySalesPojo.setInvoicedOrdersCount(orderPojos.size());

        Integer invoicedItemsCount = 0;
        Double totalRevenue = 0.0;
        for (OrderItemPojo orderItem : orderItems) {
            invoicedItemsCount += orderItem.getQuantity();
            totalRevenue += orderItem.getSellingPrice() * orderItem.getQuantity();
        }
        dailySalesPojo.setTotalRevenue(totalRevenue);
        dailySalesPojo.setInvoicedItemsCount(invoicedItemsCount);
        dailySalesPojo.setDate(today);

        dailySalesApi.upsert(dailySalesPojo);
    }
}