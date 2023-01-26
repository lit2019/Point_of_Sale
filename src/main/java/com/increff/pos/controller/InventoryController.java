package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryUpsertForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @ApiOperation(value = "Gets list of all Inventories")
    @RequestMapping(path = "/inventory", method = RequestMethod.GET)
    public List<InventoryData> getAll() throws ApiException {
        return dto.get();
    }

    @ApiOperation(value = "Adds a list of Inventories")
    @RequestMapping(path = "/inventory", method = RequestMethod.POST)
    public void add(@RequestBody List<InventoryUpsertForm> forms) throws ApiException {
        dto.add(forms);
    }

    @ApiOperation(value = "Gets an Inventory by ID")
    @RequestMapping(path = "/inventory/{id}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Updates an Inventory")
    @RequestMapping(path = "/inventory", method = RequestMethod.PUT)
    public void update(@RequestBody InventoryUpsertForm form) throws ApiException {
        dto.update(form);
    }
}