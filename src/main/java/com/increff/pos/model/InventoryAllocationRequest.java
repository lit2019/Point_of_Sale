package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAllocationRequest {
    private Integer productId;
    private Integer quantityToReduce;
}
