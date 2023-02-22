package com.increff.pos.jobs;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.DailySalesApi;
import com.increff.pos.api.InvoiceApi;
import com.increff.pos.api.OrderApi;
import com.increff.pos.entity.DailySalesPojo;
import com.increff.pos.entity.InvoicePojo;
import com.increff.pos.entity.OrderItemPojo;
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
    private InvoiceApi invoiceApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private DailySalesApi dailySalesApi;

    @Scheduled(initialDelay = 60000, fixedDelayString = "${app.job.delay:60000}")
    public void run() throws ApiException {
//        todo ZoneId must be utc
        ZonedDateTime today = ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime tomorrow = today.plusDays(1);
        List<InvoicePojo> invoicePojos = invoiceApi.getByDate(today, tomorrow);

        ArrayList<Integer> orderIds = new ArrayList<>();
        invoicePojos.forEach(pojo -> {
            orderIds.add(pojo.getOrderId());
        });

        List<OrderItemPojo> orderItems = orderApi.getOrderItemsByOrderIds(orderIds);

        DailySalesPojo dailySalesPojo = new DailySalesPojo();
        dailySalesPojo.setInvoicedOrdersCount(invoicePojos.size());

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