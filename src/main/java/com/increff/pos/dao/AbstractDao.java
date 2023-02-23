package com.increff.pos.dao;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;


@Repository
@Getter
public abstract class AbstractDao<T> {
    Class<T> clazz;
    @PersistenceContext
    private EntityManager entityManager;

    public AbstractDao() {
        this.clazz = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void insert(T pojo) {
        entityManager.persist(pojo);
    }

    public List<T> selectAll() {
        TypedQuery<T> query = createQuery("select p from " + clazz.getSimpleName() + " p");
        return query.getResultList();
    }

    public T select(Integer id) {
        TypedQuery<T> query = createQuery("select p from " + clazz.getSimpleName() + " p where id=:id");
        query.setParameter("id", id);
        return getSingleResult(query);
    }


    protected List getResultList(TypedQuery query) {
        return query.getResultList();
    }

    protected T getSingleResult(TypedQuery<T> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    protected TypedQuery<T> createQuery(String jpql) {
        return entityManager.createQuery(jpql, clazz);
    }

}