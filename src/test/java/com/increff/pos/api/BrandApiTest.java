package com.increff.pos.api;

import com.increff.pos.AbstractUnitTest;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.utils.TestObjectUtils.getNewBrandPojo;
import static junit.framework.TestCase.assertEquals;

public class BrandApiTest extends AbstractUnitTest {

    @Autowired
    private BrandDao dao;

    @Autowired
    private BrandApi api;

    @Test
    public void testAdd() throws ApiException {
        List<BrandPojo> pojos1 = new ArrayList<>();
        pojos1.add(getNewBrandPojo("name1", "category1"));
        pojos1.add(getNewBrandPojo("name2", "category2"));
        pojos1.add(getNewBrandPojo("name3", "category3"));
        api.add(pojos1);

        assertEquals(true, Objects.nonNull(dao.select("name1", "category1")));
        assertEquals(true, Objects.nonNull(dao.select("name2", "category2")));
        assertEquals(true, Objects.nonNull(dao.select("name3", "category3")));
    }

//    @Test
//    public void testUpdate(){
//
//    }
}
