package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InventoryService {

    @Autowired
    private InventoryDao dao;
    @Autowired
    private ProductService productService;

    public void add(InventoryPojo inventoryPojo) throws ApiException {
        dao.insert(inventoryPojo);
    }

    public InventoryPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    public List<InventoryPojo> getByInventoryName(String name) {
        return dao.selectByMember("name", name);
    }

    public void update(Integer id, InventoryPojo inventoryPojo) throws ApiException {
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

    public InventoryPojo getByBarcode(String barcode) throws ApiException {
        ProductPojo productPojo = productService.getByBarcode(barcode);
        return dao.select(productPojo.getId());
    }
}
