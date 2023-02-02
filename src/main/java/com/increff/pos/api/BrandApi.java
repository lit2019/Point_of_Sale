package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandApi extends AbstractApi<BrandPojo> {

    @Autowired
    private BrandDao dao;

    // TODO:use checknull in abstractdto

    //     TODO:remove single add funtion directly call dao.insert

    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    public List<BrandPojo> getByName(String name) {
        return dao.select(name,null);
    }

    //        TODO: use checknull from AbstractDTo
    //    TODO: return updated pojo
    public BrandPojo update(Integer id, BrandPojo brandPojo) throws ApiException {
        BrandPojo existingPojo = get(id);
        existingPojo.setCategory(brandPojo.getCategory());
        existingPojo.setName(brandPojo.getName());
        return existingPojo;
    }

    public List<BrandPojo> getByNameCategory(String name, String category) {
        return dao.select(name, category);
    }

    public void add(List<BrandPojo> brandPojos) throws ApiException {
        checkExistingBrandCategory(brandPojos);
        for (BrandPojo brandPojo : brandPojos) {
            dao.insert(brandPojo);
        }
    }

    public List<String> getDistinctBrands() {
        return dao.selectDistinctBrands();
    }

    private void checkExistingBrandCategory(List<BrandPojo> forms) throws ApiException {
        ArrayList<String> existingCombinations = new ArrayList<>();
//        TODO:use foreach instead
        forms.forEach((form) -> {
            if (CollectionUtils.isEmpty(getByNameCategory(form.getName(), form.getCategory()))) {
                existingCombinations.add(form.getName() + "_" + form.getCategory());
            }
        });
        checkNonEmptyList(existingCombinations, "existing combinations for brand name and category : " + existingCombinations.toString());
    }

}
