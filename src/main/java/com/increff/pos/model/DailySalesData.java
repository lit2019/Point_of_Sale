package com.increff.pos.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.increff.pos.util.CustomZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class DailySalesData {
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    private ZonedDateTime date;
    private Integer invoicedOrdersCount;
    private Integer invoicedItemsCount;
    private Double totalRevenue;
}
