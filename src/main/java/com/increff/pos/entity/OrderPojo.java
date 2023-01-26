package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.*;

@Getter
@Setter
@Entity
@Table(
        name = "pos_orders"
)
public class OrderPojo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_GENERATOR_NAME)
    @TableGenerator(name = TABLE_GENERATOR_NAME, table = TABLE_NAME, allocationSize = 1, pkColumnName = PK_COLUMN_NAME, valueColumnName = PK_COLUMN_VALUE)
    private Integer id;
}
