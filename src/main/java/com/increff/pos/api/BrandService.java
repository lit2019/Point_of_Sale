package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {

    @Autowired
    private BrandDao dao;

    public void add(BrandPojo p) {
        normalize(p);
        dao.insert(p);
    }

    public BrandPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    public void update(int id, BrandPojo p) throws ApiException {
        normalize(p);
        BrandPojo ex = getCheck(id);
        ex.setCategory(p.getCategory());
        ex.setName(p.getName());
        dao.update(p);
    }

    public BrandPojo getCheck(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return p;
    }

    protected static void normalize(BrandPojo p) {
        p.setName(p.getName().toLowerCase().trim());
        p.setCategory(p.getCategory().toLowerCase().trim());

    }

    public BrandPojo get(String name, String category) {
        BrandPojo p = dao.select(name,category);
        return p;
    }
}
