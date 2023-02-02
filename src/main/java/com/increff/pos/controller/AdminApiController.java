package com.increff.pos.controller;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.UserService;
import com.increff.pos.entity.UserPojo;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class AdminApiController {

    @Autowired
    private UserService service;

    @ApiOperation(value = "Adds a user")
    @RequestMapping(path = "/api/admin/user", method = RequestMethod.POST)
    public void addUser(@RequestBody UserForm form) throws ApiException {
        UserPojo p = convert(form);
        service.add(p);
    }

    @ApiOperation(value = "Deletes a user")
    @RequestMapping(path = "/api/admin/user/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable int id) {
        service.delete(id);
    }

    @ApiOperation(value = "Gets list of all users")
    @RequestMapping(path = "/api/admin/user", method = RequestMethod.GET)
    public List<UserData> getAllUser() {
        List<UserPojo> list = service.getAll();
        List<UserData> list2 = new ArrayList<UserData>();
        for (UserPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    private static UserData convert(UserPojo p) {
        UserData d = new UserData();
        d.setEmail(p.getEmail());
        d.setRole(p.getRole());
        d.setId(p.getId());
        return d;
    }

    private static UserPojo convert(UserForm f) {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setRole(f.getRole());
        p.setPassword(f.getPassword());
        return p;
    }

}
