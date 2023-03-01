package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "Adds a list of Brands")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void add(@RequestBody List<BrandForm> forms) throws ApiException {
        dto.add(forms);
    }

    @ApiOperation(value = "Gets a Brand by Id")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of Brands with name/category")
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<BrandData> getByBrandNameCategory(@RequestBody BrandForm brandForm) {
        return dto.get(brandForm);
    }


    @ApiOperation(value = "Gets Distinct Brand names")
    @RequestMapping(path = "/distinct", method = RequestMethod.GET)
    public List<String> getDistinctBrands() {
        return dto.getDistinctBrands();
    }

    @ApiOperation(value = "Updates a Brand")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody BrandForm brandForm) throws ApiException {
        dto.update(id, brandForm);
    }
}
