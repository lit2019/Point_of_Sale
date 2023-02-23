package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.spring.AbstractUnitTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.increff.pos.util.TestObjectUtils.getNewProductPojo;
import static junit.framework.TestCase.*;

public class ProductApiTest extends AbstractUnitTest {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ProductDao productDao;

    @Test
    public void testAddEmptyPojoList() {
        ArrayList<ProductPojo> pojos = new ArrayList<>();
        try {
            productApi.add(pojos);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("ProductPojos cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAddInvalidPojo() {
        ProductPojo pojo = getNewProductPojo(1, null, "barcode", 12.0);
        try {
            productApi.add(Collections.singletonList(pojo));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Name cannot be null", e.getMessage());
        }

        pojo = getNewProductPojo(1, "product", null, 12.0);
        try {
            productApi.add(Collections.singletonList(pojo));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Barcode cannot be null", e.getMessage());
        }

        pojo = getNewProductPojo(1, "product", "barcode", -1.0);
        productDao.insert(pojo);
        try {
            productApi.add(Collections.singletonList(pojo));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Product(s) with barcode(s) already exists : [barcode]", e.getMessage());
        }
    }

    @Test
    public void testAdd() throws ApiException {
        ArrayList<ProductPojo> pojos = new ArrayList<>();
        pojos.add(getNewProductPojo(1, "product", "barcode", 12.0));

        productApi.add(pojos);
        assertEquals(true, Objects.nonNull(productDao.selectByBarcodes(Collections.singletonList("barcode"))));
    }

    @Test
    public void testUpdateInvalidPojo() {
        try {
            productApi.update(1, getNewProductPojo(1, null, "barcode", 300.0));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Name cannot be null", e.getMessage());
        }
        try {
            productApi.update(1, getNewProductPojo(1, "name", "barcode", null));
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Mrp cannot be null", e.getMessage());
        }
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductPojo pojo = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo);
        Integer id = pojo.getId();
        productApi.update(id, getNewProductPojo(1, "product2", "barcode", 13.0));
        assertEquals("product2", productDao.select(id).getName());
        assertEquals(13.0, productDao.select(id).getMrp());
    }

    @Test
    public void testGetUniqueByBarcodeNull() {
        try {
            productApi.getUniqueByBarcode(null);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Barcode cannot be null", e.getMessage());
        }
    }

    @Test
    public void testGetUniqueByBarcode() throws ApiException {
        ProductPojo pojo = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo);

        ProductPojo pojo2 = productApi.getUniqueByBarcode("barcode");
        assertNotNull(pojo2);
        assertEquals(pojo.getId(), pojo2.getId());
    }

    @Test
    public void testGet() {
        ProductPojo pojo1 = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo1);
        ProductPojo pojo2 = productApi.get(pojo1.getId());
        Assert.assertNotNull(pojo2);
        assertEquals(pojo1.getId(), pojo2.getId());
    }

    @Test
    public void testGetBarcodeToProductPojoMap() {
        assertEquals(0, productApi.getBarcodeToProductPojoMap(Collections.emptyList()).size());
        ProductPojo pojo = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo);
        assertEquals(pojo.getId(), productApi.getBarcodeToProductPojoMap(productDao.selectByBarcodes(Collections.singletonList("barcode"))).get("barcode").getId());
    }

    @Test
    public void testGetByBarcodes() {
        assertEquals(0, productApi.getByBarcodes(Collections.emptyList()).size());
        ProductPojo pojo = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo);
        assertEquals(pojo.getId(), productApi.getByBarcodes(Collections.singletonList("barcode")).get(0).getId());
    }

    @Test
    public void testGetByBrandIds() {
        assertEquals(0, productApi.getByBrandIds(Collections.emptyList()).size());
        ProductPojo pojo = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo);
        assertEquals(pojo.getId(), productApi.getByBrandIds(Collections.singletonList(1)).get(0).getId());
    }


    @Test
    public void testGetCheckInvalidId() {
        Integer invalidId = 0;
        try {
            productApi.getCheck(invalidId);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals(String.format("Product with given id : %d does not exist", invalidId), e.getMessage());
        }
    }

    @Test
    public void testGetCheck() throws ApiException {
        ProductPojo pojo = getNewProductPojo(1, "product", "barcode", 12.0);
        productDao.insert(pojo);

        assertEquals(pojo.getId(), productApi.getCheck(pojo.getId()).getId());
    }

    @Test
    public void testCheckExistingBarcode() throws ApiException {
        String barcode = "barcode";
        ArrayList<ProductPojo> pojos = new ArrayList<>();
        ProductPojo pojo1 = getNewProductPojo(1, "product", barcode, 12.0);
        pojos.add(pojo1);
        productDao.insert(pojo1);

        ProductPojo pojo2 = getNewProductPojo(2, "product", "barcode2", 12.0);
        pojos.add(pojo2);
        ProductPojo pojo3 = getNewProductPojo(3, "product", "barcode3", 12.0);
        pojos.add(pojo3);

        try {
            productApi.checkExistingBarcode(pojos);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals(String.format("Product(s) with barcode(s) already exists : [%s]", barcode), e.getMessage());
        }
    }
}
