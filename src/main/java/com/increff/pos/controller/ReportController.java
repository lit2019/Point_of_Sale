package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.InventorySearchForm;
import com.increff.pos.model.SalesData;
import com.increff.pos.model.SalesFilterForm;
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

    @ApiOperation(value = "gets sales report by start date, end date, brand name and category")
    @RequestMapping(path = "/sales", method = RequestMethod.POST)
    public List<SalesData> getSales(@RequestBody SalesFilterForm filterForm) throws ApiException {
        return orderDto.getSalesReport(filterForm);
    }

    //    todo make filter for brand and category
    @ApiOperation(value = "gets Inventory report")
    @RequestMapping(path = "/inventory", method = RequestMethod.POST)
    public List<InventoryReportData> getInventoryReport(@RequestBody InventorySearchForm filterForm) throws ApiException {
        return inventoryDto.getInventoryReport(filterForm);
    }
}
