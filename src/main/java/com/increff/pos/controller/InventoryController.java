package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @ApiOperation(value = "Gets Inventory by barcode")
    @RequestMapping(path = "/search/{barcode}", method = RequestMethod.GET)
    public List<InventoryData> getInventories(@PathVariable String barcode) throws ApiException {
        return dto.getInventories(barcode);
    }

    @ApiOperation(value = "Gets list of Inventories")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public List<InventoryData> getAll() throws ApiException {
        return dto.getInventories(null);
    }


    @ApiOperation(value = "Adds a list of Inventories")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void add(@RequestBody List<InventoryForm> forms) throws ApiException {
        dto.add(forms);
    }

    //    todo remove if not needed
    @ApiOperation(value = "Gets an Inventory by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable Integer id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Updates an Inventory")
    @RequestMapping(path = "", method = RequestMethod.PUT)
    public void update(@RequestBody InventoryForm form) throws ApiException {
        dto.update(form);
    }


}