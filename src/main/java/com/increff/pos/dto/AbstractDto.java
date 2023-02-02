package com.increff.pos.dto;

import com.increff.pos.api.ApiException;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

public class AbstractDto<T> {
    Class<T> clazz;

    public AbstractDto() {
        this.clazz = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void checkNonNullObject(Object object, String message) throws ApiException {
        if (Objects.nonNull(object)) {
            throw new ApiException(message);
        }
    }

    public void checkNullObject(Object object, String message) throws ApiException {
        if (Objects.isNull(object)) {
            throw new ApiException(message);
        }
    }

}
