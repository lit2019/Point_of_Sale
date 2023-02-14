package com.increff.pos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.increff.pos.util.CustomZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesFilterForm {
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime startDate;
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime endDate;
    private String brandName;
    private String category;
}