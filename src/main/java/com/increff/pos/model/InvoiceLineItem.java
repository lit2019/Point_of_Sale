package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceLineItem {

    private Integer Sno;
    private String Barcode;
    private String ProductName;
    private Integer Quantity;
    private Double UnitPrice;
    private Double Total;

    public Double getTotal() {
        return Quantity * UnitPrice;
    }

}
