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
//    TODO: use generation type table here
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
    generator = "brands-table-generator")
    @TableGenerator(name = "brands-table-generator",
            table = "brand_ids",
            pkColumnName = "seq_id",
            valueColumnName = "seq_value")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
}
