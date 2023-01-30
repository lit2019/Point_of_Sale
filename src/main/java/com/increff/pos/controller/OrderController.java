package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.entity.InvoicePojo;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Creates an order")
    @RequestMapping(path = "/orders", method = RequestMethod.POST)
    public void add(@RequestBody OrderForm orderForm) throws ApiException, IOException {
        dto.add(orderForm);
    }

    @ApiOperation(value = "gets list of order items by orderId")
    @RequestMapping(path = "/orders/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrderItems(orderId);
    }

    @ApiOperation(value = "gets all orders")
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public List<OrderData> getAll() throws ApiException {
        return dto.getOrderItems();
    }

    @ApiOperation(value = "adds an order item to the given orderId")
    @RequestMapping(path = "/orders/{orderId}", method = RequestMethod.POST)
    public void addItem(@PathVariable Integer orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        dto.addItem(orderId, orderItemForm);
    }

    @ApiOperation(value = "edits an order item with orderItemId")
    @RequestMapping(path = "/order-items/{orderItemId}", method = RequestMethod.PUT)
    public void updateItem(@PathVariable Integer orderItemId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        dto.updateItem(orderItemId, orderItemForm);
    }

    @ApiOperation(value = "gets invoice with orderId")
    @RequestMapping(path = "/order-invoice/{orderId}", method = RequestMethod.GET)
    public InvoicePojo getInvoice(@PathVariable Integer orderId) throws ApiException {
        return dto.getInvoice(orderId);
    }


}
