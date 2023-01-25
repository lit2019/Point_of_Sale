package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
public class BrandUpsertForm {
    @NotBlank(message = "brand name cannot be null")
    private String name;
    @NotBlank(message = "category cannot be null")
    private String category;
}