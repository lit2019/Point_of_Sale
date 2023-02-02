package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesFilterForm {
    private String startDate;
    private String endDate;
    private String brandName;
    private String category;
}