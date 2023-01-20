package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandUpsertForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class BrandController {

    @Autowired
    private BrandDto dto;

//    TODO: make limit for batch addition
    @ApiOperation(value = "Adds a list of Brands")
    @RequestMapping(path = "/add-brands", method = RequestMethod.POST)
    public void add(@RequestBody List<BrandUpsertForm> forms) throws ApiException {
        dto.add(forms);
    }

    @ApiOperation(value = "Gets a Brand by Id")
    @RequestMapping(path = "/brands/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

//    TODO:use post request here
    @ApiOperation(value = "Gets a Brand by name, category")
    @RequestMapping(path = "/brands-by-name-category/", method = RequestMethod.GET)
    public BrandData getByNameCategory(@RequestBody BrandUpsertForm brandUpsertForm) {
        return dto.get(brandUpsertForm);
    }

    @ApiOperation(value = "Gets list of all Brands")
    @RequestMapping(path = "/brands", method = RequestMethod.GET)
    public List<BrandData> getAll() {
        return dto.getAll();
    }

    @ApiOperation(value = "Gets Categories by Brand")
    @RequestMapping(path = "/categories-by-brand/{name}", method = RequestMethod.GET)
    public List<String> getCategories(@PathVariable String name) {
        return dto.get(name);
    }

    @ApiOperation(value = "Gets Distinct Brand names")
    @RequestMapping(path = "/distinct-brand", method = RequestMethod.GET)
    public List<String> getDistinctBrands() {
        return dto.getDistinctBrands();
    }

    @ApiOperation(value = "Updates a Brand")
    @RequestMapping(path = "/brands/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody BrandUpsertForm brandUpsertForm) throws ApiException {
        dto.update(id,brandUpsertForm);
    }
}
