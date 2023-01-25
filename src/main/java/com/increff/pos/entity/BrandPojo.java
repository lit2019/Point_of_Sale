package com.increff.pos.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.SEQ_OUTWARD_ORDER;

@Getter
@Setter
@Entity
@Table(
        name = "pos_brands",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "category"})}
)
public class BrandPojo {
    //    TODO: make class table constant to access sequence generator, name should be seq_brands use same table for all the pojos
    @Id
    @TableGenerator(name = SEQ_OUTWARD_ORDER, pkColumnValue = SEQ_OUTWARD_ORDER)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = SEQ_OUTWARD_ORDER)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
}
