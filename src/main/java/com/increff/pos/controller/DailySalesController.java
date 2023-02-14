package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.DailySalesDto;
import com.increff.pos.model.DailySalesData;
import com.increff.pos.model.DailySalesFilterForm;
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
@RequestMapping("/api/dailysales")
public class DailySalesController {
    @Autowired
    private DailySalesDto dto;

    @ApiOperation(value = "gets Daily Sales by date range")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public List<DailySalesData> getByDate(@RequestBody DailySalesFilterForm filterForm) throws ApiException {
        return dto.getByFilter(filterForm);
    }
}
