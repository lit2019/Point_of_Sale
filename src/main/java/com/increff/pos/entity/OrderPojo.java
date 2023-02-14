package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.POS_ORDER_SEQ;
import static com.increff.pos.entity.TableConstants.POS_SEQ_TABLE_NAME;

@Getter
@Setter
@Entity
@Table(
        name = "pos_orders"
)
public class OrderPojo extends BaseEntity {
    @Id
    @TableGenerator(name = POS_ORDER_SEQ, pkColumnValue = POS_ORDER_SEQ, table = POS_SEQ_TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = POS_ORDER_SEQ)
    private Integer id;

    @Column(name = "invoiced_status")
    private Boolean InvoicedStatus;
}
