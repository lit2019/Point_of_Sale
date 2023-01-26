package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "pos_inventory", uniqueConstraints = {@UniqueConstraint(columnNames = {"productId"})})

public class InventoryPojo extends BaseEntity {
    @Id
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
