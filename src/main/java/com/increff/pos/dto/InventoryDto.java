package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.InventorySearchForm;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
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

    //      TODO: add pagination (end priority)
//    todo remove
    public List<InventoryData> getInventories(String barcode) throws ApiException {
        if (Objects.nonNull(barcode)) {
            Integer productId = productApi.getUniqueByBarcode(barcode).getId();
            return Collections.singletonList(get(productId));
        }
        return convert(inventoryApi.getAll());
    }

    public InventoryData get(Integer id) throws ApiException {
        return convert(inventoryApi.get(id));
    }
//          TODO move public methods to top

    public void add(List<InventoryForm> forms) throws ApiException {
        ListUtils.checkEmptyList(forms, "Inventory Forms cannot be empty");
        ListUtils.checkUploadLimit(forms, MAX_UPLOAD_SIZE);

        ArrayList<String> barcodes = new ArrayList<>();
        for (InventoryForm form : forms) {
            validate(form);
            NormalizationUtil.normalize(form);
            barcodes.add(form.getBarcode());
        }
//        TODO check for capital in error messages
        ListUtils.checkDuplicates(barcodes, "duplicate barcodes provided, \n Erroneous barcodes : ");

//        todo move to the above loop
//        todo rename to checkIfBarcodesExist
        productApi.checkIfBarcodesExist(barcodes);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        for (InventoryForm form : forms)
            inventoryPojos.add(convert(form));

        inventoryApi.upsert(inventoryPojos);
    }

    public List<InventoryReportData> getInventoryReport(InventorySearchForm searchForm) throws ApiException {
        List<InventoryData> inventoryDataList = getInventories(null);
        HashMap<Integer, Integer> brandIdToQuantity = new HashMap<>();

        for (InventoryData inventoryData : inventoryDataList) {
            ProductPojo productPojo = productApi.get(inventoryData.getProductId());
            BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
            Integer key = brandPojo.getId();
            brandIdToQuantity.put(key, brandIdToQuantity.getOrDefault(key, 0) + inventoryData.getQuantity());
        }
        ArrayList<InventoryReportData> inventoryReportDataList = new ArrayList<>();

        for (Integer brandCategoryId : brandIdToQuantity.keySet()) {
            BrandPojo brandPojo = brandApi.get(brandCategoryId);
            if (!isAllowed(searchForm, brandPojo)) {
                continue;
            }
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

    private boolean isAllowed(InventorySearchForm searchForm, BrandPojo brandPojo) {
        if (Objects.nonNull(searchForm.getBrandName()) && !searchForm.getBrandName().equals(brandPojo.getName())) {
            return false;
        }
        if (Objects.nonNull(searchForm.getCategory()) && !searchForm.getCategory().equals(brandPojo.getCategory())) {
            return false;
        }
        return true;
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
