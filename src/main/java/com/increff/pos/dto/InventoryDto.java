package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.BrandApi;
import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryReportData;
import com.increff.pos.model.InventoryUpsertForm;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;
import static com.increff.pos.util.ValidatorUtil.validate;

@Service
public class InventoryDto extends AbstractDto<InventoryUpsertForm> {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;
    @Autowired
    private BrandApi brandApi;

    //    TODO: add pagination (end priority)
    public List<InventoryData> get() throws ApiException {
        return convert(inventoryApi.getAll());
    }

    public InventoryData get(Integer id) throws ApiException {
        return convert(inventoryApi.get(id));
    }
//TODO move public methods to top

    public void add(List<InventoryUpsertForm> forms) throws ApiException {
        for (InventoryUpsertForm form : forms) {
            validate(form);
        }
        normalize(forms);

        checkDuplicateBarcode(forms);
        checkBarcode(forms);
        checkExistingInventory(forms);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            inventoryPojos.add(convert(form));
        }

        inventoryApi.add(inventoryPojos);
    }

    public ArrayList<InventoryReportData> getInventoryReport() throws ApiException {
        List<InventoryData> inventoryDatas = get();
        HashMap<String, Integer> brandCategoryQuantity = new HashMap<>();


        for (InventoryData inventoryData : inventoryDatas) {
            ProductPojo productPojo = productApi.get(inventoryData.getProductId());
            BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
            String brandName = brandPojo.getName();
            String category = brandPojo.getCategory();
            String key = brandName + "_" + category;
            brandCategoryQuantity.put(key, brandCategoryQuantity.getOrDefault(key, 0) + inventoryData.getQuantity());

        }
        ArrayList<InventoryReportData> inventoryReportDatas = new ArrayList<>();

        for (String brandCategory : brandCategoryQuantity.keySet()) {
            String brandName = brandCategory.split("_")[0];
            String category = brandCategory.split("_")[1];
            InventoryReportData reportData = new InventoryReportData();
            reportData.setBrandName(brandName);
            reportData.setCategory(category);
            reportData.setQuantity(brandCategoryQuantity.get(brandCategory));
            inventoryReportDatas.add(reportData);
        }


        return inventoryReportDatas;
    }

    public void update(InventoryUpsertForm inventoryForm) throws ApiException {
        normalize(Collections.singletonList(inventoryForm));
        validate(inventoryForm);
        inventoryApi.update(productApi.getByBarcode(inventoryForm.getBarcode()).getId(), convert(inventoryForm));
    }

    private void checkExistingInventory(List<InventoryUpsertForm> forms) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            if (Objects.nonNull(inventoryApi.getByBarcode(form.getBarcode()))) {
                existingBarcodes.add(form.getBarcode());
            }
        }
        checkNonEmptyList(existingBarcodes, "inventory for given barcode already exists\n Erroneous barcodes : " + existingBarcodes.toString());
    }

    private void checkBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        ArrayList<String> nonExistingBarcodes = new ArrayList<>();
        forms.forEach((form) -> {
            if (Objects.isNull(productApi.getByBarcode(form.getBarcode()))) {
                nonExistingBarcodes.add(form.getBarcode());
            }
        });
        checkNonEmptyList(nonExistingBarcodes, "Product with Barcode dose not exist exists\n Erroneous barcodes : " + nonExistingBarcodes.toString());
    }

    private void checkDuplicateBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        ArrayList<String> barcodes = new ArrayList<>();
        forms.forEach((form) -> {
            barcodes.add(form.getBarcode());
        });
        List<String> duplicates = ListUtils.getDuplicates(barcodes);
        checkNonEmptyList(duplicates, "duplicate barcodes exist \n Erroneous barcodes : " + duplicates.toString());
    }

    private List<InventoryData> convert(List<InventoryPojo> inventoryPojos) throws ApiException {
        List<InventoryData> inventoryDatas = new ArrayList<>();

        for (InventoryPojo inventoryPojo : inventoryPojos) {
            inventoryDatas.add(convert(inventoryPojo));
        }
        return inventoryDatas;
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

    private void normalize(List<InventoryUpsertForm> forms) {
        for (InventoryUpsertForm form : forms) {
            form.setBarcode(StringUtil.normaliseText(form.getBarcode()));
        }
    }

    private InventoryPojo convert(InventoryUpsertForm inventoryForm) throws ApiException {
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
