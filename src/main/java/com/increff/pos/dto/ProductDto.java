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

import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
public class ProductDto extends AbstractDto<ProductUpsertForm> {

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
        validate(productForm);
        normalize(productForm);
        productApi.update(id, convert(productForm));
    }

    public void add(List<ProductUpsertForm> forms) throws ApiException {
        for (Integer i = 0; i < forms.size(); i++) {
            ProductUpsertForm productForm = forms.get(i);
            normalize(productForm);
            validate(productForm);
        }

        checkDuplicateBarcode(forms);
        checkBrandCategory(forms);

        List<ProductPojo> productPojos = new ArrayList<>();
        for (ProductUpsertForm form : forms) {
            productPojos.add(convert(form));
        }
        productApi.add(productPojos);
    }


    private void checkDuplicateBarcode(List<ProductUpsertForm> forms) throws ApiException {
        HashSet<String> barcodeSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        forms.stream().map(ProductUpsertForm::getBarcode).forEach(key -> {
            if (barcodeSet.contains(key)) {
                duplicates.add(key);
            } else {
                barcodeSet.add(key);
            }
        });
        checkNonEmptyList(duplicates, "duplicate barcodes exist : " + duplicates.toString());
    }


    private void checkBrandCategory(List<ProductUpsertForm> forms) throws ApiException {
        ArrayList<String> erroneousCombinations = new ArrayList<>();
        forms.forEach((form) -> {
            if (Objects.isNull(brandApi.getByNameCategory(form.getBrandName(), form.getCategory()))) {
                erroneousCombinations.add(form.getBrandName() + "_" + form.getCategory());
            }
        });
        checkNonEmptyList(erroneousCombinations, "combinations for brand name and category dose not exist : " + erroneousCombinations.toString());
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
