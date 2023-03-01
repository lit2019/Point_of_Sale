package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class OrderForm {
    @NotEmpty(message = "order items cannot be empty")
    @Valid
    private List<OrderItemForm> orderItemForms;
}
