package com.rose.models.product;

import com.rose.entities.Product;
import com.rose.entities.ProductSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link Product} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {
    @NotBlank
    @Size(min = 2, max = 10)
    private String productCode;
    @NotBlank
    @Size(min = 3, max = 50)
    private String productName;
    private Boolean available;
    private String description;

    private Integer sold;

    private Double avgRating;

    private Double revenue;
    @URL
    private String imageUrl;
    @PositiveOrZero
    private Double commonPrice;

    private Integer discountPercent;
    private Integer totalDiscountUsed;
    private Double totalExpense;

    public ProductDto(Product product) {
        this.productCode = product.getProductCode();
        this.productName = product.getProductName();
        this.available = product.getAvailable();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.commonPrice = product.getCommonPrice();
        this.sold = product.getSold();
        this.avgRating = product.getAvgRate();
        this.revenue = product.getRevenue();
        this.discountPercent = product.getDiscount() == null ? 0 : product.getDiscount().getDiscountPercent();
        this.totalDiscountUsed = product.getDiscount() == null ? 0 : product.getDiscount().getTotalDiscountUsed();
        this.totalExpense = product.getDiscount() == null ? 0.0 : product.getDiscount().getTotalExpense();
    }
    public ProductDto(ProductSearch product) {
        this.productCode = product.getCode();
        this.productName = product.getName();
        this.available = product.getAvailable();
        this.description = product.getDescription();
        this.imageUrl = product.getImage();
        this.commonPrice = product.getPrice();
        this.sold = product.getSold();
        this.avgRating = product.getAvgRating();
        this.revenue = product.getRevenue();
    }
}