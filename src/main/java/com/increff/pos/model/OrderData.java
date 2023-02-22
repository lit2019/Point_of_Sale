package com.increff.pos.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.increff.pos.util.CustomZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderData extends OrderForm {
    private Integer id;

    //TODO ZonedDateTime
    @JsonSerialize(using = CustomZonedDateTimeSerializer.class)
    private ZonedDateTime createdAt;

    private OrderStatus orderStatus;

}
