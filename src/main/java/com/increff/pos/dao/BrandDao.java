package com.increff.pos.dao;

import com.increff.pos.entity.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public class BrandDao extends AbstractDao<BrandPojo>{

    private static final String SELECT_BY_NAME_CATEGORY = "select p from BrandPojo p where (p.name=:name and p.category=:category)";

    @PersistenceContext
    private EntityManager em;


    public BrandPojo select(String name, String category) {
        TypedQuery<BrandPojo> query = getQuery(SELECT_BY_NAME_CATEGORY);
        query.setParameter("name", name);
        query.setParameter("category", category);
        try{
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public List<BrandPojo> get(String name) {
        TypedQuery<BrandPojo> query = em.createQuery("select p from BrandPojo p where name=:name",BrandPojo.class);
        query.setParameter("name", name);
        try{
            return (List<BrandPojo>) query.getResultList();
        }catch (NoResultException e){
            return null;
        }
    }
}
