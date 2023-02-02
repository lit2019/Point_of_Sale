package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductApi extends AbstractApi<ProductPojo> {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private BrandApi brandApi;

    public void add(ProductPojo productPojo) throws ApiException {
        productDao.insert(productPojo);
    }

    public List<ProductPojo> get() throws ApiException {
        return productDao.selectAll();
    }


    public void update(Integer id, ProductPojo newProductPojo) throws ApiException {
        ProductPojo oldProductPojo = productDao.select(id);
        oldProductPojo.setName(newProductPojo.getName());
        oldProductPojo.setBrandCategoryId(newProductPojo.getBrandCategoryId());
        oldProductPojo.setMrp(newProductPojo.getMrp());
        oldProductPojo.setBarcode(newProductPojo.getBarcode());
    }

    public ProductPojo getByBarcode(String barcode) {
        List<ProductPojo> productPojos = productDao.selectByMember("barcode", barcode);
        if (productPojos.size() > 0) {
            return productPojos.get(0);
        }
        return null;
    }

    public void add(List<ProductPojo> productPojos) throws ApiException {

        checkExistingBarcode(productPojos);
        for (ProductPojo productPojo : productPojos) {
            productDao.insert(productPojo);
        }
    }

    private void checkExistingBarcode(List<ProductPojo> pojos) throws ApiException {
        ArrayList<String> existingBarcodes = new ArrayList<>();
        pojos.forEach((pojo) -> {
            if (Objects.nonNull(getByBarcode(pojo.getBarcode()))) {
                existingBarcodes.add(pojo.getBarcode());
            }
        });
        checkNonEmptyList(existingBarcodes, "product(s) with barcode(s) already exists : " + existingBarcodes.toString());
    }


}
