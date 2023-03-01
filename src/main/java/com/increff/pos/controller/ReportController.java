package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.DailySalesDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api
@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private DailySalesDto dailySalesDto;

    @ApiOperation(value = "gets sales report by start date, end date, brand name and category")
    @RequestMapping(path = "/sales", method = RequestMethod.POST)
    public List<SalesData> getSales(@RequestBody SalesFilterForm filterForm) throws ApiException {
        return orderDto.getSalesReport(filterForm);
    }

    @ApiOperation(value = "gets Inventory report")
    @RequestMapping(path = "/inventory", method = RequestMethod.POST)
    public List<InventoryReportData> getInventoryReport(@RequestBody InventorySearchForm filterForm) throws ApiException {
        return inventoryDto.getInventoryReport(filterForm);
    }

    @ApiOperation(value = "gets Daily Sales by date range")
    @RequestMapping(path = "/dailysales", method = RequestMethod.POST)
    public List<DailySalesData> getByDate(@RequestBody DailySalesFilterForm filterForm) throws ApiException {
        return dailySalesDto.getByFilter(filterForm);
    }
}
