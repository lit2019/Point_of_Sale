package com.increff.pos.util;

import com.increff.pos.api.ApiException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {

    public static void checkNonEmptyList(List list, String message) throws ApiException {
        if (!CollectionUtils.isEmpty(list)) {
            throw new ApiException(message);
        }
    }

    public static void checkEmptyList(List list, String message) throws ApiException {
        if (CollectionUtils.isEmpty(list)) {
            throw new ApiException(message);
        }
    }

    public static void checkDuplicates(List<String> list, String message) throws ApiException {
        HashSet<String> hashSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        list.forEach(s -> {
            if (hashSet.contains(s)) {
                duplicates.add(s);
            } else {
                hashSet.add(s);
            }
        });

        checkNonEmptyList(duplicates, message + duplicates);
    }

    public static void checkUploadLimit(List list, Integer maxUploadSize) throws ApiException {
        if (list.size() > maxUploadSize) {
            throw new ApiException("upload size cannot be more than " + maxUploadSize);
        }
    }
}
