package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderData extends OrderForm {
    @NotNull
    private Integer id;
    @NotNull
    private String createdAt;
}
