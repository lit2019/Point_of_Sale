package com.increff.pos.controller;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class BrandController {

    @Autowired
    private BrandService service;

    @Autowired
    private BrandDto dto;

    @ApiOperation(value = "Adds an Brand")
    @RequestMapping(path = "/api/Brand", method = RequestMethod.POST)
    public void add(@RequestBody BrandForm form) throws ApiException {
        BrandPojo pojo = dto.validate(form);
        service.add(pojo);
    }

    @ApiOperation(value = "Adds a list of Brands")
    @RequestMapping(path = "/api/Brands", method = RequestMethod.POST)
    public void add(@RequestBody List<BrandForm> forms) throws ApiException {
        List<BrandPojo> pojos = dto.validate(forms);
        service.add(pojos);
    }

    @ApiOperation(value = "Gets a Brand by ID")
    @RequestMapping(path = "/api/Brand/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable Integer id) throws ApiException {
        BrandPojo p = service.get(id);
        return convert(p);
    }
    @ApiOperation(value = "Gets a Brand by name, category")
    @RequestMapping(path = "/api/Brand/{name}/{category}", method = RequestMethod.GET)
    public BrandData getByNameCategory(@PathVariable String name,@PathVariable String category) throws ApiException {
        BrandPojo p = service.get(name,category);
        return convert(p);
    }

    @ApiOperation(value = "Gets list of all Brands")
    @RequestMapping(path = "/api/Brand", method = RequestMethod.GET)
    public List<BrandData> getAll() {
        List<BrandPojo> pojos = service.getAll();
        List<BrandData> datas = new ArrayList<BrandData>();
        for (BrandPojo p : pojos) {
            datas.add(convert(p));
        }
        return datas;
    }



    @ApiOperation(value = "Updates an Brand")
    @RequestMapping(path = "/api/Brand/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody BrandForm form) throws ApiException {
        BrandPojo pojo = dto.validate(form);
        service.update(id, pojo);
    }

    private static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

}
