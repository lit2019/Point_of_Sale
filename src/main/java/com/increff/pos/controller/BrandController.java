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
public class BrandController {

    @Autowired
    private BrandDto dto;

//    TODO: make limit for batch addition

    @ApiOperation(value = "Adds an Brand")
    @RequestMapping(path = "/api/brands", method = RequestMethod.POST)
    public void add(@RequestBody BrandForm form) throws ApiException {
        dto.add(form);
    }

    @ApiOperation(value = "Adds a list of Brands")
    @RequestMapping(path = "/api/add-brands", method = RequestMethod.POST)
    public void add(@RequestBody List<BrandForm> forms) throws ApiException {
        dto.add(forms);
    }

    @ApiOperation(value = "Gets a Brand by Id")
    @RequestMapping(path = "/api/brands/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets a Brand by name, category")
    @RequestMapping(path = "/api/brands/{name}/{category}", method = RequestMethod.GET)
    public BrandData getByNameCategory(@PathVariable String name,@PathVariable String category) {
        return dto.get(name,category);
    }

    @ApiOperation(value = "Gets list of all Brands")
    @RequestMapping(path = "/api/brands", method = RequestMethod.GET)
    public List<BrandData> getAll() {
        return dto.getAll();
    }

    @ApiOperation(value = "Gets Categories by Brand")
    @RequestMapping(path = "/api/categories-by-brand/{name}", method = RequestMethod.GET)
    public List<BrandData> getCategories(@PathVariable String name) {
        return dto.get(name);
    }

    @ApiOperation(value = "Updates a Brand")
    @RequestMapping(path = "/api/brands/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody BrandForm form) throws ApiException {
        dto.update(id,form);
    }
}
