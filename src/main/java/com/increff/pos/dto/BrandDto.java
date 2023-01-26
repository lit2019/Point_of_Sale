package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandUpsertForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
public class BrandDto extends AbstractDto<BrandUpsertForm> {


    @Autowired
    private BrandApi service;

    private static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    private BrandPojo convert(BrandUpsertForm brandForm) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setName(brandForm.getName());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }


    //    TODO: create methods for throwing exceptions
    public void add(List<BrandUpsertForm> forms) throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        for (BrandUpsertForm form : forms) {
            validate(form); //TODO: use checkValid javax validation {validate->normalize ->service}
            normalize(form); //TODO:use normalize in api level
        }
        checkDuplicate(forms);

        forms.forEach((form) -> {
            pojos.add(convert(form));
        });
        service.add(pojos);
    }

    private void checkDuplicate(List<BrandUpsertForm> forms) throws ApiException {
        HashSet<String> brandCategorySet = new HashSet<>();
        ArrayList<String> duplicateCombinations = new ArrayList<>();
        forms.forEach((form) -> {
            String key = form.getName() + "_" + form.getCategory();
            if (brandCategorySet.contains(key)) {
                duplicateCombinations.add(key);
            } else {
                brandCategorySet.add(key);
            }
        });
        checkNonEmptyList(duplicateCombinations, "duplicate combinations for brand name and category : " + duplicateCombinations.toString());
    }

    //TODO:move to api

    public BrandData get(Integer id) throws ApiException {
        return convert(service.get(id));
    }

    public List<BrandData> get(BrandUpsertForm brandUpsertForm) {
        ArrayList<BrandData> datas = new ArrayList<>();
        List<BrandPojo> pojos;
        if (!StringUtil.isEmpty(brandUpsertForm.getName())) {
            if (!StringUtil.isEmpty(brandUpsertForm.getCategory())) {
                pojos = Collections.singletonList(service.getByNameCategory(brandUpsertForm.getName(), brandUpsertForm.getCategory()));
            } else {
                pojos = service.getByName(brandUpsertForm.getName());
            }
        } else {

            pojos = service.getAll();
        }
        pojos.forEach(pojo -> {
            datas.add(convert(pojo));
        });
        return datas;
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
        List<BrandPojo> brandPojos = service.getByName(name);
        List<String> brandDatas = new ArrayList<String>();
        for (BrandPojo p : brandPojos) {
            brandDatas.add(p.getCategory());
        }
        return brandDatas;
    }

    public List<String> getDistinctBrands() {
        return service.getDistinctBrands();

    }

    public void update(Integer id, BrandUpsertForm form) throws ApiException {
        validate(form);
        normalize(form);
        BrandPojo exBrandPojo = getCheck(id);
//        TODO: use checknull from AbstractDTo
        checkNonNullObject(service.getByNameCategory(form.getName(), form.getCategory()), String.format("given name:%s and category:%s already exists", form.getName(), form.getCategory()));
        service.update(id, convert(form));
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo brandPojo = service.get(id);
//        TODO: use checknull from AbstractDTo
        checkNullObject(brandPojo, "Brand with given ID does not exist, id: " + id);
        return brandPojo;
    }

    protected void normalize(BrandUpsertForm brandForm) {
        brandForm.setName(StringUtil.normaliseText(brandForm.getName()));
        brandForm.setCategory(StringUtil.normaliseText(brandForm.getCategory()));
    }
}
