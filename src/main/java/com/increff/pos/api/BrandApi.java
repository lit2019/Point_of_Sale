package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ListUtils.checkEmptyList;
import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
@Transactional(rollbackOn = Exception.class)
public class BrandApi extends AbstractApi {

    @Autowired
    private BrandDao dao;
    private static final Integer MAX_UPLOAD_SIZE = 5000;

    // TODO:use checknull in abstractdto

    // TODO:remove single add funtion directly call dao.insert

    //        TODO: use checknull from AbstractDTo
    //    TODO: return updated pojo
    public BrandPojo update(Integer id, BrandPojo brandPojo) throws ApiException {
        validate(brandPojo);
//        TODO use getCheck over here and use checkExistingBrandCategory();

        BrandPojo pojoWithSameFields = getByNameCategory(brandPojo.getName(), brandPojo.getCategory());
        if (Objects.nonNull(pojoWithSameFields)) {

            if (pojoWithSameFields.getId() == id) {
                return brandPojo;
            } else {
                throw new ApiException(String.format("given name:%s and category:%s already exists", pojoWithSameFields.getName(), pojoWithSameFields.getCategory()));
            }
        }
        BrandPojo existingPojo = getCheck(id);
        existingPojo.setCategory(brandPojo.getCategory());
        existingPojo.setName(brandPojo.getName());
        return existingPojo;
    }


    public List<BrandPojo> getByFilter(String name, String category) {
        return dao.select(name, category);
    }


    public void add(List<BrandPojo> brandPojos) throws ApiException {
        checkEmptyList(brandPojos, "brandPojos cannot be empty");
        for (BrandPojo brandPojo : brandPojos)
            validate(brandPojo);

        checkExistingBrandCategory(brandPojos);
        for (BrandPojo brandPojo : brandPojos)
            dao.insert(brandPojo);
    }

    public List<String> getDistinctBrands() {
        return dao.selectDistinctBrandNames();
    }

    public BrandPojo getByNameCategory(String brandName, String category) {
        List<BrandPojo> pojos = dao.select(brandName, category);
        if (CollectionUtils.isEmpty(pojos)) {
            return null;
        }
        return pojos.get(0);
    }

    public BrandPojo get(Integer id) {
        return dao.select(id);
    }

    public void checkNonExistingBrandCategory(List<BrandPojo> pojos) throws ApiException {
        ArrayList<String> erroneousCombinations = new ArrayList<>();
        pojos.forEach((pojo) -> {
            if (Objects.isNull(getByNameCategory(pojo.getName(), pojo.getCategory()))) {
                erroneousCombinations.add(pojo.getName() + "_" + pojo.getCategory());
            }
        });
        checkNonEmptyList(erroneousCombinations, "combinations for brand name and category does not exist : " + erroneousCombinations);
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo pojo = get(id);
        checkNull(pojo, String.format("Brand with given id : %d does not exist", id));
        return pojo;
    }

    public void checkExistingBrandCategory(List<BrandPojo> pojos) throws ApiException {
        ArrayList<String> existingCombinations = new ArrayList<>();
//        TODO:use foreach instead
        for (BrandPojo pojo : pojos)
            if (Objects.nonNull(getByNameCategory(pojo.getName(), pojo.getCategory())))
                existingCombinations.add(pojo.getName() + "_" + pojo.getCategory());

        checkNonEmptyList(existingCombinations, "Existing combinations for brand name and category : " + existingCombinations);
    }

    private void validate(BrandPojo brandPojo) throws ApiException {
        checkNull(brandPojo.getName(), "name cannot be null");
        checkNull(brandPojo.getCategory(), "category cannot be null");
    }

}
