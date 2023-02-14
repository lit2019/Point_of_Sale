package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Creates an order")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void add(@RequestBody OrderForm orderForm) throws ApiException, IOException {
        dto.add(orderForm);
    }

    //TODO make it id only
    @ApiOperation(value = "gets list of order items by orderId")
    @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrderItems(orderId);
    }

    @ApiOperation(value = "gets all orders by date range")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<OrderData> getByDate(@RequestBody OrderFilterForm orderFilterForm) throws ApiException {
        return dto.getOrdersByFilter(orderFilterForm);
    }

    @ApiOperation(value = "gets invoice with orderId")
    @RequestMapping(path = "/invoice/{orderId}", method = RequestMethod.GET)
    public String getInvoice(@PathVariable Integer orderId) throws ApiException, IOException {
        return dto.getInvoice(orderId);
    }

    @ApiOperation(value = "gets sales report by start date, end date, brand name and category")
    @RequestMapping(path = "/sales/report", method = RequestMethod.POST)
    public ArrayList<SalesData> getSales(@RequestBody SalesFilterForm filterForm) throws ApiException {
        return dto.getSalesReport(filterForm);
    }
}
