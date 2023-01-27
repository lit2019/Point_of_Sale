package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class OrderItemForm {
    @NotBlank(message = "barcode cannot be null")
    private String barcode;
    @Positive(message = "quantity must be positive integer")
    private Integer quantity;
}
