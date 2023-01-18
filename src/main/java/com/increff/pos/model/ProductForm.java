package com.increff.pos.model;

import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    @Nullable
    String barcode;

    @Nullable
    String brandName;

    @Nullable
    String category;

    @Nullable
    String productName;

    @Nullable
    Double mrp;
}
