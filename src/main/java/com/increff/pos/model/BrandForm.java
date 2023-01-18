package com.increff.pos.model;

import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class BrandForm {
    @Nullable
    private String name;
    @NonNull
    private String Category;

}