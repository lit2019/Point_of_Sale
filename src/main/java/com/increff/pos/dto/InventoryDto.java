package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.InventoryService;
import com.increff.pos.api.ProductService;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryUpsertForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public void add(InventoryUpsertForm inventoryForm) throws ApiException {
        normalize(inventoryForm);
        checkNull(inventoryForm);
        inventoryService.add(convert(inventoryForm));
    }

    //    TODO: add pagination (end priority)
    public List<InventoryData> get() throws ApiException {
        return convert(inventoryService.getAll());
    }

    public InventoryData get(Integer id) throws ApiException {
        return convert(inventoryService.get(id));
    }
//TODO move public methods to top

    public void add(List<InventoryUpsertForm> forms) throws ApiException {

        String errorMessage = "";
        for (Integer i = 0; i < forms.size(); i++) {
            InventoryUpsertForm inventoryForm1 = forms.get(i);
            for (Integer j = i + 1; j < forms.size(); j++) {
                InventoryUpsertForm inventoryForm2 = forms.get(j);
                if (inventoryForm1.getBarcode().equals(inventoryForm2.getBarcode())) {
                    errorMessage += String.format("%s in rows %d and %d\n", inventoryForm1.getBarcode(), i + 1, j + 1);
                }
            }
        }
        if (!(errorMessage.equals(""))) {
            throw new ApiException("duplicate barcode exist \n" + errorMessage);
        }

        errorMessage = "";

        for (Integer i = 0; i < forms.size(); i++) {
            InventoryUpsertForm inventoryForm = forms.get(i);
            if (Objects.isNull(productService.getByBarcode(inventoryForm.getBarcode()))) {
                errorMessage += String.format("%s in row %d\n", inventoryForm.getBarcode(), i + 1);
            }
        }
        if (forms.size() > 1 && !(errorMessage.equals(""))) {
            throw new ApiException("product with given barcode doesn't exist \n" + errorMessage);
        }

        errorMessage = "";

        for (Integer i = 0; i < forms.size(); i++) {
            InventoryUpsertForm inventoryForm = forms.get(i);
            if (Objects.nonNull(inventoryService.getByBarcode(inventoryForm.getBarcode()))) {
                errorMessage += String.format("%s in row %d\n", inventoryForm.getBarcode(), i + 1);
            }
        }
        if (forms.size() > 1 && !(errorMessage.equals(""))) {
            throw new ApiException("inventory for given barcode already exist \n" + errorMessage);
        }

        for (InventoryUpsertForm form : forms) {
            add(form);
        }
    }

    public void update(InventoryUpsertForm inventoryForm) throws ApiException {
        normalize(inventoryForm);
        checkNull(inventoryForm);
        inventoryService.update(inventoryForm.getId(), convert(inventoryForm));
    }

    private void checkNull(InventoryUpsertForm inventoryForm) throws ApiException {
        if (Objects.isNull(inventoryForm)) {
            throw new ApiException("Inventory form cannot be null");
        }
        if (Objects.isNull(inventoryForm.getBarcode()) || inventoryForm.getBarcode().equals("")) {
            throw new ApiException("Inventory barcode cannot be null");
        }
        if (Objects.isNull(inventoryForm.getQuantity())) {
            throw new ApiException("Inventory quantity cannot be null");
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
        ProductPojo productPojo = productService.get(inventoryPojo.getId());
        InventoryData inventoryData = new InventoryData();
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        inventoryData.setBarcode(productPojo.getBarcode());
        inventoryData.setId(productPojo.getId());
        return inventoryData;
    }

    private void normalize(InventoryUpsertForm inventoryForm) {
        inventoryForm.setBarcode(StringUtil.normaliseText(inventoryForm.getBarcode()));
    }

    private InventoryPojo convert(InventoryUpsertForm inventoryForm) throws ApiException {
        ProductPojo productPojo = productService.getByBarcode(inventoryForm.getBarcode());
        if (Objects.isNull(productPojo)) {
            throw new ApiException(String.format("given barcode:%s doesn't exist", inventoryForm.getBarcode()));
        }
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setId(productPojo.getId());
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }
}
