package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "pos_products")
public class ProductPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, name = "brand_category_id")
    private Integer brandCategoryId;
    @Column(nullable = false)
    private Double mrp;
    @Column(nullable = false)
    private String barcode;


}
