package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return dao.selectByMember("name", name);
    }

    //        TODO: use checknull from AbstractDTo
    //    TODO: return updated pojo
    public BrandPojo update(Integer id, BrandPojo brandPojo) throws ApiException {
        BrandPojo exBrandPojo = get(id);
        exBrandPojo.setCategory(brandPojo.getCategory());
        exBrandPojo.setName(brandPojo.getName());
        return exBrandPojo;
    }

    public BrandPojo getByNameCategory(String name, String category) {
        BrandPojo brandPojo = dao.select(name, category);
        return brandPojo;
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
            if (Objects.nonNull(getByNameCategory(form.getName(), form.getCategory()))) {
                existingCombinations.add(form.getName() + "_" + form.getCategory());
            }
        });
        checkNonEmptyList(existingCombinations, "existing combinations for brand name and category : " + existingCombinations.toString());
    }

}
