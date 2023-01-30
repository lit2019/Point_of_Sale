package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItem {

    private Integer sno;
    private String barcode;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double total;

    public Double getTotal() {
        return quantity * unitPrice;
    }

}
