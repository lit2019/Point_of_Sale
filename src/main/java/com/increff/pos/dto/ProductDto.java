package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import com.increff.pos.model.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDto {

    @Autowired
    private BrandService brandService;

    public void validate(ProductForm form) throws ApiException {
        if (form==null){
            throw new ApiException("Product form cannot be null");
        }
        if (form.getProductName()==null || form.getProductName().trim().equals("")){
            throw new ApiException("Product name cannot be null");
        }
        if (form.getBarcode()==null || form.getBarcode().trim().equals("")){
            throw new ApiException("Product barcode cannot be null");
        }
        if (form.getMrp()==null){
            throw new ApiException("Product mrp cannot be null");
        }
        if (form.getCategory()==null || form.getCategory().trim().equals("")){
            throw new ApiException("Product category cannot be null");
        }
    }


}
