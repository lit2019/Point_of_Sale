package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchForm extends PageRequestForm {
    private String brandName;
    private String category;
    private String barcode;
}
