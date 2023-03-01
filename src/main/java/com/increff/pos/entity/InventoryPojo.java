package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "pos_inventory", uniqueConstraints = {@UniqueConstraint(name = "product_id", columnNames = {"product_id"})})

public class InventoryPojo extends BaseEntity {

    @Id
    @Column(name = "product_id")
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
