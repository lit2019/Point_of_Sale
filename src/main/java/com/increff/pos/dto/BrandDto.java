package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ValidationUtil.validate;

@Service
public class BrandDto {
    @Autowired
    private BrandApi api;
    private static final Integer MAX_UPLOAD_SIZE = 5000;

    public void add(List<BrandForm> forms) throws ApiException {
        ListUtils.checkEmptyList(forms, "Brand Forms cannot be empty");
        ListUtils.checkUploadLimit(forms, MAX_UPLOAD_SIZE);

        List<BrandPojo> pojos = new ArrayList<>();
        for (BrandForm form : forms)
            validate(form);

        for (BrandForm form : forms)
            NormalizationUtil.normalize(form);

        checkDuplicate(forms);

        forms.forEach((form) -> {
            pojos.add(convert(form));
        });
        api.add(pojos);
    }

    public BrandData get(Integer id) throws ApiException {
        return convert(api.get(id));
    }

    public List<BrandData> get(BrandForm form) {
        NormalizationUtil.normalize(form);

        List<BrandPojo> pojos = api.getByFilter(form.getName(), form.getCategory());
        ArrayList<BrandData> dataList = new ArrayList<>();
        pojos.forEach(pojo -> dataList.add(convert(pojo)));

        return dataList;
    }

    public List<String> getDistinctBrands() {
        return api.getDistinctBrands();

    }

    public void update(Integer id, BrandForm form) throws ApiException {
        validate(form);
        NormalizationUtil.normalize(form);

        api.update(id, convert(form));
    }

    private void checkDuplicate(List<BrandForm> forms) throws ApiException {
        ArrayList<String> brandCategoryCombinations = new ArrayList<>();

        forms.forEach((form) -> {
            String key = form.getName() + "_" + form.getCategory();
            brandCategoryCombinations.add(key);
        });
        ListUtils.checkDuplicates(brandCategoryCombinations, "Duplicate Combinations for brand name and category \n Erroneous combinations : ");
    }

    private BrandData convert(BrandPojo pojo) {
        BrandData data = new BrandData();
        data.setName(pojo.getName());
        data.setCategory(pojo.getCategory());
        data.setId(pojo.getId());
        return data;
    }


    private BrandPojo convert(BrandForm form) {
        BrandPojo pojo = new BrandPojo();
        pojo.setCategory(form.getCategory());
        pojo.setName(form.getName());
        return pojo;
    }

}
