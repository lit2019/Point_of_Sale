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

@Service
public class InventoryDto extends AbstractDto {

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

        checkDuplicateBarcode(forms);
        checkBarcode(forms);
        checkExistingBarcode(forms);

        ArrayList<InventoryPojo> inventoryPojos = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            inventoryPojos.add(convert(form));
        }

        inventoryApi.add(inventoryPojos);
    }

    private void checkExistingBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            if (Objects.nonNull(inventoryApi.getByBarcode(form.getBarcode()))) {
                existingBarcodes.add(form.getBarcode());
            }
        }
        checkNonEmptyList(existingBarcodes, "inventory for given barcode already exists : " + existingBarcodes.toString());
    }

    private void checkBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        String errorMessage = "";

        for (Integer i = 0; i < forms.size(); i++) {
            InventoryUpsertForm inventoryForm = forms.get(i);
            if (Objects.isNull(productApi.getByBarcode(inventoryForm.getBarcode()))) {
                errorMessage += String.format("%s in row %d\n", inventoryForm.getBarcode(), i + 1);
            }
        }
        if (!(errorMessage.equals(""))) {
            throw new ApiException("product with given barcode doesn't exist \n" + errorMessage);
        }
    }

    private void checkDuplicateBarcode(List<InventoryUpsertForm> forms) throws ApiException {
        HashSet<String> barcodeSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        for (InventoryUpsertForm form : forms) {
            String key = form.getBarcode();
            if (barcodeSet.contains(key)) {
                duplicates.add(key);
            } else {
                barcodeSet.add(key);
            }
        }
        checkNonEmptyList(duplicates, "duplicate barcodes exist : " + duplicates.toString());
    }

    public void update(InventoryUpsertForm inventoryForm) throws ApiException {
        normalize(inventoryForm);
        checkNull(inventoryForm);
        inventoryApi.update(productApi.getByBarcode(inventoryForm.getBarcode()).getId(), convert(inventoryForm));
    }

    private void checkNull(InventoryUpsertForm inventoryForm) throws ApiException {
        checkNullObject(inventoryForm, "Inventory form cannot be null");
        StringUtil.checkEmptyString(inventoryForm.getBarcode(), "Inventory barcode cannot be null");
        checkNullObject(inventoryForm.getQuantity(), "Inventory quantity cannot be null");

        if (inventoryForm.getQuantity() < 0) {
            throw new ApiException("Inventory quantity cannot be negative");
        }
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
