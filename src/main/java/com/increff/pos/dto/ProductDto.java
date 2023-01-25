package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductUpsertForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class ProductDto extends AbstractDto {

    @Autowired
    private ProductApi productApi;
    @Autowired
    private BrandApi brandApi;


    //    TODO: add pagination (end priority)
    public List<ProductData> get() throws ApiException {
        return convert(productApi.get());
    }

    public ProductData get(Integer id) throws ApiException {
        return convert(productApi.get(id));
    }
//TODO move public methods to top

    public void update(Integer id, ProductUpsertForm productForm) throws ApiException {
        checkNull(productForm);
        normalize(productForm);
        productApi.update(id, convert(productForm));
    }

    public void add(List<ProductUpsertForm> forms) throws ApiException {
        for (Integer i = 0; i < forms.size(); i++) {
            ProductUpsertForm productForm = forms.get(i);
            normalize(productForm);
            checkNull(productForm);
        }

        checkDuplicateBarcode(forms);
        checkExistingBarcode(forms);
        checkBrandCategory(forms);

        List<ProductPojo> productPojos = new ArrayList<>();
        for (ProductUpsertForm form : forms) {
            productPojos.add(convert(form));
        }
        productApi.add(productPojos);
    }

    private void checkBrandCategory(List<ProductUpsertForm> forms) throws ApiException {
        ArrayList<String> erroneousCombinations = new ArrayList<>();
        for (ProductUpsertForm productForm : forms) {
            if (Objects.isNull(brandApi.getByNameCategory(productForm.getBrandName(), productForm.getCategory()))) {
                erroneousCombinations.add(productForm.getBrandName() + "_" + productForm.getCategory());
            }
        }
        checkNonEmptyList(erroneousCombinations, "combinations for brand name and category dose not exist : " + erroneousCombinations.toString());
    }

    private void checkExistingBarcode(List<ProductUpsertForm> forms) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        for (ProductUpsertForm form : forms) {
            if (Objects.nonNull(productApi.getByBarcode(form.getBarcode()))) {
                existingBarcodes.add(form.getBarcode());
            }
        }
        checkNonEmptyList(existingBarcodes, "barcode already exists : " + existingBarcodes.toString());
    }

    private void checkDuplicateBarcode(List<ProductUpsertForm> forms) throws ApiException {
        HashSet<String> barcodeSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        for (ProductUpsertForm productForm : forms) {
            String key = productForm.getBarcode();
            if (barcodeSet.contains(key)) {
                duplicates.add(key);
            } else {
                barcodeSet.add(key);
            }
        }
        checkNonEmptyList(duplicates, "duplicate barcodes exist : " + duplicates.toString());
    }

    private void checkNull(ProductUpsertForm productForm) throws ApiException {
        checkNullObject(productForm, "Product form cannot be null");
        StringUtil.checkEmptyString(productForm.getProductName(), "Product name cannot be empty");
        StringUtil.checkEmptyString(productForm.getBarcode(), "Product barcode cannot be empty");
        checkNullObject(productForm.getMrp(), "Product mrp cannot be null");
        if (productForm.getMrp() < 0) {
            throw new ApiException("Product MRP cannot be negative:" + productForm.getMrp());
        }
        StringUtil.checkEmptyString(productForm.getCategory(), "Product category cannot be empty");
    }

    private List<ProductData> convert(List<ProductPojo> productPojos) throws ApiException {
        List<ProductData> productDatas = new ArrayList<>();

        for (ProductPojo productPojo : productPojos) {
            productDatas.add(convert(productPojo));
        }
        return productDatas;
    }

    private ProductData convert(ProductPojo productPojo) throws ApiException {
        BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
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
        BrandPojo brandPojo = brandApi.getByNameCategory(productForm.getBrandName(), productForm.getCategory());
        Integer brandCategoryId = brandPojo.getId();
        ProductPojo pojo = new ProductPojo();
        pojo.setName(productForm.getProductName());
        pojo.setBrandCategoryId(brandCategoryId);
        pojo.setMrp(productForm.getMrp());
        pojo.setBarcode(productForm.getBarcode());
        return pojo;
    }
}
