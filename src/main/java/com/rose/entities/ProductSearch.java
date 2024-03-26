package com.rose.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;


@Data
@Document(indexName = "product")
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearch implements Serializable {
    @Id
    private String code;
    private String name;
    private Boolean available;
    private String description;
    private String image;
    private Double avgRating;
    private Integer sold;
    private Double revenue;
    private Double price;

    public ProductSearch(Product product) {
        this.code = product.getProductCode();
        this.name = product.getProductName();
        this.description = product.getDescription();
        this.price = product.getCommonPrice();
        this.available = product.getAvailable();
        this.image = product.getImageUrl();
        this.revenue = product.getRevenue();
        this.sold = product.getSold();
        this.avgRating = product.getAvgRate();
    }
}
