package com.increff.pos.model;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpsertForm {
    @NonNull
    private String barcode;
    @NonNull
    private Integer quantity;
    @NonNull
    private Integer id;
}