package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class InvoiceData {
    @NotNull
    private Integer invoiceNumber;
    @NotNull
    private String invoiceDate;
    @NotNull
    private String invoiceTime;
    @NotNull
    private List<InvoiceItem> lineItems;
}
