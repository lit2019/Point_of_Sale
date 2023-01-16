package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.ProductService;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class ProductController {

    @Autowired
    private ProductDto dto;

    @Autowired
    private ProductService service;


    @ApiOperation(value = "Gets list of all Products")
    @RequestMapping(path = "/api/products", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        return service.get();
    }
    @ApiOperation(value = "Adds a Product")
    @RequestMapping(path = "/api/products", method = RequestMethod.POST)
    public void add(@RequestBody ProductForm form) throws ApiException {
        dto.validate(form);
        service.add(form);
    }


    @ApiOperation(value = "Gets a Product by ID")
    @RequestMapping(path = "/api/products/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable int id) throws ApiException {
        return service.get(id);
    }

    @ApiOperation(value = "Updates a Product")
    @RequestMapping(path = "/api/products/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {

    }
}