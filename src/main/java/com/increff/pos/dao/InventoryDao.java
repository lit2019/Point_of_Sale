package com.increff.pos.dao;

import com.increff.pos.entity.InventoryPojo;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {
    private static final String SELECT_BY_PRODUCT_IDS = "select p from InventoryPojo p where p.productId IN :productIds";

    public List<InventoryPojo> selectByProductIds(List<Integer> productIds) {
        TypedQuery<InventoryPojo> query = createQuery(SELECT_BY_PRODUCT_IDS);
        if (CollectionUtils.isEmpty(productIds)) {
            return new ArrayList<>();
        }
        query.setParameter("productIds", productIds);
        return getResultList(query);

    }
}
