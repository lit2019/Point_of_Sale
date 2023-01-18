package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import com.increff.pos.api.ProductService;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductDto {

    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    public void validate(ProductForm productForm) throws ApiException {
        checkNull(productForm);
        productService.add(convert(productForm));
    }

    public List<ProductData> get() throws ApiException {
        return convert(productService.get());
    }

    public ProductData get(Integer id) throws ApiException {
        return convert(productService.get(id));
    }

    private ProductPojo convert(ProductForm productForm) throws ApiException {
        BrandPojo brandPojo = brandService.getByBrandNameCategory(productForm.getBrandName(),productForm.getCategory());
        if (brandPojo==null){
            throw new ApiException("given brand:"+productForm.getBrandName()+"and category:"+productForm.getCategory()+"doesn't exist");
        }
        Integer brandCategoryId = brandPojo.getId();
        ProductPojo pojo = new ProductPojo();
        pojo.setName(productForm.getProductName());
        pojo.setBrandCategoryId(brandCategoryId);
        pojo.setMrp(productForm.getMrp());
        pojo.setBarcode(productForm.getBarcode());
        return pojo;
    }

    public void update(Integer id, ProductForm productForm) throws ApiException {
        checkNull(productForm);
        productService.update(id, convert(productForm));
    }

    private void checkNull(ProductForm productForm) throws ApiException {
        if (productForm==null){
            throw new ApiException("Product form cannot be null");
        }
        if (productForm.getProductName()==null || productForm.getProductName().trim().equals("")){
            throw new ApiException("Product name cannot be null");
        }
        if (productForm.getBarcode()==null || productForm.getBarcode().trim().equals("")){
            throw new ApiException("Product barcode cannot be null");
        }
        if (productForm.getMrp()==null){
            throw new ApiException("Product mrp cannot be null");
        }
        if (productForm.getCategory()==null || productForm.getCategory().trim().equals("")){
            throw new ApiException("Product category cannot be null");
        }
    }

    private List<ProductData> convert(List<ProductPojo> productPojos) throws ApiException {
        List<ProductData> productDatas = new ArrayList<>();

        for(ProductPojo productPojo:productPojos){
            productDatas.add(convert(productPojo));
        }
        return productDatas;
    }

    private ProductData convert(ProductPojo productPojo) throws ApiException {
        BrandPojo brandPojo = brandService.get(productPojo.getBrandCategoryId());
        ProductData productData = new ProductData();
        productData.setId(productPojo.getId());
        productData.setMrp(productPojo.getMrp());
        productData.setBrandName(brandPojo.getName());
        productData.setBarcode(productPojo.getBarcode());
        productData.setProductName(productPojo.getName());
        productData.setCategory(brandPojo.getCategory());
        return productData;
    }
}
