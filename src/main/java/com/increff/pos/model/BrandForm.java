package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
//TODO rename to BrandForm
public class BrandForm {
    @NotBlank(message = "Brand name cannot be blank")
    private String name;
    @NotBlank(message = "Category cannot be blank")
    private String category;
}