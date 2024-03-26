package com.rose.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Categories")
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Serializable {
    @Id
    @Column(columnDefinition = "VARCHAR(10)", unique = true, updatable = false)
    private String categoryCode;
    @Column(columnDefinition = "NVARCHAR(50)", unique = true)
    private String categoryName;
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  name = "category_products",
            joinColumns = @JoinColumn(name = "category_code"),
            inverseJoinColumns = @JoinColumn(name = "product_code"))
    private Set<Product> products = new HashSet<>();

    public Category(String categoryCode, String categoryName, Boolean active) {
        this.categoryCode = categoryCode;
        this.categoryName =  categoryName;
        this.active = active;
    }

}
