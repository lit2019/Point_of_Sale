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
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;
import static com.increff.pos.util.ValidatorUtil.validate;

@Service
public class InventoryDto extends AbstractDto {
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private BrandApi brandApi;

    //      TODO: add pagination (end priority)
    public List<InventoryData> getAll() throws ApiException {
        return convert(inventoryApi.getAll());
    }

    public InventoryData getAll(Integer id) throws ApiException {
        return convert(inventoryApi.getCheck(id));
    }
//          TODO move public methods to top

    public void add(List<InventoryForm> forms) throws ApiException {
        ListUtils.checkEmptyList(forms, "Inventory Forms cannot be empty");
        for (InventoryForm form : forms) {
            validate(form);
            NormalizationUtil.normalize(form);
        }

        checkDuplicateBarcode(forms);
        checkBarcode(forms);
        checkExistingInventory(forms);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        for (InventoryForm form : forms) {
            inventoryPojos.add(convert(form));
        }

        inventoryApi.add(inventoryPojos);
    }

    public ArrayList<InventoryReportData> getInventoryReport() throws ApiException {
        List<InventoryData> inventoryDataList = getAll();
        HashMap<Integer, Integer> brandCategoryQuantity = new HashMap<>();


        for (InventoryData inventoryData : inventoryDataList) {
            ProductPojo productPojo = productApi.get(inventoryData.getProductId());
            BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
            Integer key = brandPojo.getId();
            brandCategoryQuantity.put(key, brandCategoryQuantity.getOrDefault(key, 0) + inventoryData.getQuantity());
        }
        ArrayList<InventoryReportData> inventoryReportDataList = new ArrayList<>();

        for (Integer brandCategoryId : brandCategoryQuantity.keySet()) {
            BrandPojo brandPojo = brandApi.get(brandCategoryId);
            String brandName = brandPojo.getName();
            String category = brandPojo.getCategory();
            InventoryReportData reportData = new InventoryReportData();
            reportData.setBrandName(brandName);
            reportData.setCategory(category);
            reportData.setQuantity(brandCategoryQuantity.get(brandCategoryId));
            inventoryReportDataList.add(reportData);
        }

        return inventoryReportDataList;
    }

    public void update(InventoryForm inventoryForm) throws ApiException {
        validate(inventoryForm);
        NormalizationUtil.normalize(inventoryForm);
        inventoryApi.update(productApi.getByBarcode(inventoryForm.getBarcode()).getId(), convert(inventoryForm));
    }

    private void checkExistingInventory(List<InventoryForm> forms) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();

        ArrayList<String> barcodes = new ArrayList<>();
        forms.forEach(form -> {
            barcodes.add(form.getBarcode());
        });

        HashMap<String, ProductPojo> barcodeToProductMap = productApi.getBarcodeToProductPojoMap(barcodes);


        for (InventoryForm form : forms) {
            if (Objects.nonNull(inventoryApi.get(barcodeToProductMap.get(form.getBarcode()).getId()))) {
                existingBarcodes.add(form.getBarcode());
            }
        }
        checkNonEmptyList(existingBarcodes, "inventory for given barcode already exists\n Erroneous barcodes : " + existingBarcodes);
    }

    private void checkBarcode(List<InventoryForm> forms) throws ApiException {
        ArrayList<String> nonExistingBarcodes = new ArrayList<>();
        forms.forEach((form) -> {
            if (Objects.isNull(productApi.getByBarcode(form.getBarcode()))) {
                nonExistingBarcodes.add(form.getBarcode());
            }
        });
        checkNonEmptyList(nonExistingBarcodes, "Product with Barcode dose not exist exists\n Erroneous barcodes : " + nonExistingBarcodes);
    }

    private void checkDuplicateBarcode(List<InventoryForm> forms) throws ApiException {
        ArrayList<String> barcodes = new ArrayList<>();
        forms.forEach((form) -> {
            barcodes.add(form.getBarcode());
        });
        ListUtils.checkDuplicates(barcodes, "duplicate barcodes exist \n Erroneous barcodes : ");
    }

    private InventoryData convert(InventoryPojo inventoryPojo) throws ApiException {
        ProductPojo productPojo = productApi.get(inventoryPojo.getProductId());
        InventoryData inventoryData = new InventoryData();
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        inventoryData.setBarcode(productPojo.getBarcode());
        inventoryData.setProductId(productPojo.getId());
        inventoryData.setProductName(productPojo.getName());
        return inventoryData;
    }

    private List<InventoryData> convert(List<InventoryPojo> inventoryPojos) throws ApiException {
        List<InventoryData> inventoryDataList = new ArrayList<>();

        for (InventoryPojo inventoryPojo : inventoryPojos) {
            inventoryDataList.add(convert(inventoryPojo));
        }
        return inventoryDataList;
    }

    private InventoryPojo convert(InventoryForm inventoryForm) throws ApiException {
        ProductPojo productPojo = productApi.getByBarcode(inventoryForm.getBarcode());
        if (Objects.isNull(productPojo)) {
            throw new ApiException(String.format("given barcode:%s doesn't exist", inventoryForm.getBarcode()));
        }
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }
}
