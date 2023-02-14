package com.increff.pos.utils;

import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;

public class TestObjectUtils {
    public static BrandPojo getNewBrandPojo(String name, String category) {
        BrandPojo pojo = new BrandPojo();
        pojo.setName(name);
        pojo.setCategory(category);
        return pojo;
    }

    public static ProductPojo getNewProductPojo(Integer brandCategoryId, String name, String barcode, Double mrp) {
        ProductPojo pojo = new ProductPojo();
        pojo.setName(name);
        pojo.setBarcode(barcode);
        pojo.setMrp(mrp);
        pojo.setBrandCategoryId(brandCategoryId);
        return pojo;
    }
}
