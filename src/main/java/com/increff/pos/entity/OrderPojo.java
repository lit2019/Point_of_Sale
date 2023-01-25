package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.SEQ_OUTWARD_ORDER;

@Getter
@Setter
@Entity
@Table(
        name = "pos_orders"
)
public class OrderPojo {
    @Id
    @TableGenerator(name = SEQ_OUTWARD_ORDER, pkColumnValue = SEQ_OUTWARD_ORDER)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = SEQ_OUTWARD_ORDER)
    private Integer id;
    private long time;
}
