package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.ProductUpdateForm;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.util.TestObjectUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;

import static com.increff.pos.util.TestObjectUtils.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ProductDao productDao;
    @Autowired
    private BrandDao brandDao;

    @Test
    public void testGet() throws ApiException {
        BrandPojo brandPojo = getNewBrandPojo("brand", "category");
        brandDao.insert(brandPojo);
        
        ProductPojo productPojo = getNewProductPojo(brandPojo.getId(), "product", "barcode", 12.0);
        productDao.insert(productPojo);
        ProductData productData = productDto.get(productPojo.getId());
        assertNotNull(productData);
        assertEquals(productPojo.getId(), productData.getId());
        assertEquals(productPojo.getName(), productData.getProductName());
        assertEquals(productPojo.getBarcode(), productData.getBarcode());
        assertEquals(productPojo.getMrp(), productData.getMrp());
        assertEquals(brandPojo.getName(), productData.getBrandName());
        assertEquals(brandPojo.getCategory(), productData.getCategory());
    }

    @Test
    public void testAddEmptyForms() {
        ArrayList<ProductForm> forms = new ArrayList<>();
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Product Forms cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAddBlankFields() {
        ArrayList<ProductForm> forms = new ArrayList<>();
        forms.add(getNewProductForm(" ", "category", "product", "barcode", 12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Brand name cannot be blank", e.getMessage());
        }

        forms = new ArrayList<>();
        forms.add(getNewProductForm("brand", " ", "product", "barcode", 12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Category cannot be blank", e.getMessage());
        }

        forms = new ArrayList<>();
        forms.add(getNewProductForm("brand", "category", " ", "barcode", 12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Product name cannot be blank", e.getMessage());
        }
        forms = new ArrayList<>();
        forms.add(getNewProductForm("brand", "category", "product", " ", 12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Barcode cannot be blank", e.getMessage());
        }

        forms = new ArrayList<>();
        forms.add(getNewProductForm("brand", "category", "product", "barcode", -12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Quantity must be Positive", e.getMessage());
        }
    }

    @Test
    public void testAddDuplicateBarcode() {
        ArrayList<ProductForm> forms = new ArrayList<>();
        String barcode = "barcode";
        forms.add(getNewProductForm("brand", "category", "product", barcode, 12.0));
        forms.add(getNewProductForm("brand", "category", "product", barcode, 12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals(String.format("duplicate barcodes exist \n Erroneous barcodes : [%s]", barcode), e.getMessage());
        }
    }

    @Test
    public void testAddInvalidBrandCategory() {
        ArrayList<ProductForm> forms = new ArrayList<>();
        String category = "category";
        String brand = "brand";

        forms.add(getNewProductForm(brand, category, "product", "barcode", 12.0));
        try {
            productDto.add(forms);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals(String.format("combinations for brand name and category does not exist : [brand_category]", brand, category), e.getMessage());
        }
    }

    @Test
    public void testAdd() throws ApiException {
        BrandPojo brandPojo = getNewBrandPojo("brand", "category");
        brandDao.insert(brandPojo);

        ArrayList<ProductForm> forms = new ArrayList<>();
        forms.add(getNewProductForm("  BrAnD  ", " caTegory  ", "producT", "  barcodE  ", 12.0));

        productDto.add(forms);
        ProductPojo pojo = productDao.selectByBarcodes(Collections.singletonList("barcode")).get(0);
        assertEquals(brandPojo.getId(), pojo.getBrandCategoryId());
        assertEquals("product", pojo.getName());
        assertEquals("barcode", pojo.getBarcode());
        assertEquals((Double) 12.0, pojo.getMrp());
    }

    @Test
    public void testUpdate() throws ApiException {
        BrandPojo brandPojo = getNewBrandPojo("brand", "category");
        brandDao.insert(brandPojo);

        ProductPojo pojo = getNewProductPojo(brandPojo.getId(), "product1", "barcode", 12.0);
        productDao.insert(pojo);

        ProductUpdateForm productUpdateForm = new ProductUpdateForm();
        productUpdateForm.setMrp(13.0);
        productUpdateForm.setProductName("  ProDuct2  ");

        productDto.update(pojo.getId(), productUpdateForm);
        assertEquals(brandPojo.getId(), pojo.getBrandCategoryId());
        assertEquals("product2", pojo.getName());
        assertEquals((Double) 13.0, pojo.getMrp());
    }

    @Test
    public void testFilter() throws ApiException {
        BrandPojo brandPojo1 = getNewBrandPojo("brand1", "category1");
        brandDao.insert(brandPojo1);
        BrandPojo brandPojo2 = getNewBrandPojo("brand1", "category2");
        brandDao.insert(brandPojo2);
        BrandPojo brandPojo3 = getNewBrandPojo("brand2", "category1");
        brandDao.insert(brandPojo3);

        ProductPojo pojo1 = getNewProductPojo(brandPojo1.getId(), "product1", "barcode1", 12.0);
        productDao.insert(pojo1);
        ProductPojo pojo2 = getNewProductPojo(brandPojo2.getId(), "product2", "barcode2", 12.0);
        productDao.insert(pojo2);
        ProductPojo pojo3 = getNewProductPojo(brandPojo3.getId(), "product3", "barcode3", 12.0);
        productDao.insert(pojo3);
        ProductPojo pojo4 = getNewProductPojo(brandPojo2.getId(), "product4", "barcode4", 12.0);
        productDao.insert(pojo4);

        assertEquals(3, productDto.filter(TestObjectUtils.getNewProductSearchForm("brand1", null, null)).size());
        assertEquals(2, productDto.filter(TestObjectUtils.getNewProductSearchForm(null, "category1", null)).size());
        assertEquals(2, productDto.filter(TestObjectUtils.getNewProductSearchForm("brand1", "category2", null)).size());
        assertEquals("barcode3", productDto.filter(TestObjectUtils.getNewProductSearchForm(null, null, "barcode3")).get(0).getBarcode());
    }
}
