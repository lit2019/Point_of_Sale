package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandUpsertForm;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BrandDto {


    @Autowired
    private BrandService service;

    private BrandPojo convert(BrandUpsertForm brandForm) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setName(brandForm.getName());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }

    private void checkNull(BrandUpsertForm form) throws ApiException {
        if (Objects.isNull(form)) {
            throw new ApiException("Brand form cannot be null");
        }
        if (Objects.isNull(form.getName()) || form.getName().trim().equals("")) {
            throw new ApiException("Brand name cannot be null");
        }
        if (Objects.isNull(form.getCategory()) || form.getCategory().trim().equals("")) {
            throw new ApiException("Brand category cannot be empty");
        }
    }

    //    TODO: check for duplication
    public void add(List<BrandUpsertForm> brandForms) throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        for(BrandUpsertForm form: brandForms){
            checkNull(form);
            normalize(form);
        }

        String errorMessage = "";
        for (Integer i = 0; i < brandForms.size(); i++) {
            BrandUpsertForm brandForm1 = brandForms.get(i);
            for (Integer j = i+1; j < brandForms.size(); j++) {
                BrandUpsertForm brandForm2 = brandForms.get(j);
                if(brandForm1.getCategory().equals(brandForm2.getCategory()) && brandForm1.getName().equals(brandForm2.getName())){
                    errorMessage+=String.format("%s:%s in rows %d and %d\n", brandForm1.getName(), brandForm1.getCategory(),i+1,j+1);
                }
            }
        }
        if(!(errorMessage.equals(""))){
            throw new ApiException("duplicate rows exist for brand:category \n"+errorMessage);
        }
        for(BrandUpsertForm brandForm:brandForms){
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

    public BrandData get(BrandUpsertForm brandUpsertForm) {
        return convert(service.getByBrandNameCategory(brandUpsertForm.getName(), brandUpsertForm.getCategory()));
    }

    public List<BrandData> getAll() {
        List<BrandPojo> pojos = service.getAll();
        List<BrandData> datas = new ArrayList<BrandData>();
        for (BrandPojo p : pojos) {
            datas.add(convert(p));
        }
        return datas;
    }

    public List<String> get(String name) {
        List<BrandPojo> brandPojos = service.getByBrandName(name);
        List<String> brandDatas = new ArrayList<String>();
        for (BrandPojo p : brandPojos) {
            brandDatas.add(p.getCategory());
        }
        return brandDatas;
    }

    public void update(Integer id, BrandUpsertForm form) throws ApiException {
        checkNull(form);
        normalize(form);
        service.update(id, convert(form));
    }

    protected  void normalize(BrandUpsertForm brandForm) {
        brandForm.setName(StringUtil.normaliseText(brandForm.getName()));
        brandForm.setCategory(StringUtil.normaliseText(brandForm.getCategory()));
    }

}
