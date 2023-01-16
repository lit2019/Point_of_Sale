package com.increff.pos.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.util.List;


@Repository
public abstract class AbstractDao<T> {
    Class<T> clazz;
    @PersistenceContext
    private EntityManager entityManager;

    public AbstractDao() {
        this.clazz = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    public final void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public void insert(T pojo) {
        entityManager.persist(pojo);
    }

    public List<T> selectAll() {
        TypedQuery<T> query = getQuery("select p from " + clazz.getSimpleName()+ " p");
        return (List<T>) query.getResultList();
    }

    public T select(Integer id) {
        TypedQuery<T> query = getQuery("select p from " + clazz.getSimpleName()+ " p where id=:id");
        query.setParameter("id", id);
        try{
            return (T) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    protected TypedQuery<T> getQuery(String jpql) {
        return entityManager.createQuery(jpql, clazz);
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

}