package com.increff.pos.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandData extends BrandUpsertForm {
    @NotNull
    private Integer id;
}
