package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.ProductSearchForm;
import com.increff.pos.model.ProductUpdateForm;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ValidationUtil.validate;

@Service
public class ProductDto {

    @Autowired
    private ProductApi productApi;
    @Autowired
    private BrandApi brandApi;
    private static final Integer MAX_UPLOAD_SIZE = 5000;
    //    TODO: add pagination (end priority)

    public ProductData get(Integer id) throws ApiException {
        return convert(productApi.get(id));
    }

    public void add(List<ProductForm> forms) throws ApiException {
        ListUtils.checkEmptyList(forms, "Product Forms cannot be empty");
        for (Integer i = 0; i < forms.size(); i++) {
            ProductForm productForm = forms.get(i);
            validate(productForm);
            NormalizationUtil.normalize(productForm);
        }

        checkDuplicateBarcode(forms);
        checkBrandCategory(forms);

        List<ProductPojo> productPojos = new ArrayList<>();
        for (ProductForm form : forms)
            productPojos.add(convert(form));

        ListUtils.checkUploadLimit(productPojos, MAX_UPLOAD_SIZE);
        productApi.add(productPojos);
    }
    //TODO move public methods to top

    public void update(Integer id, ProductUpdateForm updateForm) throws ApiException {
        validate(updateForm);
        NormalizationUtil.normalize(updateForm);
        productApi.update(id, convert(updateForm));
    }

    public List<ProductData> filter(ProductSearchForm searchForm) throws ApiException {
        if (Objects.nonNull(searchForm.getBarcode())) {
            return convert(productApi.getByBarcodes(Collections.singletonList(searchForm.getBarcode())));
        }

        NormalizationUtil.normalize(searchForm);
        List<BrandPojo> brandPojos = brandApi.getByFilter(searchForm.getBrandName(), searchForm.getCategory());

        ArrayList<Integer> brandIds = new ArrayList<>();
        brandPojos.forEach(brandPojo -> brandIds.add(brandPojo.getId()));
        return convert(productApi.getByBrandIds(brandIds));
    }


    private void checkDuplicateBarcode(List<ProductForm> forms) throws ApiException {
        ArrayList<String> barcodes = new ArrayList<>();
        forms.forEach((form) -> {
            barcodes.add(form.getBarcode());
        });
        ListUtils.checkDuplicates(barcodes, "duplicate barcodes exist \n Erroneous barcodes : ");
    }

    private ProductPojo convert(ProductUpdateForm updateForm) {
        ProductPojo newPojo = new ProductPojo();
        newPojo.setMrp(updateForm.getMrp());
        newPojo.setName(updateForm.getProductName());
        return newPojo;
    }


    //    cannot be moved to ProductApi since brand name and category cannot be accessed with productPojo

    private void checkBrandCategory(List<ProductForm> forms) throws ApiException {
        ArrayList<BrandPojo> brandPojos = new ArrayList<>();
        forms.forEach((form) -> {
            BrandPojo brandPojo = new BrandPojo();
            brandPojo.setCategory(form.getCategory());
            brandPojo.setName(form.getBrandName());
            brandPojos.add(brandPojo);
        });

        brandApi.checkNonExistingBrandCategory(brandPojos);
    }

    private List<ProductData> convert(List<ProductPojo> productPojos) throws ApiException {
        List<ProductData> productDataList = new ArrayList<>();

        for (ProductPojo productPojo : productPojos) {
            productDataList.add(convert(productPojo));
        }
        return productDataList;
    }

    private ProductData convert(ProductPojo productPojo) throws ApiException {
        BrandPojo brandPojo = brandApi.getCheck(productPojo.getBrandCategoryId());
        ProductData productData = new ProductData();
        productData.setId(productPojo.getId());
        productData.setMrp(productPojo.getMrp());
        productData.setBrandName(brandPojo.getName());
        productData.setBarcode(productPojo.getBarcode());
        productData.setProductName(productPojo.getName());
        productData.setCategory(brandPojo.getCategory());
        return productData;
    }


    private ProductPojo convert(ProductForm productForm) throws ApiException {
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
