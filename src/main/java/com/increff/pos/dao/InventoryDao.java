package com.increff.pos.dao;

import com.increff.pos.entity.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {
    @PersistenceContext
    private EntityManager em;

    public List<InventoryPojo> get(String name) {
        TypedQuery<InventoryPojo> query = em.createQuery("select p from InventoryPojo p where name=:name", InventoryPojo.class);
        query.setParameter("name", name);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<String> selectDistinctInventorys() {
        TypedQuery<String> query = em().createQuery("select DISTINCT(c.name) from " + clazz.getSimpleName() + " c", String.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<String>();
        }
    }
}
