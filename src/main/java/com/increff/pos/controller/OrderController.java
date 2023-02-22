package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderFilterForm;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderItemData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    //TODO make it id only(didn't rename, cause there are 2 ids orderId and orderItemId)
    @ApiOperation(value = "gets list of order items by orderId")
    @RequestMapping(path = "items/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItems(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrderItems(orderId);
    }

    @ApiOperation(value = "gets all orders by date range")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<OrderData> getByFilter(@RequestBody OrderFilterForm orderFilterForm) throws ApiException {
        return dto.getOrdersByFilter(orderFilterForm);
    }

    //TODO: use seperate controller for invoice
    @ApiOperation(value = "gets invoice with orderId")
    @RequestMapping(path = "/invoice/{orderId}", method = RequestMethod.GET)
    public String getInvoice(@PathVariable Integer orderId) throws ApiException, IOException {
        return dto.getInvoice(orderId);
    }

    //TODO make a controller for all the reports

}
