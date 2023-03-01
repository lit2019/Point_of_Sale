package com.increff.pos.api;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.increff.pos.util.TestObjectUtils.getNewBrandPojo;
import static junit.framework.TestCase.*;

public class BrandApiTest extends AbstractUnitTest {

    @Autowired
    private BrandDao dao;

    @Autowired
    private BrandApi api;

    @Test
    public void testAddEmptyPojoList() {
        List<BrandPojo> pojos = new ArrayList<>();
        try {
            api.add(pojos);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("brandPojos cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAddInvalidName() {
        BrandPojo pojo = getNewBrandPojo(null, "category");
        try {
            api.add(Collections.singletonList(pojo));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("name cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAddInvalidCategory() {
        BrandPojo pojo = getNewBrandPojo("name", null);
        try {
            api.add(Collections.singletonList(pojo));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("category cannot be null", e.getMessage());
        }
    }


    @Test
    public void testAdd() throws ApiException {
//        todo add comments for teting nornalize
        List<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("  Name1", " catEgory1  "));
        pojos.add(getNewBrandPojo("  name2  ", "category2"));
        pojos.add(getNewBrandPojo("name3", "category3"));
        api.add(pojos);

        assertNotNull(dao.select("name1", "category1"));
        assertNotNull(dao.select("name2", "category2"));
        assertNotNull(dao.select("name3", "category3"));
    }

    @Test
    public void testUpdateInvalidPojo() {
        try {
            api.update(0, getNewBrandPojo(null, "category"));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("name cannot be null", e.getMessage());
        }

        try {
            api.update(0, getNewBrandPojo("name", null));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("category cannot be null", e.getMessage());
        }
    }

    @Test
    public void testUpdateExistingPojo() {
        BrandPojo pojo1 = getNewBrandPojo("name", "category");
        dao.insert(pojo1);
        BrandPojo pojo2 = getNewBrandPojo("name2", "category2");
        dao.insert(pojo2);
//todo insert two pojos here
        try {
            api.update(pojo2.getId(), getNewBrandPojo("name", "category"));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("given name:name and category:category already exists", e.getMessage());
        }
    }

    @Test
    public void testUpdateInvalidId() {
        BrandPojo pojo = getNewBrandPojo("name", "category");
        try {
            api.update(1, pojo);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Brand with given id : 1 does not exist", e.getMessage());
        }

    }

    @Test
    public void testUpdate() throws ApiException {
        BrandPojo pojo = getNewBrandPojo("name1", "category1");
        dao.insert(pojo);

        Integer id = pojo.getId();
        api.update(id, getNewBrandPojo("name2", "category2"));
        assertEquals("name2", dao.select(id).getName());
        assertEquals("category2", dao.select(id).getCategory());
    }

    @Test
    public void testGetByFilter() throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("name1", "category1"));
        pojos.add(getNewBrandPojo("name1", "category2"));
        pojos.add(getNewBrandPojo("name3", "category2"));
        pojos.forEach(pojo -> dao.insert(pojo));

//        todo check for feilds as well
        pojos = api.getByFilter("name1", "category1");
        assertEquals(1, pojos.size());

        pojos = api.getByFilter("name1", null);
        assertEquals(2, pojos.size());

        pojos = api.getByFilter(null, "category2");
        assertEquals(2, pojos.size());

        pojos = api.getByFilter(null, null);
        assertEquals(3, pojos.size());
    }

    @Test
    public void testCheckNonExistingBrandCategory() {
        List<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("name1", "category1"));
        pojos.add(getNewBrandPojo("name2", "category2"));
//        todo use try catch
        try {
            api.checkNonExistingBrandCategory(pojos);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("combinations for brand name and category does not exist : [name1_category1, name2_category2]", e.getMessage());
        }
    }

    @Test
    public void testCheckExistingBrandCategory1() {
        ArrayList<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("name1", "category1"));
        pojos.add(getNewBrandPojo("name2", "category2"));
        pojos.forEach(pojo -> dao.insert(pojo));

        try {
            api.checkExistingBrandCategory(pojos);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Existing combinations for brand name and category : [name1_category1, name2_category2]", e.getMessage());
        }
    }

    @Test
    public void testGetDistinctBrands() throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("name1", "category1"));
        pojos.add(getNewBrandPojo("name1", "category2"));
        pojos.add(getNewBrandPojo("name3", "category2"));
        pojos.forEach(pojo -> dao.insert(pojo));

        List<String> names = api.getDistinctBrands();
        Assert.assertEquals(2, names.size());

        for (String name : new String[]{"name1", "name3"})
            assertTrue(names.contains(name));
    }

    @Test
    public void testGetByNameCategory() {
        BrandPojo pojo = getNewBrandPojo("name", "category");
        dao.insert(pojo);
        BrandPojo pojo1 = api.getByNameCategory("name", "category");
        assertEqualsPojo(pojo, pojo1);
    }

    @Test
    public void testGetCheck() throws ApiException {
        BrandPojo pojo1 = getNewBrandPojo("name", "category");
        dao.insert(pojo1);
        BrandPojo pojo2 = api.getCheck(pojo1.getId());
        assertEqualsPojo(pojo1, pojo2);
    }

    @Test
    public void testGetCheckInvalidId() {
        Integer invalidId = 0;
        try {
            api.getCheck(invalidId);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals(String.format("Brand with given id : %d does not exist", invalidId), e.getMessage());
        }
    }

    private void assertEqualsPojo(BrandPojo pojo1, BrandPojo pojo2) {
        assertEquals(pojo1.getId(), pojo2.getId());
        assertEquals(pojo1.getName(), pojo2.getName());
        assertEquals(pojo1.getCategory(), pojo2.getCategory());
    }
}