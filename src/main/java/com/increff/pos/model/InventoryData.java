package com.increff.pos.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class InventoryData extends InventoryUpsertForm {

    @NonNull
    Integer productId;
}
