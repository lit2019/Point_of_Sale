package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandSearchForm;
import com.increff.pos.model.BrandUpsertForm;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;
import static com.increff.pos.util.ValidatorUtil.validate;

@Service
public class BrandDto extends AbstractDto<BrandUpsertForm> {

    @Autowired
    private BrandApi api;
    @Autowired
    private BrandDao dao;


    //TODO:move to api

    public BrandData get(Integer id) throws ApiException {
        return convert(api.get(id));
    }

    public List<BrandData> get(BrandSearchForm form) {
        normalize(form);
        List<BrandPojo> pojos = api.getByNameCategory(form.getName(), form.getCategory());
        ArrayList<BrandData> datas = new ArrayList<>();
        pojos.forEach(pojo -> {
            datas.add(convert(pojo));
        });
        return datas;
    }

    public List<BrandData> getAll() {
        List<BrandPojo> pojos = api.getAll();
        List<BrandData> datas = new ArrayList<BrandData>();
        for (BrandPojo p : pojos) {
            datas.add(convert(p));
        }
        return datas;
    }

    public List<String> get(String name) {
        List<BrandPojo> brandPojos = api.getByName(name);
        List<String> brandDatas = new ArrayList<String>();
        for (BrandPojo p : brandPojos) {
            brandDatas.add(p.getCategory());
        }
        return brandDatas;
    }

    public List<String> getDistinctBrands() {
        return api.getDistinctBrands();

    }

    public void update(Integer id, BrandUpsertForm form) throws ApiException {
        validate(form);
        normalize(Collections.singletonList(form));
        getCheck(id);
//        TODO: use checknull from AbstractDTo
        checkNonNullObject(api.getByNameCategory(form.getName(), form.getCategory()), String.format("given name:%s and category:%s already exists", form.getName(), form.getCategory()));
        api.update(id, convert(form));
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo brandPojo = api.get(id);
//        TODO: use checknull from AbstractDTo
        checkNullObject(brandPojo, "Brand with given ID does not exist, id: " + id);
        return brandPojo;
    }

    public void add(List<BrandUpsertForm> forms) throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        for (BrandUpsertForm form : forms) {
            validate(form); //TODO: use checkValid javax validation {validate->normalize ->service}
        }
        normalize(forms); //TODO:use normalize in api level
        checkDuplicate(forms);

        forms.forEach((form) -> {
            pojos.add(convert(form));
        });
        api.add(pojos);
    }

    private void normalize(BrandSearchForm searchForm) {
        if (Objects.nonNull(searchForm.getCategory())) {
            searchForm.setCategory(StringUtil.normaliseText(searchForm.getCategory()));
        }
        if (Objects.nonNull(searchForm.getName())) {
            searchForm.setName(StringUtil.normaliseText(searchForm.getName()));
        }
    }

    private BrandPojo convert(BrandUpsertForm brandForm) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setName(brandForm.getName());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }


    //    TODO: create methods for throwing exceptions
    private static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setName(p.getName());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    private void checkDuplicate(List<BrandUpsertForm> forms) throws ApiException {
        ArrayList<String> brandCategoryCombinations = new ArrayList<>();

        forms.forEach((form) -> {
            String key = form.getName() + "_" + form.getCategory();
            brandCategoryCombinations.add(key);
        });
        ArrayList<String> duplicateCombinations = ListUtils.getDuplicates(brandCategoryCombinations);
        checkNonEmptyList(duplicateCombinations, "duplicate combinations for brand name and category \n Erroneous combinations : " + duplicateCombinations.toString());
    }

    protected void normalize(List<BrandUpsertForm> forms) {
        for (BrandUpsertForm form : forms) {
            form.setName(StringUtil.normaliseText(form.getName()));
            form.setCategory(StringUtil.normaliseText(form.getCategory()));
        }
    }
}
