package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Autowired
    private BrandService brandService;

    public void add(ProductPojo pojo) throws ApiException {
        normalize(pojo);
        dao.insert(pojo);
    }

    protected  void normalize(ProductPojo p) {
        p.setName(p.getName().toLowerCase().trim());
    }

    public ProductPojo get(int id) throws ApiException {
        return dao.select(id);
    }

    public List<ProductPojo> get() throws ApiException {
        List<ProductPojo> pojos = dao.selectAll();

        return pojos;
    }

    public void update(Integer id, ProductPojo newProductPojo) throws ApiException {
        ProductPojo oldProductPojo = dao.select(id);
        if (oldProductPojo.getBarcode()!=newProductPojo.getBarcode()){
//        barcode is changed
            if(dao.selectByMember("barcode",newProductPojo.getBarcode()).size()>0){
                throw new ApiException("given barcode:"+newProductPojo.getBarcode()+" Already Exists");
            }
        }
        oldProductPojo.setName(newProductPojo.getName());
        oldProductPojo.setBrandCategoryId(newProductPojo.getBrandCategoryId());
        oldProductPojo.setMrp(newProductPojo.getMrp());
        oldProductPojo.setBarcode(newProductPojo.getBarcode());

    }
}
