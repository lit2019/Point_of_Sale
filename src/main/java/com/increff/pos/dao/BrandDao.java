package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;


@Repository
@Transactional
public class BrandDao extends AbstractDao<BrandPojo>{

    private static final String select_by_name_category = "select p from BrandPojo p where (p.name=:name and p.category=:category)";

    @PersistenceContext
    private EntityManager em;


    public BrandPojo select(String name,String category) {
        TypedQuery<BrandPojo> query = getQuery(select_by_name_category);
        query.setParameter("name", name);
        query.setParameter("category", category);
        try{
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public void update(BrandPojo p) {
    }


}
