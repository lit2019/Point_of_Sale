package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventorySearchForm;
import com.increff.pos.spring.AbstractUnitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.TestObjectUtils.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BrandDao brandDao;

    @Test
    public void testAddEmptyFormsList() {
        ArrayList<InventoryForm> forms = new ArrayList<>();
        try {
            inventoryDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Inventory Forms cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAddInvalidForms() {
        ArrayList<InventoryForm> forms = new ArrayList<>();
        forms.add(getNewInventoryForm(" ", 10));
        try {
            inventoryDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Barcode cannot be blank", e.getMessage());
        }
        forms = new ArrayList<>();
        forms.add(getNewInventoryForm("barcode", -10));
        try {
            inventoryDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Quantity must be a positive integer", e.getMessage());
        }
    }

    @Test
    public void testAddDuplicateForms() {
        ArrayList<InventoryForm> forms = new ArrayList<>();
        forms.add(getNewInventoryForm("barcode", 10));
        forms.add(getNewInventoryForm("barcode", 10));
        try {
            inventoryDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("duplicate barcodes provided, \n Erroneous barcodes : [barcode]", e.getMessage());
        }
    }

    @Test
    public void testAdd() throws ApiException {
        List<ProductPojo> productPojos = getNewProductPojoList();
        productPojos.forEach(productDao::insert);

        ArrayList<InventoryForm> forms = new ArrayList<>();
        forms.add(getNewInventoryForm("barcode1", 10));
        forms.add(getNewInventoryForm("barcode2", 10));
        inventoryDto.add(forms);
        List<Integer> productIds = new ArrayList<>();
        productPojos.forEach(productPojo -> productIds.add(productPojo.getId()));
        assertEquals(2, inventoryDao.selectByProductIds(productIds).size());
    }

    @Test
    public void testGetInventories() throws ApiException {
        List<ProductPojo> productPojos = getNewProductPojoList();
        productPojos.forEach(productDao::insert);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        inventoryPojos.add(getNewInventoryPojo(productPojos.get(0).getId(), 10));
        inventoryPojos.add(getNewInventoryPojo(productPojos.get(1).getId(), 10));
        inventoryPojos.forEach(inventoryDao::insert);

        assertEquals(1, inventoryDto.getInventoryByBarcode("barcode1").size());
    }

    @Test
    public void testGet() throws ApiException {
        ProductPojo productPojo = getNewProductPojo(1, "name", "barcode", 10.0);
        productDao.insert(productPojo);

        InventoryPojo inventoryPojo = getNewInventoryPojo(productPojo.getId(), 10);
        inventoryDao.insert(inventoryPojo);

        InventoryData inventoryData = inventoryDto.get(inventoryPojo.getProductId());
        assertNotNull(inventoryData);
        assertEquals(inventoryPojo.getProductId(), inventoryData.getProductId());
        assertEquals(inventoryPojo.getQuantity(), inventoryData.getQuantity());
    }

    @Test
    public void testGetInventoryReport() throws ApiException {
        List<BrandPojo> brandPojos = getNewBrandPojoList();
        brandPojos.forEach(brandDao::insert);

        List<ProductPojo> productPojos = new ArrayList<>();
        productPojos.add(getNewProductPojo(brandPojos.get(0).getId(), "name1", "barcode1", 10.0));
        productPojos.add(getNewProductPojo(brandPojos.get(1).getId(), "name2", "barcode2", 10.0));
        productPojos.add(getNewProductPojo(brandPojos.get(2).getId(), "name3", "barcode3", 10.0));
        productPojos.forEach(productDao::insert);

        productPojos.forEach(productPojo -> inventoryDao.insert(getNewInventoryPojo(productPojo.getId(), 10)));

        InventorySearchForm searchForm = new InventorySearchForm();

        assertEquals(3, inventoryDto.getInventoryReport(searchForm).size());

        searchForm.setBrandName("brand1");
        assertEquals(2, inventoryDto.getInventoryReport(searchForm).size());

        searchForm.setCategory("category1");
        assertEquals(1, inventoryDto.getInventoryReport(searchForm).size());

        searchForm.setBrandName(null);
        assertEquals(2, inventoryDto.getInventoryReport(searchForm).size());
    }

    @Test
    public void testUpdate() throws ApiException {
        List<ProductPojo> productPojos = getNewProductPojoList();
        productPojos.forEach(productDao::insert);

        InventoryPojo inventoryPojo = getNewInventoryPojo(productPojos.get(0).getId(), 10);
        inventoryDao.insert(inventoryPojo);

        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(productPojos.get(0).getBarcode());
        inventoryForm.setQuantity(20);
        inventoryDto.update(inventoryForm);
        assertEquals((Integer) 20, inventoryDao.select(inventoryPojo.getProductId()).getQuantity());
    }
}
