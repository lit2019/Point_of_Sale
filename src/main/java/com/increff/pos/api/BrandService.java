package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {

    @Autowired
    private BrandDao dao;

    public void add(BrandPojo brandPojo) throws ApiException {
        if(Objects.nonNull(getByBrandNameCategory(brandPojo.getName(),brandPojo.getCategory()))){
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
        return dao.selectByMember("name",name);
    }

    public void update(Integer id, BrandPojo brandPojo) throws ApiException {
        BrandPojo ex = getCheck(id);
        if(Objects.nonNull(getByBrandNameCategory(brandPojo.getName(),brandPojo.getCategory()))){
//            TODO: show values of name and category here
            throw new ApiException("given name:"+brandPojo.getName()+" and category:"+brandPojo.getCategory()+" already exists");
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

//    TODO: should be in dto layer


    public BrandPojo getByBrandNameCategory(String name, String category) {
        BrandPojo p = dao.select(name,category);
        return p;
    }

    public void add(List<BrandPojo> brandPojos) throws ApiException {
        String errorMessage = "";
        for(BrandPojo brandPojo : brandPojos){
            if(Objects.nonNull(getByBrandNameCategory(brandPojo.getName(),brandPojo.getCategory()))){
                errorMessage+=" ["+brandPojo.getName()+":"+brandPojo.getCategory()+"] ";
            }
        }
        if(!errorMessage.equals("")){
            throw new ApiException("given brand:category combination Already exists "+errorMessage);
        }
        for(BrandPojo brandPojo : brandPojos){
            add(brandPojo);
        }
    }
}
