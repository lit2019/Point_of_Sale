package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {
    @NotNull
    private Integer id;
    @NotNull
    private String productName;
}
