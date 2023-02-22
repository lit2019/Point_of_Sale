package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.model.ProductUpdateForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "filter Products")
    @RequestMapping(path = "/filter", method = RequestMethod.POST)
    public List<ProductData> filter(@RequestBody ProductSearchForm searchForm) throws ApiException {
        return dto.filter(searchForm);
    }

    @ApiOperation(value = "Adds a list of Products")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void add(@RequestBody List<ProductForm> forms) throws ApiException {
        dto.add(forms);
    }

    @ApiOperation(value = "Gets a Product by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Updates a Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.PATCH)
    public void update(@PathVariable Integer id, @RequestBody ProductUpdateForm form) throws ApiException {
        dto.update(id, form);
    }
}