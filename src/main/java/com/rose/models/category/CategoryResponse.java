package com.rose.models.category;

import com.rose.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private String categoryName;
    private String categoryCode;
    private String status;
    private Integer totalProduct;

    public CategoryResponse(Category category){
        this.categoryName = category.getCategoryName();
        this.categoryCode = category.getCategoryCode();
        this.status = category.isActive() ? "Active" : "Inactive";
        this.totalProduct = category.getProducts().size();
    }
}
