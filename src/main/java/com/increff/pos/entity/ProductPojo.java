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
    @GeneratedValue(strategy= GenerationType.TABLE,
            generator = "products-table-generator")
    @TableGenerator(name = "products-table-generator",
            table = "product_ids",
            pkColumnName = "seq_id",
            valueColumnName = "seq_value")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, name = "brand_category_id")
    private Integer brandCategoryId;
    @Column(nullable = false)
    private Double mrp;
    @Column(nullable = false, unique = true)
    private String barcode;
}
