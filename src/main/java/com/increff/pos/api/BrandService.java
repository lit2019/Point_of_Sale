package com.increff.pos.api;

import com.google.protobuf.Api;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandForm;
import com.increff.pos.entity.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {

    @Autowired
    private BrandDao dao;

    public void add(BrandForm form) throws ApiException {
        BrandPojo pojo = get(form.getName(),form.getCategory());
        if(pojo!=null){
            throw new ApiException("given name and category already exists");
        }
        pojo = convert(form);
        normalize(pojo);
        dao.insert(pojo);
    }

    public BrandPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    public List<BrandPojo> get(String name) {
        return dao.get(name);
    }

    public void update(Integer id, BrandForm form) throws ApiException {
        BrandPojo pojo = get(form.getName(),form.getCategory());
        if(pojo!=null){
            throw new ApiException("given name and category already exists");
        }
        pojo = dao.select(id);
        pojo = convert(form);
        normalize(pojo);
        BrandPojo ex = getCheck(id);
        ex.setCategory(pojo.getCategory());
        ex.setName(pojo.getName());
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo pojo = dao.select(id);
        if (pojo == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return pojo;
    }

    protected  void normalize(BrandPojo p) {
        p.setName(p.getName().toLowerCase().trim());
        p.setCategory(p.getCategory().toLowerCase().trim());
    }

    public BrandPojo get(String name, String category) {

        if (category==null){

        }
        BrandPojo p = dao.select(name,category);
        return p;
    }

    public void add(List<BrandForm> forms) throws ApiException {
        for(BrandForm form:forms){
            add(form);
        }
    }
    private BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setName(f.getName());
        p.setCategory(f.getCategory());
        return p;
    }


}
