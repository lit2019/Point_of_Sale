package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InventoryReportData {
    @NotNull
    private String brandName;
    @NotNull
    private String category;
    @NotNull
    private Integer quantity;
}
