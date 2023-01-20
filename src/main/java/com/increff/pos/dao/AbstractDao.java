package com.increff.pos.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


@Repository
public abstract class AbstractDao<T> {
    Class<T> clazz;
    @PersistenceContext
    private EntityManager entityManager;

    public AbstractDao() {
        this.clazz = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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
        return getSingleResult(query);
    }


    public List<T> selectByMember(String member, String value) {
        TypedQuery<T> query = getQuery("select p from " + clazz.getSimpleName()+ " p where "+member+"=:value");
        query.setParameter("value", value);
        return getResultList(query);
    }

    private List<T> getResultList(TypedQuery<T> query) {
        try{
            return (List<T>) query.getResultList();
        }catch (NoResultException e){
            return new ArrayList<T>();
        }
    }

    private T getSingleResult(TypedQuery<T> query) {
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