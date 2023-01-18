package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
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

    public void add(BrandForm form) throws ApiException {
        checkNull(form);
        normalize(form);
        service.add(convert(form));
    }

    private BrandPojo convert(BrandForm brandForm) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setName(brandForm.getName());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }

    private void checkNull(BrandForm form) throws ApiException {
        if (form == null) {
            throw new ApiException("Brand form cannot be null");
        }
        if (form.getName() == null || form.getName().trim().equals("")) {
            throw new ApiException("Brand name cannot be null");
        }
        if (form.getCategory() == null || form.getCategory().trim().equals("")) {
            throw new ApiException("Brand category cannot be empty");
        }
    }

    //    TODO: check for duplication
    public void add(List<BrandForm> brandForms) throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        for(BrandForm form: brandForms){
            checkNull(form);
            normalize(form);
        }

        String errorMessage = "";
        for (Integer i = 0; i < brandForms.size(); i++) {
            BrandForm brandForm1 = brandForms.get(i);
            for (Integer j = i+1; j < brandForms.size(); j++) {
                BrandForm brandForm2 = brandForms.get(j);
                if(brandForm1.getCategory().equals(brandForm2.getCategory()) && brandForm1.getName().equals(brandForm2.getName())){
                    errorMessage+=brandForm1.getName()+":"+brandForm1.getCategory()+"\n";
                }
            }
        }
        if(!(errorMessage.equals(""))){
            throw new ApiException("duplicate rows exist for brand:category \n"+errorMessage);
        }
        for(BrandForm brandForm:brandForms){

            pojos.add(convert(brandForm));
        }
        service.add(pojos);
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
        return convert(service.getByBrandNameCategory(name, category));
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
        List<BrandPojo> brandPojos = service.getByBrandName(name);
        List<BrandData> brandDatas = new ArrayList<BrandData>();
        for (BrandPojo p : brandPojos) {
            brandDatas.add(convert(p));
        }
        return brandDatas;
    }

    public void update(Integer id, BrandForm form) throws ApiException {
        checkNull(form);
        normalize(form);
        service.update(id, convert(form));
    }
    protected  void normalize(BrandForm brandForm) {
        brandForm.setName(brandForm.getName().toLowerCase().trim());
        brandForm.setCategory(brandForm.getCategory().toLowerCase().trim());
    }

}
