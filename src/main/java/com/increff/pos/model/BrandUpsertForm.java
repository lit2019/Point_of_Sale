package com.increff.pos.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class BrandUpsertForm {
    @NonNull
    private String name;
    @NonNull
    private String category;
    @NonNull
    private Integer id;
}