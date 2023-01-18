package com.increff.pos.entity;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "pos_brands",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "category"})}
)
//TODO: use Unique Constraint
public class BrandPojo {
    @Id
//    TODO: use generation type table here
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
}
