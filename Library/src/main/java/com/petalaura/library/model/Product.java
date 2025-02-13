package com.petalaura.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="products", uniqueConstraints = @UniqueConstraint(columnNames = {"name","image"}))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long id;
    private String name;
    private String description;
    private String long_description;
    private int currentQuantity;
    private double costPrice;
    private double salePrice;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="category_id",referencedColumnName = "category_id")
    private Category category;
    private boolean is_activated;
    private boolean is_deleted;
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "product" ,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    private List<Image> images;
}