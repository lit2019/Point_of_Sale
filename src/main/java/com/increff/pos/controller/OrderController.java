package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.entity.InvoicePojo;
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
    public OrderData add(@RequestBody OrderForm orderForm) throws ApiException, IOException {
        return dto.add(orderForm);
    }

    @ApiOperation(value = "gets list of order items by orderId")
    @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrderItems(orderId);
    }

    @ApiOperation(value = "gets all orders")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<OrderData> getAll() throws ApiException {
        return dto.getOrderItems();
    }

    @ApiOperation(value = "adds an order item to the given orderId")
    @RequestMapping(path = "/{orderId}", method = RequestMethod.POST)
    public void addItem(@PathVariable Integer orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        dto.addItem(orderId, orderItemForm);
    }

    @ApiOperation(value = "gets invoice with orderId")
    @RequestMapping(path = "/invoice/{orderId}", method = RequestMethod.GET)
    public InvoicePojo getInvoice(@PathVariable Integer orderId) throws ApiException, IOException {
        return dto.getInvoice(orderId);
    }

    @ApiOperation(value = "gets sales report by start date, end date, brand name and category")
    @RequestMapping(path = "/sales-report", method = RequestMethod.POST)
    public ArrayList<SalesData> getSales(@RequestBody SalesFilterForm filterForm) throws ApiException {
        return dto.getSalesReport(filterForm);
    }
}
