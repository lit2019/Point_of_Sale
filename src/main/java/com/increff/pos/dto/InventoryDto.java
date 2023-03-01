package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.*;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.increff.pos.util.ValidationUtil.validate;

@Service
public class InventoryDto {
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private BrandApi brandApi;
    private static final Integer MAX_UPLOAD_SIZE = 5000;

    public List<InventoryData> getInventoryByBarcode(String barcode) throws ApiException {
        StringUtil.checkEmptyString(barcode, "Barcode cannot be null");

        ProductPojo productPojo = productApi.getUniqueByBarcode(barcode);
        if (Objects.isNull(productPojo)) return new ArrayList<>();

        InventoryData inventoryData = get(productPojo.getId());
        if (Objects.isNull(inventoryData)) return new ArrayList<>();

        return Collections.singletonList(inventoryData);
    }

    public List<InventoryData> getInventoriesByBrandIds(List<Integer> brandIds) {
        List<ProductPojo> productPojos = productApi.getByBrandIds(brandIds);

        List<Integer> productIds = new ArrayList<>();
        productPojos.forEach(productPojo -> productIds.add(productPojo.getId()));

        return convert(inventoryApi.getByProductIds(productIds));
    }

    public InventoryData get(Integer id) throws ApiException {
        return convert(inventoryApi.get(id));
    }

    public void add(List<InventoryForm> forms) throws ApiException {
        ListUtils.checkEmptyList(forms, "Inventory Forms cannot be empty");
        ListUtils.checkUploadLimit(forms, MAX_UPLOAD_SIZE);

        ArrayList<String> barcodes = new ArrayList<>();
        for (InventoryForm form : forms) {
            validate(form);
            NormalizationUtil.normalize(form);
            barcodes.add(form.getBarcode());
        }
        ListUtils.checkDuplicates(barcodes, "duplicate barcodes provided, \n Erroneous barcodes : ");

        productApi.checkIfBarcodesExist(barcodes);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        for (InventoryForm form : forms)
            inventoryPojos.add(convert(form));

        inventoryApi.upsert(inventoryPojos);
    }

    public List<InventoryReportData> getInventoryReport(InventorySearchForm searchForm) {

        List<BrandPojo> brandPojos = brandApi.getByFilter(searchForm.getBrandName(), searchForm.getCategory());
        ArrayList<Integer> brandIds = new ArrayList<>();
        brandPojos.forEach(brandPojo -> brandIds.add(brandPojo.getId()));

        List<InventoryData> inventoryDataList = getInventoriesByBrandIds(brandIds);

        HashMap<Integer, Integer> brandIdToQuantity = new HashMap<>();
        for (InventoryData inventoryData : inventoryDataList) {
            ProductPojo productPojo = productApi.get(inventoryData.getProductId());
            Integer key = productPojo.getBrandCategoryId();
            brandIdToQuantity.put(key, brandIdToQuantity.getOrDefault(key, 0) + inventoryData.getQuantity());
        }

        ArrayList<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        for (Integer brandCategoryId : brandIdToQuantity.keySet()) {
            BrandPojo brandPojo = brandApi.get(brandCategoryId);
            InventoryReportData reportData = new InventoryReportData();
            reportData.setBrandName(brandPojo.getName());
            reportData.setCategory(brandPojo.getCategory());
            reportData.setQuantity(brandIdToQuantity.get(brandCategoryId));
            inventoryReportDataList.add(reportData);
        }

        return inventoryReportDataList;
    }

    public void update(InventoryForm inventoryForm) throws ApiException {
        validate(inventoryForm);
        NormalizationUtil.normalize(inventoryForm);
        inventoryApi.update(productApi.getUniqueByBarcode(inventoryForm.getBarcode()).getId(), convert(inventoryForm));
    }

    public List<InventoryData> getInventoriesByPageRequest(PageRequestForm pageRequestForm) {
        return convert(inventoryApi.getInventories(pageRequestForm.getPageNo(), pageRequestForm.getPageSize()));
    }

    private InventoryData convert(InventoryPojo inventoryPojo) {
        ProductPojo productPojo = productApi.get(inventoryPojo.getProductId());
        InventoryData inventoryData = new InventoryData();
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        inventoryData.setBarcode(productPojo.getBarcode());
        inventoryData.setProductId(productPojo.getId());
        inventoryData.setProductName(productPojo.getName());
        return inventoryData;
    }

    private List<InventoryData> convert(List<InventoryPojo> inventoryPojos) {
        List<InventoryData> inventoryDataList = new ArrayList<>();

        for (InventoryPojo inventoryPojo : inventoryPojos) {
            inventoryDataList.add(convert(inventoryPojo));
        }
        return inventoryDataList;
    }

    private InventoryPojo convert(InventoryForm inventoryForm) throws ApiException {
        ProductPojo productPojo = productApi.getUniqueByBarcode(inventoryForm.getBarcode());
        if (Objects.isNull(productPojo))
            throw new ApiException(String.format("given barcode:%s doesn't exist", inventoryForm.getBarcode()));

        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }
}
