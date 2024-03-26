package com.rose.models.discount;

import com.rose.entities.Discount;
import com.rose.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * A DTO for the {@link com.rose.entities.Discount} entity
 */
@Data
public class DiscountDto implements Serializable {
    private String discountCode;
    private String description;
    private Boolean active;
    private Integer totalDiscountUsed;
    private Double totalExpense;
    private Integer discountPercent;
    private Date createDate;
    private Date startTime;
    private Date endTime;
    private Set<Product> products;

    public DiscountDto(Discount discount){
        this.discountCode = discount.getDiscountCode();
        this.description = discount.getDescription();
        this.active = discount.getActive();
        this.totalDiscountUsed = discount.getTotalDiscountUsed();
        this.discountPercent = discount.getDiscountPercent();
        this.createDate = discount.getCreateDate();
        this.startTime = discount.getStartTime();
        this.endTime = discount.getEndTime();
        this.products = discount.getProducts();
    }
}