package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.model.InventoryAllocationRequest;
import com.increff.pos.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(rollbackOn = Exception.class)
public class InventoryApi extends AbstractApi {

    @Autowired
    private InventoryDao dao;

    public void add(List<InventoryPojo> inventoryPojos) throws ApiException {
        UploadLimit.checkSize(inventoryPojos.size());
        ListUtils.checkEmptyList(inventoryPojos, "inventory pojos cannot be empty");
        for (InventoryPojo inventoryPojo : inventoryPojos) {
            validate(inventoryPojo);
            dao.insert(inventoryPojo);
        }
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    public void update(Integer id, InventoryPojo inventoryPojo) throws ApiException {
        validate(inventoryPojo);
        InventoryPojo exInventoryPojo = getCheck(id);
        exInventoryPojo.setQuantity(inventoryPojo.getQuantity());
    }

    public InventoryPojo getCheck(Integer id) throws ApiException {
        InventoryPojo pojo = dao.select(id);
        if (Objects.isNull(pojo)) {
            throw new ApiException("Inventory with given ID does not exist, id: " + id);
        }
        return pojo;
    }

    public void allocateInventory(ArrayList<InventoryAllocationRequest> requests, Map<Integer, String> productIdToBarcode) throws ApiException {
        ListUtils.checkEmptyList(requests, "Allocation Requests cannot be empty");
        for (InventoryAllocationRequest inventoryAllocationRequest : requests)
            validate(inventoryAllocationRequest);

        checkInsufficientInventory(requests, productIdToBarcode);

        requests.forEach(request -> {
            InventoryPojo inventoryPojo = get(request.getProductId());
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - request.getQuantityToReduce());
        });
    }

    public InventoryPojo get(Integer id) {
        return dao.select(id);
    }

    private void validate(InventoryAllocationRequest request) throws ApiException {
        checkNull(request.getProductId(), "Product Id cannot be null");
        checkNull(request.getQuantityToReduce(), "Quantity To Reduce cannot be null");
    }

    private void validate(InventoryPojo pojo) throws ApiException {
        checkNull(pojo.getQuantity(), "quantity cannot be null");
        checkNull(pojo.getProductId(), "Product Id cannot be null");
    }

    private void checkInsufficientInventory(ArrayList<InventoryAllocationRequest> requests, Map<Integer, String> productIdToBarcode) throws ApiException {
        ArrayList<String> barcodes = new ArrayList<>();
        requests.forEach(request -> {
            if (request.getQuantityToReduce() > get(request.getProductId()).getQuantity()) {
                barcodes.add(productIdToBarcode.get(request.getProductId()));
            }
        });

        ListUtils.checkNonEmptyList(barcodes, "Insufficient Inventory for barcodes : " + barcodes);
    }
}
