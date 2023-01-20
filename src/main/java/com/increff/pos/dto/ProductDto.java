package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandService;
import com.increff.pos.api.ProductService;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductUpsertForm;
import com.increff.pos.util.StringUtil;
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

    public void add(ProductUpsertForm productForm) throws ApiException {
        normalize(productForm);
        checkNull(productForm);
        if(Objects.nonNull(productService.getByBarcode(productForm.getBarcode()))){
            throw new ApiException("barcode already exists \n"+productForm.getBarcode()+"\n");
        }
        productService.add(convert(productForm));
    }

//    TODO: add pagination (end priority)
    public List<ProductData> get() throws ApiException {
        return convert(productService.get());
    }

    public ProductData get(Integer id) throws ApiException {
        return convert(productService.get(id));
    }
//TODO move public methods to top

    public void add(List<ProductUpsertForm> forms) throws ApiException {

        String errorMessage = "";
        for (Integer i = 0; i < forms.size(); i++) {
            ProductUpsertForm productForm1 = forms.get(i);
            for (Integer j = i+1; j < forms.size(); j++) {
                ProductUpsertForm productForm2 = forms.get(j);
                if(productForm1.getBarcode().equals(productForm2.getBarcode())){
                    errorMessage+=String.format("%s in rows %d and %d\n",productForm1.getBarcode(),i+1,j+1);
                }
            }
        }
        if(!(errorMessage.equals(""))){
            throw new ApiException("duplicate barcode exist \n"+errorMessage);
        }

        errorMessage = "";

        for (Integer i = 0; i < forms.size(); i++) {
            ProductUpsertForm productForm = forms.get(i);
            if(Objects.nonNull(productService.getByBarcode(productForm.getBarcode()))){
                errorMessage+=String.format("%s in row %d\n",productForm.getBarcode(),i+1);

            }
        }
        if(!(errorMessage.equals(""))){
            throw new ApiException("barcode already exists \n"+errorMessage);
        }

        for(ProductUpsertForm form:forms){
            normalize(form);
            checkNull(form);
            add(form);
        }
    }
    public void update(ProductUpsertForm productForm) throws ApiException {
        checkNull(productForm);
        productService.update(productForm.getId(), convert(productForm));
    }

    private void checkNull(ProductUpsertForm productForm) throws ApiException {
        if (Objects.isNull(productForm)){
            throw new ApiException("Product form cannot be null");
        }
        if (Objects.isNull(productForm.getProductName()) || productForm.getProductName().equals("")){
            throw new ApiException("Product name cannot be null");
        }
        if (Objects.isNull(productForm.getBarcode()) || productForm.getBarcode().equals("")){
            throw new ApiException("Product barcode cannot be null");
        }
        if (Objects.isNull(productForm.getMrp())){
            throw new ApiException("Product mrp cannot be null");
        }
        if (Objects.isNull(productForm.getCategory()) || productForm.getCategory().equals("")){
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

    private void normalize(ProductUpsertForm productForm) {
        productForm.setBarcode(StringUtil.normaliseText(productForm.getBarcode()));
        productForm.setProductName(StringUtil.normaliseText(productForm.getProductName()));
        productForm.setBrandName(StringUtil.normaliseText(productForm.getBrandName()));
        productForm.setCategory(StringUtil.normaliseText(productForm.getCategory()));
    }

    private ProductPojo convert(ProductUpsertForm productForm) throws ApiException {
        BrandPojo brandPojo = brandService.getByBrandNameCategory(productForm.getBrandName(),productForm.getCategory());
        if (Objects.isNull(brandPojo)){
            throw new ApiException(String.format("given brand:%s and category:%s doesn't exist",productForm.getBrandName(),productForm.getCategory()));
        }
        Integer brandCategoryId = brandPojo.getId();
        ProductPojo pojo = new ProductPojo();
        pojo.setName(productForm.getProductName());
        pojo.setBrandCategoryId(brandCategoryId);
        pojo.setMrp(productForm.getMrp());
        pojo.setBarcode(productForm.getBarcode());
        return pojo;
    }
}
