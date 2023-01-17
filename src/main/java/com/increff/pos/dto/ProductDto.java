package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import com.increff.pos.api.ProductService;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDto {

    @Autowired
    private ProductService productService;

    public void validate(ProductForm form) throws ApiException {
        checkeNull(form);
        productService.add(form);
    }


    public void validateUpdate(Integer id, ProductForm form) throws ApiException {
        checkeNull(form);
        productService.update(id,form);
    }

    private void checkeNull(ProductForm form) throws ApiException {
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

    public List<ProductData> get() throws ApiException {
        return productService.get();
    }

    public ProductData get(Integer id) throws ApiException {
        return productService.get(id);
    }
}
