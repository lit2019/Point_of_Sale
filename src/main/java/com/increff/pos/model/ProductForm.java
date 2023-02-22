package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ProductForm {
    @NotBlank(message = "Barcode cannot be blank")
    String barcode;

    @NotBlank(message = "Brand name cannot be blank")
    String brandName;

    @NotBlank(message = "Category cannot be blank")
    String category;

    @NotBlank(message = "Product name cannot be blank")
    String productName;

    @Positive(message = "Quantity must be Positive")
    Double mrp;

}
