package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    String barcode;

    String brandName;

    String category;

    String productName;

    Double mrp;
}
