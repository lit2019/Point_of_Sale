package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {

    @Autowired
    private BrandDao dao;

    public void add(BrandPojo brandPojo) throws ApiException {
        if(Objects.nonNull(getByBrandNameCategory(brandPojo.getName(), brandPojo.getCategory()))){
            return;
        }
        dao.insert(brandPojo);
    }

    public BrandPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    public List<BrandPojo> getByBrandName(String name) {
        return dao.selectByMember("name", name);
    }

    public void update(Integer id, BrandPojo brandPojo) throws ApiException {
        BrandPojo ex = getCheck(id);
        if(Objects.nonNull(getByBrandNameCategory(brandPojo.getName(), brandPojo.getCategory()))){
            throw new ApiException(String.format("given name:%s and category:%s already exists",brandPojo.getName(),brandPojo.getCategory()));
        }
        ex.setCategory(brandPojo.getCategory());
        ex.setName(brandPojo.getName());
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo pojo = dao.select(id);
        if (Objects.isNull(pojo)) {
            throw new ApiException("Brand with given ID does not exist, id: " + id);
        }
        return pojo;
    }

    public BrandPojo getByBrandNameCategory(String name, String category) {
        BrandPojo p = dao.select(name, category);
        return p;
    }

    public void add(List<BrandPojo> brandPojos) throws ApiException {
        String errorMessage = "";
        for(Integer i = 0; i<brandPojos.size(); i++){
            BrandPojo brandPojo = brandPojos.get(i);
            if(Objects.nonNull(getByBrandNameCategory(brandPojo.getName(), brandPojo.getCategory()))){
                if(brandPojos.size()>1){
                    errorMessage+=String.format("%s:%s in row %d\n", brandPojo.getName(), brandPojo.getCategory(),i+1);
                }else{
                    errorMessage+=String.format("%s:%s\n", brandPojo.getName(), brandPojo.getCategory());
                }
            }
        }
        if(!errorMessage.equals("")){
            throw new ApiException("given brand:category combination Already exists \n"+errorMessage);
        }
        for(BrandPojo brandPojo : brandPojos){
            add(brandPojo);
        }
    }

    public List<String> getDistinctBrands() {
        return dao.selectDistinctBrands();
    }
}
