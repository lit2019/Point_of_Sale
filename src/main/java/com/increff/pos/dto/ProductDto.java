package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.ProductPojo;

public class ProductDto {
    public ProductPojo validate(ProductForm form) throws ApiException {
        if (form==null){
            throw new ApiException("Product form cannot be null");
        }
        if (form.getName()==null || form.getName().trim().equals("")){
            throw new ApiException("Product name cannot be null");
        }

        return convert(form);
    }

    private static ProductPojo convert(ProductForm f) {
        ProductPojo p = new ProductPojo();
        return null;
    }
}
