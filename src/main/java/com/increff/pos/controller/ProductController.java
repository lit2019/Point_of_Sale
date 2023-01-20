package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductUpsertForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "Gets list of all Products")
    @RequestMapping(path = "/products", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        return dto.get();
    }

    @ApiOperation(value = "Adds a list of Products")
    @RequestMapping(path = "/add-products", method = RequestMethod.POST)
    public void add(@RequestBody List<ProductUpsertForm> forms) throws ApiException {
        dto.add(forms);
    }

    @ApiOperation(value = "Gets a Product by ID")
    @RequestMapping(path = "/products/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Updates a Product")
    @RequestMapping(path = "/products", method = RequestMethod.PUT)
    public void update(@RequestBody ProductUpsertForm form) throws ApiException {
        dto.update(form);
    }
}