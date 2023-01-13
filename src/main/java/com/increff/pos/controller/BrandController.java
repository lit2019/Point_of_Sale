package com.increff.pos.controller;

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

    @ApiOperation(value = "Adds an Brand")
    @RequestMapping(path = "/api/Brand", method = RequestMethod.POST)
    public void add(@RequestBody BrandForm form) {

        service.add(convert(form));
    }

    @ApiOperation(value = "Gets a Brand by ID")
    @RequestMapping(path = "/api/Brand/{id}", method = RequestMethod.GET)
    public BrandData get(@PathVariable int id) throws ApiException {
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
        List<BrandPojo> list = service.getAll();
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }



    @ApiOperation(value = "Updates an Brand")
    @RequestMapping(path = "/api/Brand/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
        BrandPojo p = convert(f);
        service.update(id, p);
    }


    private static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setBrandName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    private static BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setName(f.getBrandName());
        p.setCategory(f.getCategory());
        return p;
    }

}
