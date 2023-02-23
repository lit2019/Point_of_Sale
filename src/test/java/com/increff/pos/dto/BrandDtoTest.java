package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.spring.AbstractUnitTest;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.increff.pos.util.TestObjectUtils.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class BrandDtoTest extends AbstractUnitTest {
    @Autowired
    private BrandDto dto;
    @Autowired
    private BrandDao dao;

    @Test
    public void testAdd1() {
        ArrayList<BrandForm> forms = new ArrayList<>();
        try {
            dto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Brand Forms cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAdd2() {
        ArrayList<BrandForm> forms = new ArrayList<>();
        forms.add(getNewBrandForm(" ", "category"));
        try {
            dto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Brand name cannot be blank", e.getMessage());
        }
        forms = new ArrayList<>();
        forms.add(getNewBrandForm("name", " "));
        try {
            dto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Category cannot be blank", e.getMessage());
        }
    }

    @Test
    public void testAdd3() {
        ArrayList<BrandForm> forms = new ArrayList<>();
        forms.add(getNewBrandForm("Brand", "categOry"));
        forms.add(getNewBrandForm("  brand", "CateGory  "));
        try {
            dto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Duplicate Combinations for brand name and category \n Erroneous combinations : [brand_category]", e.getMessage());
        }

    }

    @Test
    public void testAdd4() throws ApiException {
        ArrayList<BrandForm> forms = new ArrayList<>();
        forms.add(getNewBrandForm("Brand1", "categOry1"));
        forms.add(getNewBrandForm("  brand2", "CateGory2  "));
        forms.add(getNewBrandForm("brand3", "  category3  "));

        dto.add(forms);
        List<BrandPojo> pojos = dao.selectAll();
        assertEquals(3, pojos.size());

        List<String> brand_categorys = Arrays.asList(new String[]{"brand1_category1", "brand2_category2", "brand3_category3"});
        for (BrandPojo pojo : pojos)
            assertEquals(true, brand_categorys.contains(pojo.getName() + "_" + pojo.getCategory()));
    }

    @Test
    public void testGet() throws ApiException {
        BrandPojo pojo = getNewBrandPojo("name", "category");
        dao.insert(pojo);
        assertEquals("name", dto.get(pojo.getId()).getName());
        assertEquals("category", dto.get(pojo.getId()).getCategory());
    }

    @Test
    public void testGetBySearchForm() {
        List<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("name1", "category1"));
        pojos.add(getNewBrandPojo("name1", "category2"));
        pojos.add(getNewBrandPojo("name3", "category2"));
        pojos.forEach(pojo -> dao.insert(pojo));

        List<BrandData> datas;
        datas = dto.get(getNewBrandSearchForm(" Name1", "  category1  "));
        TestCase.assertEquals(1, datas.size());

        datas = dto.get(getNewBrandSearchForm("name1", null));
        TestCase.assertEquals(2, datas.size());

        datas = dto.get(getNewBrandSearchForm(null, "categorY2"));
        TestCase.assertEquals(2, datas.size());

        datas = dto.get(getNewBrandSearchForm(null, null));
        TestCase.assertEquals(3, datas.size());
    }

    @Test
    public void testGetDistinctBrands() throws ApiException {
        List<BrandPojo> pojos = new ArrayList<>();
        pojos.add(getNewBrandPojo("name1", "category1"));
        pojos.add(getNewBrandPojo("name1", "category2"));
        pojos.add(getNewBrandPojo("name3", "category2"));
        pojos.forEach(pojo -> dao.insert(pojo));

        List<String> names = dto.getDistinctBrands();
        Assert.assertEquals(2, names.size());

        for (String name : new String[]{"name1", "name3"})
            TestCase.assertEquals(true, names.contains(name));
    }

    @Test
    public void testUpdate1() {
        try {
            dto.update(0, getNewBrandForm("  ", "category"));
            fail("expected ApiException");
        } catch (ApiException e) {
            TestCase.assertEquals("Brand name cannot be blank", e.getMessage());
        }

        try {
            dto.update(0, getNewBrandForm("name", "  "));
            fail("expected ApiException");
        } catch (ApiException e) {
            TestCase.assertEquals("Category cannot be blank", e.getMessage());
        }
    }

    @Test
    public void testUpdate2() throws ApiException {
        BrandPojo pojo = getNewBrandPojo("name", "category");
        dao.insert(pojo);
        dto.update(pojo.getId(), getNewBrandForm(" nAme2  ", "cAtegorY2"));
        BrandPojo pojo1 = dao.select(pojo.getId());
        TestCase.assertEquals("name2", pojo1.getName());
        TestCase.assertEquals("category2", pojo1.getCategory());
    }

    @Test
    public void testCheckDuplicate() {
        ArrayList<BrandForm> brandForms = new ArrayList<>();
        brandForms.add(getNewBrandForm("name", "category"));
        brandForms.add(getNewBrandForm("name", "category"));
        brandForms.add(getNewBrandForm("name2", "category2"));

        try {
            dto.add(brandForms);
            fail("expected ApiException");
        } catch (ApiException e) {
            TestCase.assertEquals("Duplicate Combinations for brand name and category \n Erroneous combinations : [name_category]", e.getMessage());
        }
    }
}
