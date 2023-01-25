package com.increff.pos.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ProductData extends ProductUpsertForm {

    @NonNull
    Integer id;
}
