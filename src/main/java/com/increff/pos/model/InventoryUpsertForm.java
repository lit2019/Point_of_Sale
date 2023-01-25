package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class InventoryUpsertForm {
    @NotBlank(message = "barcode cannot be null")
    private String barcode;
    @PositiveOrZero(message = "quantity must be a positive integer")
    private Integer quantity;
}