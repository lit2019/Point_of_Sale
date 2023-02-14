package com.increff.pos.util;

import com.increff.pos.model.*;

import java.util.List;
import java.util.Objects;

public class NormalizationUtil {

    public static void normalize(BrandForm form) {
        form.setName(StringUtil.normaliseText(form.getName()));
        form.setCategory(StringUtil.normaliseText(form.getCategory()));
    }

    public static void normalize(BrandSearchForm searchForm) {
        if (Objects.nonNull(searchForm.getCategory())) {
            searchForm.setCategory(StringUtil.normaliseText(searchForm.getCategory()));
        }
        if (Objects.nonNull(searchForm.getName())) {
            searchForm.setName(StringUtil.normaliseText(searchForm.getName()));
        }
    }


    public static void normalize(InventoryForm form) {
        form.setBarcode(StringUtil.normaliseText(form.getBarcode()));
    }

    public static void normalize(ProductForm form) {
        form.setBarcode(StringUtil.normaliseText(form.getBarcode()));
        form.setProductName(StringUtil.normaliseText(form.getProductName()));
        form.setBrandName(StringUtil.normaliseText(form.getBrandName()));
        form.setCategory(StringUtil.normaliseText(form.getCategory()));
    }

    public static void normalize(ProductUpdateForm form) {
        form.setProductName(StringUtil.normaliseText(form.getProductName()));
    }

    public static void normalize(OrderForm orderForm) {
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();

        orderItemsList.forEach((orderItemForm) -> {
            orderItemForm.setBarcode(StringUtil.normaliseText(orderItemForm.getBarcode()));
        });
    }

    public static void normalize(SalesFilterForm filterForm) {
        if (Objects.nonNull(filterForm.getBrandName())) {
            filterForm.setBrandName(StringUtil.normaliseText(filterForm.getBrandName()));
        }
        if (Objects.nonNull(filterForm.getCategory())) {
            filterForm.setCategory(StringUtil.normaliseText(filterForm.getCategory()));
        }
    }

}
