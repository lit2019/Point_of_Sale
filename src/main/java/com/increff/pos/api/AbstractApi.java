package com.increff.pos.api;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;


@Service
@Transactional(rollbackOn = Exception.class)
public abstract class AbstractApi {

    //TODO remove this

    protected void checkNull(Object object, String message) throws ApiException {
        if (Objects.isNull(object)) {
            throw new ApiException(message);
        }
    }
}
