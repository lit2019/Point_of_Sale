package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.*;

@Getter
@Setter
@Entity
@Table(name = "pos_products", uniqueConstraints = {@UniqueConstraint(columnNames = {"barcode"})})

public class ProductPojo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_GENERATOR_NAME)
    @TableGenerator(name = TABLE_GENERATOR_NAME, table = TABLE_NAME, allocationSize = 1, pkColumnName = PK_COLUMN_NAME, valueColumnName = PK_COLUMN_VALUE)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, name = "brand_category_id")
    private Integer brandCategoryId;
    @Column(nullable = false)
    private Double mrp;

    //    TODO: use unique constraints annotation in class
    @Column(nullable = false, unique = true)
    private String barcode;
}
