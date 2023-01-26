package com.increff.pos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class UIController {

    @Value("${app.baseUrl}")
    private String baseUrl;

    @RequestMapping(value = "/ui/brands")
    public ModelAndView home() {
        return mav("brands.html");
    }

    @RequestMapping(value = "/ui/products")
    public ModelAndView products() {
        return mav("products.html");
    }

    @RequestMapping(value = "/ui/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }

    @RequestMapping(value = "/ui/orders")
    public ModelAndView orders() {
        return mav("orders.html");
    }

    private ModelAndView mav(String page) {
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("baseUrl", baseUrl);
        return mav;
    }

}
