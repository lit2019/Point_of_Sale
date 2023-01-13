package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;


@Repository
public class BrandDao {

    private static final String select_id = "select p from BrandPojo p where id=:id";
    private static final String select_all = "select p from BrandPojo p";
    private static final String select_by_name_category = "select p from BrandPojo p where name=:name and category=:category";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(BrandPojo p) {
        em.persist(p);
    }


    @Transactional
    public BrandPojo select(int id) {
        TypedQuery<BrandPojo> query = getQuery(select_id);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Transactional
    public BrandPojo select(String name,String category) {
        TypedQuery<BrandPojo> query = getQuery(select_by_name_category);
        query.setParameter("name", name);
        query.setParameter("category", category);

        return query.getSingleResult();
    }

    @Transactional
    public List<BrandPojo> selectAll() {
        TypedQuery<BrandPojo> query = getQuery(select_all);
        return query.getResultList();
    }

    public void update(BrandPojo p) {
    }

    @Transactional
    TypedQuery<BrandPojo> getQuery(String jpql) {
        return em.createQuery(jpql, BrandPojo.class);
    }

}
