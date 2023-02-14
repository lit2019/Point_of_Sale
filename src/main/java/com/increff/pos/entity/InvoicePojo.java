package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@Entity
@Table(name = "pos_invoices")
public class InvoicePojo extends BaseEntity {
    @Id
    @Column(name = "order_id")
    private Integer orderId;


    //TODO rename to invoice_url
    @Column(name = "invoice_url", nullable = false)
    private String invoiceUrl;
}
