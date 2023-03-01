package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.InventoryAllocationRequest;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.util.TestObjectUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.increff.pos.util.TestObjectUtils.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InventoryApiTest extends AbstractUnitTest {
    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProductDao productDao;

    private List<ProductPojo> productPojos;

    @Before
    public void setup() {
        productPojos = getNewProductPojoList();
        productPojos.forEach(productDao::insert);
    }

    @Test
    public void testUpsertWithEmptyList() {
        try {
            inventoryApi.upsert(new ArrayList<>());
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Inventory pojos cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testUpsert() throws ApiException {
        List<InventoryPojo> inventoryPojos = getNewInventoryPojoList(productPojos);
        inventoryApi.upsert(inventoryPojos);

        List<Integer> productIds = new ArrayList<>();
        for (ProductPojo productPojo : productPojos)
            productIds.add(productPojo.getId());

        assertEquals(inventoryPojos.size(), inventoryDao.selectByProductIds(productIds).size());
    }

    @Test
    public void testUpsertExistingId() throws ApiException {
        List<InventoryPojo> inventoryPojos = getNewInventoryPojoList(productPojos);
        inventoryApi.upsert(inventoryPojos);
        inventoryPojos.get(0).setQuantity(100);
        inventoryApi.upsert(inventoryPojos);
        assertEquals((Integer) 100, inventoryDao.select(inventoryPojos.get(0).getProductId()).getQuantity());
    }

    @Test
    public void testGetCheck() throws ApiException {
        InventoryPojo inventoryPojo = getNewInventoryPojo(productPojos.get(0).getId(), 100);
        inventoryDao.insert(inventoryPojo);

        try {
            inventoryApi.getCheck(0);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Inventory with given ID does not exist, id: 0", e.getMessage());
        }
        InventoryPojo inventoryPojo2 = inventoryApi.getCheck(inventoryPojo.getProductId());
        assertEqualsInventoryPojo(inventoryPojo, inventoryPojo2);
    }

    @Test
    public void testAllocateInventoryWithEmptyList() {
        try {
            inventoryApi.allocateInventory(new ArrayList<>(), new HashMap<>());
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Allocation Requests cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAllocateInventoryWithInvalidRequest() {
        try {
            inventoryApi.allocateInventory(Collections.singletonList(getNewInventoryAllocationRequest(null, 1)), new HashMap<>());
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Product Id cannot be null", e.getMessage());
        }

        try {
            inventoryApi.allocateInventory(Collections.singletonList(getNewInventoryAllocationRequest(productPojos.get(0).getId(), -1)), new HashMap<>());
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("Quantity to Reduce cannot be less than 0", e.getMessage());
        }
    }


    @Test
    public void testAllocateInventory() throws ApiException {
        InventoryPojo inventoryPojo1 = getNewInventoryPojo(productPojos.get(0).getId(), 100);
        inventoryDao.insert(inventoryPojo1);
        InventoryPojo inventoryPojo2 = getNewInventoryPojo(productPojos.get(1).getId(), 100);
        inventoryDao.insert(inventoryPojo2);

        HashMap<Integer, String> productIdToBarcode = new HashMap<>();
        for (ProductPojo pojo : productPojos)
            productIdToBarcode.put(pojo.getId(), pojo.getBarcode());

        List<InventoryAllocationRequest> requests = new ArrayList<>();
        requests.add(TestObjectUtils.getNewInventoryAllocationRequest(productPojos.get(0).getId(), 10));
        requests.add(TestObjectUtils.getNewInventoryAllocationRequest(productPojos.get(1).getId(), 30));
        inventoryApi.allocateInventory(requests, productIdToBarcode);

        assertEquals((Integer) 90, inventoryDao.select(inventoryPojo1.getProductId()).getQuantity());
        assertEquals((Integer) 70, inventoryDao.select(inventoryPojo2.getProductId()).getQuantity());
    }

    @Test
    public void testGetInventories() {
        List<InventoryPojo> inventoryPojos = getNewInventoryPojoList(productPojos);
        inventoryPojos.forEach(inventoryDao::insert);

        List<InventoryPojo> inventoryPojos2 = inventoryApi.getInventories(1, 2);
        assertNotNull(inventoryPojos2);
        assertEquals(2, inventoryPojos2.size());

        inventoryPojos2 = inventoryApi.getInventories(1, 3);
        assertNotNull(inventoryPojos2);
        assertEquals(3, inventoryPojos2.size());
    }


    private void assertEqualsInventoryPojo(InventoryPojo inventoryPojo, InventoryPojo inventoryPojo2) {
        assertEquals(inventoryPojo.getProductId(), inventoryPojo2.getProductId());
        assertEquals(inventoryPojo.getQuantity(), inventoryPojo2.getQuantity());
    }
}
