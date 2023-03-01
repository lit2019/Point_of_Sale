package com.increff.pos.dao;

import com.increff.pos.entity.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao<ProductPojo> {
    private static final String SELECT_BY_BARCODES = "select p from ProductPojo p where p.barcode IN :barcodes";
    private static final String SELECT_BY_BRAND_IDS = "select p from ProductPojo p where p.brandCategoryId IN :brandIds order by p.createdAt desc";


    public List<ProductPojo> selectByBarcodes(List<String> barcodes) {
        TypedQuery<ProductPojo> query = createQuery(SELECT_BY_BARCODES);

        query.setParameter("barcodes", barcodes);
        return getResultList(query);
    }

    public List<ProductPojo> selectByBrandIds(List<Integer> brandIds) {
        TypedQuery<ProductPojo> query = createQuery(SELECT_BY_BRAND_IDS);
        query.setParameter("brandIds", brandIds);
        return getResultList(query);
    }

    public List<ProductPojo> selectByFilter(List<Integer> brandIds, Integer pageNo, Integer pageSize) {
        TypedQuery<ProductPojo> query = createQuery(SELECT_BY_BRAND_IDS)
                .setMaxResults(pageSize)
                .setFirstResult((pageNo - 1) * pageSize);
        query.setParameter("brandIds", brandIds);
        return getResultList(query);
    }

    public ProductPojo selectBarcode(String barcode) {
        TypedQuery<ProductPojo> query = createQuery(SELECT_BY_BARCODES);

        query.setParameter("barcodes", Collections.singletonList(barcode));
        return getSingleResult(query);
    }

}
