package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static com.increff.pos.entity.TableConstants.POS_DAILY_SALES_SEQ;
import static com.increff.pos.entity.TableConstants.POS_SEQ_TABLE_NAME;

@Getter
@Setter
@Entity
@Table(name = "pos_daily_sales", uniqueConstraints = {@UniqueConstraint(name = "date", columnNames = {"date"})})
public class DailySalesPojo extends BaseEntity {
    @Id
    @TableGenerator(name = POS_DAILY_SALES_SEQ, pkColumnValue = POS_DAILY_SALES_SEQ, table = POS_SEQ_TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = POS_DAILY_SALES_SEQ)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime date;

    @Column(nullable = false, name = "invoiced_orders_count")
    private Integer invoicedOrdersCount;

    @Column(nullable = false, name = "invoiced_items_count")
    private Integer invoicedItemsCount;

    @Column(nullable = false, name = "total_revenue")
    private Double totalRevenue;
}
