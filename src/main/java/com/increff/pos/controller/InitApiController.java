package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.model.InfoData;
import com.increff.pos.model.UserForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InitApiController extends AbstractUiController {

    @Autowired
    private InfoData info;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/init", method = RequestMethod.GET)
    public ModelAndView showPage(UserForm form) throws ApiException {
        info.setMessage("");
        return mav("init.html");
    }

}
