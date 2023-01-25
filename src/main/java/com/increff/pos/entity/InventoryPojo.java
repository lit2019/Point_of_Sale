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
@Table(name = "pos_inventory")
public class InventoryPojo {
    @Id
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
