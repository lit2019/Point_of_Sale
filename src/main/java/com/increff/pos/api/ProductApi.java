package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;

import static com.increff.pos.util.ListUtils.checkEmptyList;
import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProductApi extends AbstractApi {

    @Autowired
    private ProductDao dao;

    public void add(List<ProductPojo> productPojos) throws ApiException {
        checkEmptyList(productPojos, "ProductPojos cannot be empty");
        for (ProductPojo productPojo : productPojos)
            validate(productPojo);

        checkExistingBarcode(productPojos);
        for (ProductPojo productPojo : productPojos)
            dao.insert(productPojo);
    }

    public void update(Integer id, ProductPojo newProductPojo) throws ApiException {
        checkNull(newProductPojo.getName(), "Name cannot be null");
        checkNull(newProductPojo.getMrp(), "Mrp cannot be null");

        ProductPojo oldProductPojo = getCheck(id);

        oldProductPojo.setName(newProductPojo.getName());
        oldProductPojo.setMrp(newProductPojo.getMrp());
    }

    public ProductPojo getUniqueByBarcode(String barcode) throws ApiException {
        checkNull(barcode, "Barcode cannot be null");
        return dao.selectBarcode(barcode);
    }

    public ProductPojo get(Integer id) {
        return dao.select(id);
    }

    public static Map<String, ProductPojo> getBarcodeToProductPojoMap(List<ProductPojo> pojos) {
        HashMap<String, ProductPojo> barcodeToProductMap = new HashMap<>();
        pojos.forEach(pojo -> {
            barcodeToProductMap.put(pojo.getBarcode(), pojo);
        });

        return barcodeToProductMap;
    }

    public List<ProductPojo> getByBarcodes(List<String> barcodes) {
        if (CollectionUtils.isEmpty(barcodes)) {
            return new ArrayList<>();
        }
        return dao.selectByBarcodes(barcodes);
    }

    public List<ProductPojo> getByFilter(List<Integer> brandIds, Integer pageNo, Integer pageSize) {
        if (CollectionUtils.isEmpty(brandIds))
            return new ArrayList<>();

        return dao.selectByFilter(brandIds, pageNo, pageSize);
    }

    public List<ProductPojo> getByBrandIds(List<Integer> brandIds) {
        if (CollectionUtils.isEmpty(brandIds))
            return new ArrayList<>();

        return dao.selectByBrandIds(brandIds);
    }


    public ProductPojo getCheck(Integer id) throws ApiException {
        ProductPojo productPojo = dao.select(id);
        checkNull(productPojo, String.format("Product with given id : %d does not exist", id));
        return productPojo;
    }

    public void checkExistingBarcode(List<ProductPojo> pojos) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        for (ProductPojo pojo : pojos) {
            if (Objects.nonNull(getUniqueByBarcode(pojo.getBarcode()))) {
                existingBarcodes.add(pojo.getBarcode());
            }
        }
        checkNonEmptyList(existingBarcodes, "Product(s) with barcode(s) already exists : " + existingBarcodes);
    }

    public void checkIfBarcodesExist(List<String> barcodes) throws ApiException {
        Map<String, ProductPojo> barcodeToProductMap = getBarcodeToProductPojoMap(getByBarcodes(barcodes));
        ArrayList<String> nonExistingBarcodes = new ArrayList<>();
        for (String barcode : barcodes)
            if (!barcodeToProductMap.containsKey(barcode))
                nonExistingBarcodes.add(barcode);

        ListUtils.checkNonEmptyList(nonExistingBarcodes, "Products do not exist for Barcodes : " + barcodes);
    }

    private void validate(ProductPojo productPojo) throws ApiException {
        checkNull(productPojo.getName(), "Name cannot be null");
        checkNull(productPojo.getBrandCategoryId(), "BrandCategoryId cannot be null");
        checkNull(productPojo.getMrp(), "Mrp cannot be null");
        checkNull(productPojo.getBarcode(), "Barcode cannot be null");
    }
}