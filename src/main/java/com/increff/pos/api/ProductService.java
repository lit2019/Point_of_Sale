package com.increff.pos.api;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Autowired
    private BrandService brandService;

    public void add(ProductForm form) throws ApiException {
        ProductPojo pojo = convert(form);
        normalize(pojo);
        dao.insert(pojo);
    }

    protected  void normalize(ProductPojo p) {
        p.setName(p.getName().toLowerCase().trim());
    }

    public ProductData get(int id) throws ApiException {
        return convert(getCheck(id));
    }


    public List<ProductData> get() throws ApiException {
        List<ProductPojo> pojos = dao.selectAll();
        List<ProductData> datas = new ArrayList<>();

        for(ProductPojo pojo:pojos){
            datas.add(convert(pojo));
        }
        return datas;
    }

    private ProductPojo getCheck(int id) {
        return dao.select(id);
    }
    private ProductData convert(ProductPojo pojo) throws ApiException {
        BrandPojo brandPojo = brandService.get(pojo.getBrandCategoryId());
        ProductData data = new ProductData();
        data.setId(pojo.getId());
        data.setMrp(pojo.getMrp());
        data.setBrandName(brandPojo.getName());
        data.setBarcode(pojo.getBarcode());
        data.setProductName(pojo.getName());
        data.setCategory(brandPojo.getCategory());
        return data;
    }

    private ProductPojo convert(ProductForm form) throws ApiException {
        BrandPojo brandPojo = brandService.get(form.getBrandName(),form.getCategory());
        if (brandPojo==null){
            throw new ApiException("given combination of brand and category doesn't exist");
        }
        Integer brandCategoryId = brandPojo.getId();
        ProductPojo pojo = new ProductPojo();
        pojo.setName(form.getProductName());
        pojo.setBrandCategoryId(brandCategoryId);
        pojo.setMrp(form.getMrp());
        pojo.setBarcode(form.getBarcode());
        return pojo;
    }
}
