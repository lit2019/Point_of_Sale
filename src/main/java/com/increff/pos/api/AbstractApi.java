package com.increff.pos.api;

import com.increff.pos.dao.AbstractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;


@Service
@Transactional(rollbackOn = ApiException.class)
public abstract class AbstractApi<T> {
    Class<T> clazz;
    @Autowired
    private AbstractDao<T> dao;

    public AbstractApi() {
        this.clazz = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T get(Integer id) throws ApiException {
        return dao.select(id);
    }

}
