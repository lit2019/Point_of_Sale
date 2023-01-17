package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandDto {


    @Autowired
    private BrandService service;

    public void validate(BrandForm form) throws ApiException {
        checkNull(form);
        service.add(form);
    }

    private void checkNull(BrandForm form) throws ApiException {
        if (form==null){
            throw new ApiException("Brand form cannot be null");
        }
        if (form.getName()==null || form.getName().trim().equals("")){
            throw new ApiException("Brand name cannot be null");
        }
        if (form.getCategory()==null || form.getCategory().trim().equals("")){
            throw new ApiException("Brand category cannot be empty");
        }
    }

    public void validate(List<BrandForm> forms) throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        for(BrandForm form:forms){
            validate(form);
        }
    }


    public BrandData get(Integer id) throws ApiException {
        return convert(service.get(id));
    }

    private static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    public BrandData get(String name, String category) {
        return convert(service.get(name, category));
    }

    public List<BrandData> getAll() {


        List<BrandPojo> pojos = service.getAll();
        List<BrandData> datas = new ArrayList<BrandData>();
        for (BrandPojo p : pojos) {
            datas.add(convert(p));
        }
        return datas;
    }

    public List<BrandData> get(String name) {
        List<BrandPojo> pojos = service.get(name);
        List<BrandData> datas = new ArrayList<BrandData>();
        for (BrandPojo p : pojos) {
            datas.add(convert(p));
        }
        return datas;
    }

    public void validateUpdate(Integer id, BrandForm form) throws ApiException {
        checkNull(form);
        service.update(id, form);
    }
}
