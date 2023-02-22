package com.increff.pos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.increff.pos.util.CustomZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DailySalesFilterForm {
    @NotNull(message = "end date cannot be null")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime endDate;
    @NotNull(message = "start date cannot be null")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime startDate;
}
