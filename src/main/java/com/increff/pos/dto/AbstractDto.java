package com.increff.pos.dto;

import com.increff.pos.api.ApiException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Set;

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

    protected void validate(T form) throws ApiException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        for (ConstraintViolation<T> violation : violations) {
            throw new ApiException(violation.getMessage());
        }
    }

}
