package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static com.increff.pos.entity.TableConstants.POS_ORDER_ITEM_SEQ;
import static com.increff.pos.entity.TableConstants.POS_SEQ_TABLE_NAME;

@Getter
@Setter
@Entity
//TODO rename to pos_order_items
@Table(name = "pos_order_items", uniqueConstraints = {@UniqueConstraint(name = "order_id_product_id", columnNames = {"order_id", "product_id"})})
public class OrderItemPojo extends BaseEntity {
    @Id
    @TableGenerator(name = POS_ORDER_ITEM_SEQ, pkColumnValue = POS_ORDER_ITEM_SEQ, table = POS_SEQ_TABLE_NAME)
//    TODO use diffferent table generators
//    TODO make unique constrain for prodId and order id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = POS_ORDER_ITEM_SEQ)
    private Integer id;

    @Column(nullable = false, name = "order_id")
    private Integer orderId;

    @Column(nullable = false, name = "product_id")
    private Integer productId;

    @Column(nullable = false, name = "quantity")
    private Integer quantity;

    @Column(nullable = false, name = "selling_price")
    private Double sellingPrice;

}
