package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.POS_PRODUCT_SEQ;
import static com.increff.pos.entity.TableConstants.POS_SEQ_TABLE_NAME;

@Getter
@Setter
@Entity
@Table(name = "pos_products", uniqueConstraints = {@UniqueConstraint(name = "barcode", columnNames = {"barcode"})})

public class ProductPojo extends BaseEntity {
    @Id
    @TableGenerator(name = POS_PRODUCT_SEQ, pkColumnValue = POS_PRODUCT_SEQ, table = POS_SEQ_TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = POS_PRODUCT_SEQ)
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
