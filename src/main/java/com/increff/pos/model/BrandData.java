package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandData extends BrandForm {
    //TODO Remove javax validation constraints from data class
    private Integer id;
}
