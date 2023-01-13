//package com.increff.pos.controller;
//
//import com.increff.pos.api.BrandService;
//import com.increff.pos.dto.BrandDto;
//import com.increff.pos.dto.ProductDto;
//import com.increff.pos.model.ProductData;
//import com.increff.pos.model.ProductForm;
//import com.increff.pos.pojo.BrandPojo;
//import com.increff.pos.pojo.ProductPojo;
//import com.increff.pos.api.ApiException;
//import com.increff.pos.api.ProductService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Api
//@RestController
//public class ProductController {
//
//    @Autowired
//    private ProductDto dto;
//
//    @Autowired
//    private ProductService service;
//
//    @ApiOperation(value = "Adds an Product")
//    @RequestMapping(path = "/api/Product", method = RequestMethod.POST)
//    public void add(@RequestBody ProductForm form) throws ApiException {
//        ProductPojo pojo = dto.validate(form);
//        service.add(pojo);
//    }
//
//
//    @ApiOperation(value = "Gets an Product by ID")
//    @RequestMapping(path = "/api/Product/{id}", method = RequestMethod.GET)
//    public ProductData get(@PathVariable int id) throws ApiException {
//        return null;
//    }
//
//    @ApiOperation(value = "Gets list of all Products")
//    @RequestMapping(path = "/api/Product", method = RequestMethod.GET)
//    public List<ProductData> getAll() {
//        return null;
//
//    }
//
//    @ApiOperation(value = "Updates an Product")
//    @RequestMapping(path = "/api/Product/{id}", method = RequestMethod.PUT)
//    public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
//    }
//
//
//    private static ProductData convert(ProductPojo p) {
//        ProductData d = new ProductData();
////        d.setProductName(p.getProductName());
////        d.setCategory(p.getCategory());
////        d.setId(p.getId());
////        return d;
//        return null;
//    }
//
//    private static ProductPojo convert(ProductForm f) {
////        ProductPojo p = new ProductPojo();
////        p.setProductName(f.getProductName());
////        p.setCategory(f.getCategory());
////        return p;
//        return null;
//    }
//
//}
