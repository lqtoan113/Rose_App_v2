package com.rose.models.category;

import com.rose.entities.Category;
import com.rose.models.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link Category} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest implements Serializable {
    @NotBlank
    private String categoryCode;
    @NotBlank
    private String categoryName;
    private boolean active;
    private Set<String> setProductCode;
}