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
    private Integer orderId;

    @Column(name = "invoice_link", nullable = false)
    private String invoiceLink;
}
