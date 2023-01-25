package com.increff.pos.dao;

import com.increff.pos.entity.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


@Repository
public class BrandDao extends AbstractDao<BrandPojo> {

    private static final String SELECT_BY_NAME_CATEGORY = "select p from BrandPojo p where (p.name=:name and p.category=:category)";

    @PersistenceContext
    private EntityManager em;


    public BrandPojo select(String name, String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_NAME_CATEGORY);
        query.setParameter("name", name);
        query.setParameter("category", category);
//        TODO:make method for getSingleResult
        return getSingleResult(query);
    }

    //    TODO: use getbymember

    public List<String> selectDistinctBrands() {
//        TODO: change clazz to pojo
        TypedQuery<String> query = getEntityManager().createQuery("select DISTINCT(c.name) from BrandPojo c", String.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<String>();
        }
    }
}
