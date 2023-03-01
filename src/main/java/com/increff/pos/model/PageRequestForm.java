package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class PageRequestForm {
    @Positive(message = "Page No. must be Positive")
    private Integer pageNo;
    @Positive(message = "Page Size must be Positive")
    private Integer pageSize;

}
