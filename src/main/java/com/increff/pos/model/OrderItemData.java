package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {
    @NotNull
    private Integer id;
    @PositiveOrZero(message = "quantity must be a non negative integer")
    private Double mrp;
    @NotBlank(message = "product name cannot be null")
    private String productName;
}
