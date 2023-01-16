package com.increff.pos.entity;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "pos_brands")
public class BrandPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
}
