package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandApi {

    @Autowired
    private BrandDao dao;

    // TODO:use checknull in abstractdto

    //     TODO:remove single add funtion directly call dao.insert

    public BrandPojo get(Integer id) throws ApiException {
        return dao.select(id);
    }

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
        for (BrandPojo brandPojo : brandPojos) {
            dao.insert(brandPojo);
        }
    }


    public List<String> getDistinctBrands() {
        return dao.selectDistinctBrands();
    }
}
