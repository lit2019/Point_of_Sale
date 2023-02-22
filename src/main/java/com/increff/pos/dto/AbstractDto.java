package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AbstractDto {
    public void checkNullObject(Object object, String message) throws ApiException {
        if (Objects.isNull(object)) {
            throw new ApiException(message);
        }
    }

}
