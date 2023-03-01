package com.increff.pos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.increff.pos.util.CustomZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderFilterForm extends PageRequestForm {
    @NotNull(message = "End Date cannot be null")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime endDate;
    @NotNull(message = "Start Date cannot be null")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime startDate;

    private OrderStatus orderStatus;

    private Integer orderId;

}
