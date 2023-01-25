package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class OrderForm {
    //    TODO:nonnull to notnull
    @NotEmpty(message = "order items cannot be empty")
    private List<OrderItemForm> orderItemForms;
}
