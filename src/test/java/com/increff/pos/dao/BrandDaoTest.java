package com.increff.pos.dao;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.api.ApiException;
import com.increff.pos.entity.BrandPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static com.increff.pos.utils.TestObjectUtils.getNewBrandPojo;
import static org.junit.Assert.assertEquals;

public class BrandDaoTest extends AbstractUnitTest {
    @Autowired
    private BrandDao brandDao;

    @Test
    public void testSelect() throws ApiException {
        BrandPojo pojo1 = getNewBrandPojo("name", "category");
        brandDao.insert(pojo1);

        BrandPojo pojo2 = brandDao.select("name", "category").get(0);
        assertEquals(pojo1.getName(), pojo2.getName());
        assertEquals(pojo1.getCategory(), pojo2.getCategory());
    }

    @Test
    public void testSelectDistinctBrandNames() throws ApiException {

        brandDao.insert(getNewBrandPojo("name1", "category1"));
        brandDao.insert(getNewBrandPojo("name2", "category2"));
        brandDao.insert(getNewBrandPojo("name3", "category3"));

        HashSet<String> names = new HashSet<>(brandDao.selectDistinctBrandNames());
        assertEquals(3, names.size());

        for (String name : new String[]{"name1", "name2", "name3"})
            names.remove(name);

        assertEquals(0, names.size());
    }


}
