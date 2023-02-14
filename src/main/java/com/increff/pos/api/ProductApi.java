package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ListUtils.checkEmptyList;
import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProductApi extends AbstractApi {

    @Autowired
    private ProductDao dao;

    public List<ProductPojo> get() throws ApiException {
        return dao.selectAll();
    }

    public void update(Integer id, ProductPojo newProductPojo) throws ApiException {
        getCheck(id);
        checkNull(newProductPojo.getName(), "Name cannot be null");
        checkNull(newProductPojo.getMrp(), "mrp cannot be null");

        ProductPojo oldProductPojo = dao.select(id);
        oldProductPojo.setName(newProductPojo.getName());
        oldProductPojo.setMrp(newProductPojo.getMrp());
    }

    public ProductPojo getByBarcode(String barcode) {
        List<ProductPojo> productPojos = dao.selectByMember("barcode", barcode);
        if (productPojos.size() > 0) {
            return productPojos.get(0);
        }
        return null;
    }

    public void add(List<ProductPojo> productPojos) throws ApiException {
        checkEmptyList(productPojos, "productPojos cannot be empty");
        UploadLimit.checkSize(productPojos.size());
        for (ProductPojo productPojo : productPojos) {
            validate(productPojo);
        }
        checkExistingBarcode(productPojos);
        for (ProductPojo productPojo : productPojos)
            dao.insert(productPojo);
    }

    public ProductPojo get(Integer id) {
        return dao.select(id);
    }

    public HashMap<String, ProductPojo> getBarcodeToProductPojoMap(ArrayList<String> barcodes) {
        HashMap<String, ProductPojo> barcodeToProductMap = new HashMap<>();
        List<ProductPojo> productPojos = dao.selectByBarcodes(barcodes);
        productPojos.forEach(pojo -> {
            barcodeToProductMap.put(pojo.getBarcode(), pojo);
        });

        return barcodeToProductMap;
    }

    private void validate(ProductPojo productPojo) throws ApiException {
        checkNull(productPojo.getId(), "id cannot be null");
        checkNull(productPojo.getName(), "Name cannot be null");
        checkNull(productPojo.getBrandCategoryId(), "BrandCategoryId cannot be null");
        checkNull(productPojo.getMrp(), "mrp cannot be null");
        checkNull(productPojo.getBarcode(), "Barcode cannot be null");
    }

    private void getCheck(Integer id) throws ApiException {
        ProductPojo productPojo = dao.select(id);
        checkNull(productPojo, String.format("product with given id : %d does not exist", id));
    }

    private void checkExistingBarcode(List<ProductPojo> pojos) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        pojos.forEach((pojo) -> {
            if (Objects.nonNull(getByBarcode(pojo.getBarcode()))) {
                existingBarcodes.add(pojo.getBarcode());
            }
        });
        checkNonEmptyList(existingBarcodes, "product(s) with barcode(s) already exists : " + existingBarcodes);
    }

}
