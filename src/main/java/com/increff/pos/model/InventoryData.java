package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InventoryData extends InventoryUpsertForm {
    @NotNull
    Integer productId;
}
