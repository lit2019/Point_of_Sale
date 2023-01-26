package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.InventoryApi;
import com.increff.pos.api.ProductApi;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryUpsertForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
public class InventoryDto extends AbstractDto<InventoryUpsertForm> {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private ProductApi productApi;

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

        checkDuplicateBarcode(forms);
        checkBarcode(forms);
        checkExistingInventory(forms);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            inventoryPojos.add(convert(form));
        }

        inventoryApi.add(inventoryPojos);
    }

    private void checkExistingInventory(List<InventoryUpsertForm> forms) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            if (Objects.nonNull(inventoryApi.getByBarcode(form.getBarcode()))) {
                existingBarcodes.add(form.getBarcode());
            }
        }
        checkNonEmptyList(existingBarcodes, "inventory for given barcode already exists : " + existingBarcodes.toString());
    }

    private void checkBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        ArrayList<String> nonExistingBarcodes = new ArrayList<>();
        forms.forEach((form) -> {
            if (Objects.isNull(productApi.getByBarcode(form.getBarcode()))) {
                nonExistingBarcodes.add(form.getBarcode());
            }
        });
        checkNonEmptyList(nonExistingBarcodes, "Product with Barcode dose not exist exists : " + nonExistingBarcodes.toString());
    }

    private void checkDuplicateBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        HashSet<String> barcodeSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        forms.forEach((form) -> {
            String key = form.getBarcode();
            if (barcodeSet.contains(key)) {
                duplicates.add(key);
            } else {
                barcodeSet.add(key);
            }
        });
        checkNonEmptyList(duplicates, "duplicate barcodes exist : " + duplicates.toString());
    }

    public void update(InventoryUpsertForm inventoryForm) throws ApiException {
        normalize(inventoryForm);
        validate(inventoryForm);
        inventoryApi.update(productApi.getByBarcode(inventoryForm.getBarcode()).getId(), convert(inventoryForm));
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
        return inventoryData;
    }

    private void normalize(InventoryUpsertForm inventoryForm) {
        inventoryForm.setBarcode(StringUtil.normaliseText(inventoryForm.getBarcode()));
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
