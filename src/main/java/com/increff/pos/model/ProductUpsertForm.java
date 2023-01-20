package com.increff.pos.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ProductUpsertForm {
    @NonNull
    String barcode;

    @NonNull
    String brandName;

    @NonNull
    String category;

    @NonNull
    String productName;

    @NonNull
    Double mrp;

    @NonNull
    Integer id;
}
