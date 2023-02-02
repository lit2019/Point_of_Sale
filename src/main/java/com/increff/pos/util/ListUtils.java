package com.increff.pos.util;

import com.increff.pos.api.ApiException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {

    public static void checkNonEmptyList(List list, String message) throws ApiException {
        if (!CollectionUtils.isEmpty(list)) { //TODO:use colleectionutils
            throw new ApiException(message);
        }
    }


    public static ArrayList<String> getDuplicates(ArrayList<String> list) {
        HashSet<String> hashSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        list.forEach(s -> {
            if (hashSet.contains(s)) {
                duplicates.add(s);
            } else {
                hashSet.add(s);
            }
        });

        return duplicates;
    }
}
