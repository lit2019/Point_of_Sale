package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public void add(ProductPojo productPojo) throws ApiException {
        productDao.insert(productPojo);
    }


    public ProductPojo get(int id) throws ApiException {
        return productDao.select(id);
    }

    public List<ProductPojo> get() throws ApiException {
        List<ProductPojo> productPojos = productDao.selectAll();

        return productPojos;
    }



    public void update(Integer id, ProductPojo newProductPojo) throws ApiException {
        ProductPojo oldProductPojo = productDao.select(id);
        if (!oldProductPojo.getBarcode().equals(newProductPojo.getBarcode())){
//        barcode is changed
            if(productDao.selectByMember("barcode",newProductPojo.getBarcode()).size()>0){
                throw new ApiException(String.format("given barcode:%s Already Exists",newProductPojo.getBarcode()));
            }
        }
        oldProductPojo.setName(newProductPojo.getName());
        oldProductPojo.setBrandCategoryId(newProductPojo.getBrandCategoryId());
        oldProductPojo.setMrp(newProductPojo.getMrp());
        oldProductPojo.setBarcode(newProductPojo.getBarcode());

    }

    public ProductPojo getByBarcode(String barcode) {
        List<ProductPojo> productPojos = productDao.selectByMember("barcode",barcode);
        if(productPojos.size()>0){
            return productPojos.get(0);
        }
        return null;
    }
}
