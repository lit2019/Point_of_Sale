package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.model.InventoryAllocationRequest;
import com.increff.pos.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackOn = Exception.class)
public class InventoryApi extends AbstractApi {

    @Autowired
    private InventoryDao dao;

    public void upsert(List<InventoryPojo> inventoryPojos) throws ApiException {
//    TODO should be seperate for each api
        ListUtils.checkEmptyList(inventoryPojos, "Inventory pojos cannot be empty");
        ArrayList<Integer> productIds = new ArrayList<>();
        for (InventoryPojo inventoryPojo : inventoryPojos) {
            validate(inventoryPojo);
            productIds.add(inventoryPojo.getProductId());
        }

//            TODO make map for productIdToInventoryPojo
        Map<Integer, InventoryPojo> productIdToInventoryPojo = getProductIdToInventoryPojoMap(getByProductIds(productIds));
        for (InventoryPojo inventoryPojo : inventoryPojos) {
            if (productIdToInventoryPojo.containsKey(inventoryPojo.getProductId()))
                update(inventoryPojo.getProductId(), inventoryPojo);
            else
                dao.insert(inventoryPojo);
        }
    }


    public void update(Integer id, InventoryPojo newPojo) throws ApiException {
        validate(newPojo);
        InventoryPojo existingPojo = getCheck(id);
        existingPojo.setQuantity(newPojo.getQuantity());
    }

    public InventoryPojo getCheck(Integer id) throws ApiException {
        InventoryPojo pojo = dao.select(id);
//        TODO use checknull
        checkNull(pojo, "Inventory with given ID does not exist, id: " + id);
        return pojo;
    }

    public void allocateInventory(List<InventoryAllocationRequest> requests, Map<Integer, String> productIdToBarcode) throws ApiException {
        ListUtils.checkEmptyList(requests, "Allocation Requests cannot be empty");
        for (InventoryAllocationRequest inventoryAllocationRequest : requests)
            validate(inventoryAllocationRequest);

        checkIfInventoryPresent(requests, productIdToBarcode);

        requests.forEach(request -> {
            InventoryPojo inventoryPojo = get(request.getProductId());
            inventoryPojo.setQuantity(inventoryPojo.getQuantity() - request.getQuantityToReduce());
        });
    }

    public InventoryPojo get(Integer id) {
        return dao.select(id);
    }

    public static Map<Integer, InventoryPojo> getProductIdToInventoryPojoMap(List<InventoryPojo> pojos) {
        HashMap<Integer, InventoryPojo> productIdToProductMap = new HashMap<>();
        pojos.forEach(pojo -> {
            productIdToProductMap.put(pojo.getProductId(), pojo);
        });

        return productIdToProductMap;
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    private List<InventoryPojo> getByProductIds(List<Integer> productIds) {
        return dao.selectByProductIds(productIds);
    }

    private void validate(InventoryAllocationRequest request) throws ApiException {
        checkNull(request.getProductId(), "Product Id cannot be null");
//        TODO also check for negative values here
        checkNull(request.getQuantityToReduce(), "Quantity to reduce cannot be null");
        if (request.getQuantityToReduce() <= 0) {
            throw new ApiException("Quantity to Reduce cannot be less than 0");
        }
    }

    private void validate(InventoryPojo pojo) throws ApiException {
//        TODO check capital
        checkNull(pojo.getQuantity(), "Quantity cannot be null");
        checkNull(pojo.getProductId(), "Product Id cannot be null");
    }

    private void checkIfInventoryPresent(List<InventoryAllocationRequest> requests, Map<Integer, String> productIdToBarcode) throws ApiException {
        ArrayList<String> barcodes = new ArrayList<>();
//        TODO use map of productIdTOInventorypojo here

        ArrayList<Integer> productIds = new ArrayList<>();
        for (InventoryAllocationRequest request : requests)
            productIds.add(request.getProductId());

        Map<Integer, InventoryPojo> productIdToInventoryPojo = getProductIdToInventoryPojoMap(getByProductIds(productIds));
        requests.forEach(request -> {
            if ((!productIdToInventoryPojo.containsKey(request.getProductId())) || (request.getQuantityToReduce() > productIdToInventoryPojo.get(request.getProductId()).getQuantity())) {
                barcodes.add(productIdToBarcode.get(request.getProductId()));
            }
        });

        ListUtils.checkNonEmptyList(barcodes, "Insufficient Inventory for barcodes : " + barcodes);
    }
}
