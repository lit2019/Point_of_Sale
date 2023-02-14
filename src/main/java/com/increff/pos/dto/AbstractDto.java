package com.increff.pos.dto;

import com.increff.pos.api.ApiException;

import java.util.Objects;

public class AbstractDto {


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
