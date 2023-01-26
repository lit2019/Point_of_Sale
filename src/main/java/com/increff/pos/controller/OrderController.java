package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Creates an order")
    @RequestMapping(path = "/orders", method = RequestMethod.POST)
    public void add(@RequestBody OrderForm orderForm) throws ApiException {
        dto.add(orderForm);
    }

    @ApiOperation(value = "gets orderItems by orderId")
    @RequestMapping(path = "/orders/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable Integer orderId) throws ApiException {
        return dto.get(orderId);
    }

    @ApiOperation(value = "gets all orders")
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public List<OrderData> getAll() throws ApiException {
        return dto.get();
    }

    @ApiOperation(value = "adds an order item to the given orderId")
    @RequestMapping(path = "/orders/{orderId}", method = RequestMethod.POST)
    public void addItem(@PathVariable Integer orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        dto.addItem(orderId, orderItemForm);
    }

    @ApiOperation(value = "adds an order item to the given orderId")
    @RequestMapping(path = "/orders/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        dto.updateItem(id, orderItemForm);
    }


}
