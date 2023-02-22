package com.increff.pos.util;

import com.increff.pos.api.ApiException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationUtil {
    public static <T> void validate(Object form) throws ApiException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate((T) form);
        for (ConstraintViolation<T> violation : violations) {
            throw new ApiException(violation.getMessage());
        }
    }
}
