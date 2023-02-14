package com.increff.pos.dao;

import com.increff.pos.entity.ProductPojo;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao<ProductPojo> {
    private static final String SELECT_BY_BARCODE_ID = "select p from ProductPojo p where p.barcode IN :barcodes";

    public List<ProductPojo> selectByBarcodes(List<String> barcodes) {
        TypedQuery<ProductPojo> query = createQuery(SELECT_BY_BARCODE_ID);
        if (CollectionUtils.isEmpty(barcodes)) {
            return new ArrayList<>();
        }
        query.setParameter("barcodes", barcodes);
        return getResultList(query);
    }
}
