package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandDto {

    @Autowired
    private BrandDao dao;

    public BrandPojo validate(BrandForm form) throws ApiException {
        if (form==null){
            throw new ApiException("Brand form cannot be null");
        }
        if (form.getName()==null || form.getName().trim().equals("")){
            throw new ApiException("Brand name cannot be null");
        }
        if (form.getCategory()==null || form.getCategory().trim().equals("")){
            throw new ApiException("Brand category cannot be empty");
        }
        if (dao.select(form.getName(),form.getCategory())!=null){
            throw new ApiException("Brand with given name and category already exists");
        }

        return convert(form);
    }

    public List<BrandPojo> validate(List<BrandForm> forms) throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        for(BrandForm form:forms){
            BrandPojo pojo = validate(form);
            pojos.add(pojo);
        }
        return pojos;
    }

    private static BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setName(f.getName());
        p.setCategory(f.getCategory());
        return p;
    }
}
