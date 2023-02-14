package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ProductUpdateForm {
    @NotBlank(message = "product name cannot be blank")
    String productName;

    @PositiveOrZero(message = "quantity must be a non negative integer")
    Double mrp;
}
