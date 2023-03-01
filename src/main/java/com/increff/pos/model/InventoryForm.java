package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class InventoryForm {
    @NotBlank(message = "Barcode cannot be blank")
    private String barcode;
    @PositiveOrZero(message = "Quantity must be a positive integer")
    private Integer quantity;
}