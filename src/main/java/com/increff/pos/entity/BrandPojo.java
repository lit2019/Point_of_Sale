package com.increff.pos.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.POS_BRAND_SEQ;
import static com.increff.pos.entity.TableConstants.POS_SEQ_TABLE_NAME;

@Getter
@Setter
@Entity
@Table(
        name = "pos_brands",
        uniqueConstraints = {@UniqueConstraint(name = "name_category", columnNames = {"name", "category"})}
)
public class BrandPojo extends BaseEntity {
    @Id
    @TableGenerator(name = POS_BRAND_SEQ, pkColumnValue = POS_BRAND_SEQ, table = POS_SEQ_TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = POS_BRAND_SEQ)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
}
