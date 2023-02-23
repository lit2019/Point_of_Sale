package com.increff.pos.dao;

import com.increff.pos.entity.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public class BrandDao extends AbstractDao<BrandPojo> {

    private static final String SELECT_BY_NAME_CATEGORY = "select p from BrandPojo p where (:name is null or p.name=:name) " +
            "and (:category is null or p.category=:category)";
    private static final String SELECT_DISTINCT_BRAND_NAMES = "select DISTINCT(c.name) from BrandPojo c";

    public List<BrandPojo> select(String name, String category) {
        TypedQuery<BrandPojo> query = createQuery(SELECT_BY_NAME_CATEGORY);
        query.setParameter("name", name);
        query.setParameter("category", category);
//        TODO:make method for getSingleResult
        return getResultList(query);
    }

    //    TODO: use getbymember

    //TODO rename the method
    public List<String> selectDistinctBrandNames() {
//        TODO: change clazz to pojo
        TypedQuery<String> query = getEntityManager().createQuery(SELECT_DISTINCT_BRAND_NAMES, String.class);
        return getResultList(query);
    }

}
