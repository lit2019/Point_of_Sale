package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesData {
    private Integer quantity;
    private Double revenue;
    private String brandName;
    private String category;

    public SalesData(Integer quantity, Double revenue, String brandName, String category) {
        this.quantity = quantity;
        this.revenue = revenue;
        this.brandName = brandName;
        this.category = category;
    }
}
