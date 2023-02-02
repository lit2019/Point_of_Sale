package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {

    @RequestMapping(value = "/ui/home")
    public ModelAndView home() {
        return mav("home.html");
    }

    @RequestMapping(value = "/ui/admin")
    public ModelAndView admin() {
        return mav("user.html");
    }

    @RequestMapping(value = "/ui/brands")
    public ModelAndView brands() {
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

    @RequestMapping(value = "/ui/inventory-report")
    public ModelAndView inventoryReport() {
        return mav("inventory_report.html");
    }

    @RequestMapping(value = "/ui/sales-report")
    public ModelAndView salesReport() {
        return mav("sales_report.html");
    }


}
