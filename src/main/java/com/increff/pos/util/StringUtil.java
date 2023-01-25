package com.increff.pos.util;

import com.increff.pos.api.ApiException;

import java.util.Objects;

public class StringUtil {
    public static String normaliseText(String text) {
        return text.toLowerCase().trim();
    }

    public static void checkEmptyString(String string, String message) throws ApiException {
        if (Objects.isNull(string) || string.trim().equals("")) {
            throw new ApiException(message);
        }
    }
}
