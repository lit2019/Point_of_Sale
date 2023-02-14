package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ProductForm {
    @NotNull
    String barcode;

    @NotBlank(message = "Brand Name cannot be blank")
    String brandName;

    @NotBlank(message = "Category cannot be blank")
    String category;

    @NotBlank(message = "Product Name cannot be blank")
    String productName;

    @PositiveOrZero(message = "Quantity must be a non negative integer")
    Double mrp;

}
