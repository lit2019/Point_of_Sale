package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.SEQ_OUTWARD_ORDER;

@Getter
@Setter
@Entity
@Table(name = "pos_products")
public class ProductPojo {
    @Id
    @TableGenerator(name = SEQ_OUTWARD_ORDER, pkColumnValue = SEQ_OUTWARD_ORDER)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = SEQ_OUTWARD_ORDER)
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
